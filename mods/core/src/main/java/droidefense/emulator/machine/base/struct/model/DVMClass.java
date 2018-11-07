package droidefense.emulator.machine.base.struct.model;

import droidefense.emulator.machine.base.DynamicUtils;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseClass;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseField;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.emulator.machine.reader.DexClassReader;
import droidefense.sdk.util.InternalConstant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class DVMClass extends IDroidefenseClass implements Serializable {

    protected int flag;

    protected String name;

    protected String superClass;

    private boolean isInterface;

    //inherited superclass

    private String[] interfaces;


    private IDroidefenseField[] instanceFields;

    private IDroidefenseField[] staticFields;

    private Hashtable staticFieldMap;

    private IDroidefenseMethod[] directMethods;

    private IDroidefenseMethod[] virtualMethods;

    private boolean binded;

    public DVMClass() {
        super();
        interfaces = new String[0];
        instanceFields = new IDroidefenseField[0];
        staticFields = new IDroidefenseField[0];
        staticFieldMap = new Hashtable();

        directMethods = new IDroidefenseMethod[0];
        virtualMethods = new IDroidefenseMethod[0];
    }

    public IDroidefenseMethod getDirectMethod(final String name, final String descriptor, boolean getRealMethod) {
        IDroidefenseMethod[] currentMethods = getDirectMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IDroidefenseMethod method = currentMethods[i];
            if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                return method;
            }
        }
        /*
        this code was not in original currentProject
        IDroidefenseMethod method;
        String supercls = this.getSuperClass();
        do {
            IDroidefenseClass cls = DexClassReader.getInstance().load(supercls);
            method = cls.getDirectMethod(name, descriptor);
            supercls = cls.getSuperClass();
        } while (method != null && !supercls.equals(DroidefenseParams.SUPERCLASS));
        return method;
        */
        return null;
    }

    @Override
    public IDroidefenseField getStaticField(String name) {
        return (IDroidefenseField) getStaticFieldMap().get(name);
    }

    public IDroidefenseMethod getMethod(final String name, final String descriptor, boolean getRealMethod) {
        IDroidefenseMethod[] currentMethods = getAllMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IDroidefenseMethod method = currentMethods[i];
            if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                return method;
            }
        }
        if (!getSuperClass().equalsIgnoreCase(InternalConstant.SUPERCLASS)) {
            IDroidefenseClass c = DexClassReader.getInstance().load(getSuperClass());
            return c.getMethod(name, descriptor, getRealMethod);
        }
        return null;
    }

    @Override
    public IDroidefenseMethod[] getMethod(String name) {

        ArrayList<IDroidefenseMethod> list = new ArrayList<>();
        IDroidefenseMethod[] currentMethods = getAllMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IDroidefenseMethod method = currentMethods[i];
            if (method.getName().equals(name)) {
                list.add(method);
            }
        }
        return list.toArray(new IDroidefenseMethod[list.size()]);
    }

    @Override
    public IDroidefenseField getField(String fieldName, String fieldType) {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    public IDroidefenseMethod[] getDirectMethods() {
        if (directMethods == null)
            return new IDroidefenseMethod[0];
        return directMethods;
    }

    @Override
    public void setDirectMethods(IDroidefenseMethod[] directMethods) {
        this.directMethods = directMethods;
    }

    public IDroidefenseMethod getVirtualMethod(final String name, final String descriptor, boolean getRealMethod) {
        //TODO check this endlesss loop in some conditions
        IDroidefenseClass current = this;
        do {
            IDroidefenseMethod[] currentMethods = current.getVirtualMethods();
            for (int i = 0, length = currentMethods.length; i < length; i++) {
                IDroidefenseMethod method = currentMethods[i];
                if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                    return method;
                }
            }
            current = DexClassReader.getInstance().load(current.getSuperClass());
        } while (current != null /*&& !current.getSuperClass().equals(DroidefenseParams.SUPERCLASS)*/);
        //return current.getVirtualMethod(name, descriptor);
        return null;
    }

    public String getBeautyName() {
        return getName().replace("/", ".");
    }

    public String getSuperClassBeautyName() {
        return getSuperClass().replace("/", ".");
    }

    public String getFullClassName() {
        return superClass + "." + name;    //name.replace('/', '.');
    }

    @Override
    public IDroidefenseMethod[] getAllMethods() {
        return DynamicUtils.concat(getDirectMethods(), getVirtualMethods());
    }

    @Override
    public void addMethod(IDroidefenseMethod methodToCall) {
    }

    //GETTERS AND SETTERS

    public IDroidefenseField getInstanceField(String fieldName, String fieldType) {
        IDroidefenseField[] list = getInstanceFields();
        for (IDroidefenseField f : list) {
            if (f.getName().equals(fieldName) && f.getType().equals(fieldType))
                return f;
        }
        return null;
    }

    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSuperClass() {
        return superClass;
    }

    @Override
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    @Override
    public boolean isInterface() {
        return isInterface;
    }

    @Override
    public void setInterface(boolean anInterface) {

    }

    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    @Override
    public String[] getInterfaces() {
        return interfaces;
    }

    @Override
    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public IDroidefenseField[] getInstanceFields() {
        return instanceFields;
    }

    @Override
    public void setInstanceFields(IDroidefenseField[] instanceFields) {
        this.instanceFields = instanceFields;
    }

    @Override
    public IDroidefenseField[] getStaticFields() {
        return staticFields;
    }

    @Override
    public void setStaticFields(IDroidefenseField[] staticFields) {
        this.staticFields = staticFields;
    }

    @Override
    public Hashtable getStaticFieldMap() {
        return staticFieldMap;
    }

    @Override
    public void setStaticFieldMap(Hashtable staticFieldMap) {
        this.staticFieldMap = staticFieldMap;
    }

    @Override
    public IDroidefenseMethod[] getVirtualMethods() {
        return virtualMethods;
    }

    @Override
    public void setVirtualMethods(IDroidefenseMethod[] virtualMethods) {
        this.virtualMethods = virtualMethods;
    }

    @Override
    public boolean isBinded() {
        return binded;
    }

    @Override
    public void setBinded(boolean binded) {
        this.binded = binded;
    }

    @Override
    public String toString() {
        return (isInterface ? "interface " : "class ") + getName();
    }

    public IDroidefenseMethod[] getMainMethods() {
        IDroidefenseMethod[] all = this.getAllMethods();
        ArrayList<IDroidefenseMethod> mains = new ArrayList<>();
        for (IDroidefenseMethod dm : all) {
            if (dm.getName().equals("onCreate") ||
                    dm.getName().equals("onResume") ||
                    dm.getName().equals("onStart") ||
                    dm.getName().equals("onDestroy") ||
                    dm.getName().equals("onResume") ||
                    dm.getName().equals("onBind") ||
                    dm.getName().equals("onStartCommand") ||
                    dm.getName().equals("onCreate")
            ) {
                mains.add(dm);
            }
        }
        return mains.toArray(new IDroidefenseMethod[mains.size()]);
    }
}
