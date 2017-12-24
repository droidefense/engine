package droidefense.xmodel.base;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.manifest.*;
import droidefense.sdk.manifest.base.AbstractManifestClass;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by sergio on 4/3/16.
 */
public final class ManifestParser implements Serializable {

    private static final int MAX_ANDROID_VERSION = 23;
    private static final double JDK_LANGUAGE_LEVEL = 1.8;
    private static final boolean DEBUG = false;
    //temp vars
    private final Stack<AbstractManifestClass> stack = new Stack<>();
    private Manifest manifest;
    private File manifestFile;
    //parent variable
    private AbstractManifestClass parent = null;

    public ManifestParser() {
        Log.write(LoggerType.TRACE,"Running ManifestParser...");
        Log.write(LoggerType.TRACE, "Compiled with JAVA SDK v" + JDK_LANGUAGE_LEVEL);
        Log.write(LoggerType.TRACE,"Max AndroidManifest.xml version supported: " + MAX_ANDROID_VERSION);
    }

    public final AbstractManifestClass getMainClass() {
        //search for main class if exists
        AbstractManifestClass parent = null;
        ArrayList<IntentFilter> allFilters = manifest.getAllFilters();
        for (IntentFilter f : allFilters) {
            ArrayList<Action> action = f.getAction();
            if (f.getParent() != null
                    && f.getParent() instanceof Activity
                    && action != null
                    && action.size() > 0
                    && action.get(0).getName().equals("android.intent.action.MAIN"))
                parent = f.getParent();
            break;
        }
        //return first match
        return parent;
    }

    public final AbstractManifestClass getBootServices() {
        //search for main class if exists
        AbstractManifestClass parent = null;
        ArrayList<IntentFilter> allFilters = manifest.getAllFilters();
        for (IntentFilter f : allFilters) {
            ArrayList<Action> action = f.getAction();
            if (action != null && action.size() > 0 && action.get(0).getName().equals("android.intent.action.BOOT_COMPLETED"))
                parent = f.getParent();
            break;
        }
        //return first match
        return parent;
    }

    public final boolean hasMainClass() {
        return getMainClass() != null;
    }

    public final boolean hasAutoBootServices() {
        return getBootServices() != null;
    }

    public final ArrayList<AbstractManifestClass> getEntryPoints() {
        ArrayList<AbstractManifestClass> list = new ArrayList<>();
        //add main class
        if (hasMainClass())
            list.add(getMainClass());
        //add widgets
        //list.add()
        //add services
        list.addAll(getServices());
        //add receivers
        list.addAll(getReceivers());
        //add providers
        list.addAll(getProviders());
        return list;
    }

    public final ArrayList<Service> getServices() {
        return manifest.getApplication().getServices();
    }

    public final ArrayList<Receiver> getReceivers() {
        return manifest.getApplication().getReceivers();
    }

    public final ArrayList<Provider> getProviders() {
        return manifest.getApplication().getProviders();
    }

    //***** droidefense.sdk.manifest parser utils ******

    public void parse(File f) throws ParserConfigurationException, SAXException, IOException {
        this.manifestFile = f;
        parserCode(new FileInputStream(this.manifestFile));
        if (DEBUG)
            System.out.println("XML parsing done.");
    }

    public void parse(byte[] data) throws ParserConfigurationException, SAXException, IOException {
        parserCode(new ByteArrayInputStream(data));
        if (DEBUG)
            System.out.println("XML parsing done.");
    }

    private void parserCode(InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {

            public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
                if (DEBUG)
                    System.out.println("Start: " + qName);
                try {
                    //get class
                    Class cls = ManifestID.getResolvedObject(qName);
                    //create object
                    AbstractManifestClass readedClass = (AbstractManifestClass) cls.newInstance();
                    //set parent to recovered object
                    readedClass.setParent(parent);
                    //add to stack
                    stack.addElement(readedClass);
                    //update parent
                    parent = readedClass;

                    //get tag info and update object data
                    //data can be: string, int, boolean or enum type
                    int total = attributes.getLength();
                    for (int i = 0; i < total; i++) {
                        String data = attributes.getValue(i);
                        String methodName = ManifestID.getJavaMethodName(attributes, i, data);
                        String tagName = attributes.getLocalName(i);

                        if (data == null || data.trim().isEmpty())
                            data = methodName;
                        DataType type = getDataType(cls, data);
                        if (type != null && data != null && cls != null && readedClass != null && methodName != null)
                            type.parseData(data, cls, readedClass, methodName);
                        else {
                            System.err.println("Error");
                        }
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                    if (DEBUG)
                        System.err.println(e.getLocalizedMessage());
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (DEBUG)
                    System.out.println("End: " + qName);
                //remove from the stack and refresh next parent item
                AbstractManifestClass last = stack.remove(stack.size() - 1);
                if (stack.isEmpty()) {
                    manifest = (Manifest) last;
                } else {
                    parent = stack.get(stack.size() - 1);
                }
            }

        };
        Reader reader = new InputStreamReader(stream, "UTF-8");

        InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
    }

    private DataType getDataType(Class owner, String data) {
        if (data == null || data.trim().isEmpty())
            return null;
        String lower = data.toLowerCase().trim();

        //check if boolean
        if (lower.equals("false") || lower.equals("true"))
            return DataType.BOOLEAN;

        //check if numeric
        try {
            Integer.valueOf(lower);
            return DataType.INT;
        } catch (NumberFormatException e) {
        }

        //check if enum
        if (owner != null) {
            Method[] list = owner.getDeclaredMethods();
            if (list != null) {
                //iterate over each one checking names
                for (Method m : list) {
                    if (m.getName().toLowerCase().contains("set" + lower)) {
                        return DataType.ENUM;
                    }
                }
            }
        }

        //else, is tring
        return DataType.STRING;
    }

    public final Manifest getManifest() {
        return manifest;
    }

    public final ArrayList<UsesPermission> getPermissions() {
        return manifest.getUsesPermissionList();
    }
}
