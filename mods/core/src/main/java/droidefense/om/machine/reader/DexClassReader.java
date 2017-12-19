package droidefense.om.machine.reader;

import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.struct.fake.DVMTaintClass;
import droidefense.om.machine.base.struct.fake.DVMTaintField;
import droidefense.om.machine.base.struct.fake.EncapsulatedClass;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.model.base.DroidefenseProject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Hashtable;

public final class DexClassReader implements Serializable {

    //Singleton class
    private static DexClassReader instance;

    private final DalvikVM vm;
    private final DexClassParser parser;
    private final Hashtable<String, IDroidefenseClass> classes = new Hashtable<>();
    private DroidefenseProject currentProject;
    private boolean loaded;

    private DexClassReader(DalvikVM dalvikVM, DroidefenseProject currentProject) {
        super();
        this.vm = dalvikVM;
        this.loaded = false;
        this.currentProject = currentProject;
        this.parser = new DexClassParser(currentProject);
    }

    public static DexClassReader init(DalvikVM dalvikVM, DroidefenseProject currentProject) {
        if (instance == null)
            instance = new DexClassReader(dalvikVM, currentProject);
        return instance;
    }

    public static DexClassReader getInstance() {
        return instance;
    }

    private static boolean hasNoValue(final int value) {
        return value == -1;
    }

    public IDroidefenseClass load(String name) {
        IDroidefenseClass cls;
        name = name.replace(".", "/");
        if (classes.containsKey(name)) {
            //class exist on dex file
            cls = classes.get(name);
        } else {
            cls = findClass(name);
            classes.put(name, cls);
        }
        if (!cls.isBinded() && !cls.isFake()) {
            cls.setBinded(true);
            IAtomMethod clinit = cls.getDirectMethod("<init>", "()V", true);
            if (clinit != null && !clinit.isFake()) {
                //TOdo changed this. may explode
                AbstractDVMThread firstThread = this.vm.getThread(0);
                if (firstThread != null) {
                    IAtomFrame frame = firstThread.pushFrame();
                    frame.init(clinit);
                }
                /*try {
                    loadThread.run();
                } catch (ChangeThreadException e) {
                    // TODO Implement here by checking the behavior of the class loading in The Java Virtual Machine Specification
                } catch (Throwable e) {
                    com.error(e);
                }*/
            }
        }
        return cls;
    }

    private IDroidefenseClass findClass(final String name) {
        //class does not exists on .dex file.
        //check if class belongs to java sdk or to android sdk.
        //anyway, if does not exist, send a fake class

        IDroidefenseClass javaClass = null;
        String cname = name.replace(".", "/");

        //1 try to load cls from java jdk via reflection
        try {
            Class<?> s = Class.forName(name.replace("/", "."));

            //TODO Object[] lastCallArgs = loadThread.getLastMethodArgs();
            Object[] lastCallArgs = null;
            Class<?>[] classes;
            if (name.equals("java/lang/Object")) {
                //Special case. this class ahs no super
                Object newInstance = s.newInstance();
                EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                newClass.setClass(s);
                newClass.setJavaObject(newInstance);
                newClass.setSuperClass(null);
                currentProject.addDexClass(name, newClass);
                return newClass;
            } else if (name.startsWith("java/lang/")) {
                Object newInstance = s.newInstance();
                EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                newClass.setClass(s);
                newClass.setJavaObject(newInstance);
                currentProject.addDexClass(name, newClass);
                return newClass;
            } else if (lastCallArgs == null) {
                Constructor<?> constructor = s.getConstructor();
                if (constructor != null) {
                    Object newInstance = constructor.newInstance();
                    EncapsulatedClass newClass = buildFakeClss(name, newInstance);
                    newClass.setClass(s);
                    newClass.setJavaObject(newInstance);
                    currentProject.addDexClass(name, newClass);
                    return newClass;
                }
            } else {
                classes = new Class[lastCallArgs.length];
                int i = 0;
                for (Object obj : lastCallArgs) {
                    classes[i] = obj.getClass();
                    i++;
                }
                Constructor<?> constructor = s.getConstructor(classes);
                if (constructor != null) {
                    Object newInstance = constructor.newInstance(lastCallArgs);
                    IDroidefenseClass newClass = buildFakeClss(name, newInstance);
                    currentProject.addDexClass(name, newClass);
                    return newClass;
                }
            }
        } catch (ClassNotFoundException e) {
            Log.write(LoggerType.ERROR, "Could not find class on java SDK " + name, e);
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "Error when loading class from java SDK " + name, e);
        }

        //Last option, emulate cls behaviour, emulate it!
        if (cname.contains("$")) {
            String[] data = cname.split("\\$");
            cname = data[0];
            javaClass = new DVMTaintClass(name);
            for (int i = 1; i < data.length; i++) {
                ((DVMTaintClass) javaClass).addDVMTaintField(new DVMTaintField(data[i], javaClass));
            }
            currentProject.addDexClass(name, javaClass);
        } else {
            javaClass = new DVMTaintClass(name);
            currentProject.addDexClass(name, javaClass);
        }

        return javaClass;
    }

    private EncapsulatedClass buildFakeClss(String name, Object newInstance) {
        EncapsulatedClass newClass = new EncapsulatedClass(name);
        newClass.setName(name);
        newClass.setJavaObject(newInstance);
        newClass.setSuperClass(InternalConstant.SUPERCLASS);
        return newClass;
    }

    public void loadClasses(byte[] bytes, boolean multidex) {
        if(!loaded){
            parser.loadClasses(bytes, multidex);
            loaded = true;
        }
    }

    public IDroidefenseClass[] getAllClasses() {
        return parser.getAllClasses();
    }
}
