package axmlparser.org.xmlpull.v1;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class XmlPullParserFactory {

    public static final String PROPERTY_NAME = "org.xmlpull.v1.XmlPullParserFactory";
    static final Class referenceContextClass;
    private static final String RESOURCE_NAME = "/META-INF/services/org.xmlpull.v1.XmlPullParserFactory";

    static {
        XmlPullParserFactory f = new XmlPullParserFactory();
        referenceContextClass = f.getClass();
    }

    protected Vector parserClasses;
    protected String classNamesLocation;
    protected Vector serializerClasses;
    protected Hashtable features = new Hashtable();

    public static XmlPullParserFactory newInstance() throws XmlPullParserException {
        return newInstance((String) null, (Class) null);
    }

    public static XmlPullParserFactory newInstance(String classNames, Class context) throws XmlPullParserException {
        if (context == null) {
            context = referenceContextClass;
        }

        String classNamesLocation = null;
        if (classNames != null && classNames.length() != 0 && !"DEFAULT".equals(classNames)) {
            classNamesLocation = "parameter classNames to newInstance() that contained \'" + classNames + "\'";
        } else {
            try {
                InputStream factory = context.getResourceAsStream("/META-INF/services/org.xmlpull.v1.XmlPullParserFactory");
                if (factory == null) {
                    throw new XmlPullParserException("resource not found: /META-INF/services/org.xmlpull.v1.XmlPullParserFactory make sure that parser implementing XmlPull API is available");
                }

                StringBuffer parserClasses = new StringBuffer();

                while (true) {
                    int serializerClasses = factory.read();
                    if (serializerClasses < 0) {
                        factory.close();
                        classNames = parserClasses.toString();
                        break;
                    }

                    if (serializerClasses > 32) {
                        parserClasses.append((char) serializerClasses);
                    }
                }
            } catch (Exception var13) {
                throw new XmlPullParserException((String) null, (XmlPullParser) null, var13);
            }

            classNamesLocation = "resource /META-INF/services/org.xmlpull.v1.XmlPullParserFactory that contained \'" + classNames + "\'";
        }

        XmlPullParserFactory factory1 = null;
        Vector parserClasses1 = new Vector();
        Vector serializerClasses1 = new Vector();

        int cut;
        for (int pos = 0; pos < classNames.length(); pos = cut + 1) {
            cut = classNames.indexOf(44, pos);
            if (cut == -1) {
                cut = classNames.length();
            }

            String name = classNames.substring(pos, cut);
            Class candidate = null;
            Object instance = null;

            try {
                candidate = Class.forName(name);
                instance = candidate.newInstance();
            } catch (Exception var12) {
                var12.printStackTrace();
            }

            if (candidate != null) {
                boolean recognized = false;
                if (instance instanceof XmlPullParser) {
                    parserClasses1.addElement(candidate);
                    recognized = true;
                }

                if (instance instanceof XmlSerializer) {
                    serializerClasses1.addElement(candidate);
                    recognized = true;
                }

                if (instance instanceof XmlPullParserFactory) {
                    if (factory1 == null) {
                        factory1 = (XmlPullParserFactory) instance;
                    }

                    recognized = true;
                }

                if (!recognized) {
                    throw new XmlPullParserException("incompatible class: " + name);
                }
            }
        }

        if (factory1 == null) {
            factory1 = new XmlPullParserFactory();
        }

        factory1.parserClasses = parserClasses1;
        factory1.serializerClasses = serializerClasses1;
        factory1.classNamesLocation = classNamesLocation;
        return factory1;
    }

    public void setFeature(String name, boolean state) throws XmlPullParserException {
        this.features.put(name, Boolean.valueOf(state));
    }

    public boolean getFeature(String name) {
        Boolean value = (Boolean) this.features.get(name);
        return value != null && value.booleanValue();
    }

    public boolean isNamespaceAware() {
        return this.getFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
    }

    public void setNamespaceAware(boolean awareness) {
        this.features.put("http://xmlpull.org/v1/doc/features.html#process-namespaces", Boolean.valueOf(awareness));
    }

    public boolean isValidating() {
        return this.getFeature("http://xmlpull.org/v1/doc/features.html#validation");
    }

    public void setValidating(boolean validating) {
        this.features.put("http://xmlpull.org/v1/doc/features.html#validation", Boolean.valueOf(validating));
    }

    public XmlPullParser newPullParser() throws XmlPullParserException {
        if (this.parserClasses == null) {
            throw new XmlPullParserException("Factory initialization was incomplete - has not tried " + this.classNamesLocation);
        } else if (this.parserClasses.size() == 0) {
            throw new XmlPullParserException("No valid parser classes found in " + this.classNamesLocation);
        } else {
            StringBuffer issues = new StringBuffer();
            int i = 0;

            while (i < this.parserClasses.size()) {
                Class ppClass = (Class) this.parserClasses.elementAt(i);

                try {
                    XmlPullParser ex = (XmlPullParser) ppClass.newInstance();
                    Enumeration e = this.features.keys();

                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        Boolean value = (Boolean) this.features.get(key);
                        if (value != null && value.booleanValue()) {
                            ex.setFeature(key, true);
                        }
                    }

                    return ex;
                } catch (Exception var8) {
                    issues.append(ppClass.getName()).append(": ").append(var8.toString()).append("; ");
                    ++i;
                }
            }

            throw new XmlPullParserException("could not create parser: " + issues);
        }
    }

    public XmlSerializer newSerializer() throws XmlPullParserException {
        if (this.serializerClasses == null) {
            throw new XmlPullParserException("Factory initialization incomplete - has not tried " + this.classNamesLocation);
        } else if (this.serializerClasses.size() == 0) {
            throw new XmlPullParserException("No valid serializer classes found in " + this.classNamesLocation);
        } else {
            StringBuffer issues = new StringBuffer();
            int i = 0;

            while (i < this.serializerClasses.size()) {
                Class ppClass = (Class) this.serializerClasses.elementAt(i);

                try {
                    return (XmlSerializer) ppClass.newInstance();
                } catch (Exception var5) {
                    issues.append(ppClass.getName()).append(": ").append(var5.toString()).append("; ");
                    ++i;
                }
            }

            throw new XmlPullParserException("could not create serializer: " + issues);
        }
    }
}
