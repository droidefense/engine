package droidefense.emulator.machine.base.struct.fake;

import droidefense.emulator.machine.base.struct.generic.IDroidefenseField;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.emulator.machine.base.DynamicUtils;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseClass;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.sdk.util.InternalConstant;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Created by r00t on 25/10/15.
 */
public class EncapsulatedClass extends IDroidefenseClass implements Serializable {

    //encapsulated vars
    protected String name;
    private int flag;
    private boolean isInterface;
    private String superClass;
    private String[] interfaces;
    private IDroidefenseField[] instanceFields;
    private IDroidefenseField[] staticFields;
    private Hashtable staticFieldMap;
    private IDroidefenseMethod[] directMethods;
    private IDroidefenseMethod[] virtualMethods;
    private boolean binded;
    private Class<?> aClass;
    private Object javaObject;

    public EncapsulatedClass(String name) {
        super();
        this.name = name;
        this.setSuperClass(InternalConstant.SUPERCLASS);
        javaObject = null;
    }

    protected IDroidefenseMethod searchMethod(String name, String desc, Object[] lastMethodArgs, boolean getRealMethod) {
        if (getRealMethod) {
            Class[] params = DynamicUtils.getParamsClasses(desc);
            try {
                if (name.equals("<init>")) {
                    Object instance = aClass.getConstructor(params).newInstance(lastMethodArgs);
                    this.setJavaObject(instance);
                    DVMTaintMethod tmethod = new DVMTaintMethod(name, this.getName());
                    tmethod.setMethodReturn(instance);
                    tmethod.setDescriptor(desc);
                    tmethod.setReflected(true);
                    return tmethod;
                }
                Method method = aClass.getDeclaredMethod(name, params);
                DVMTaintMethod tmethod = new DVMTaintMethod(name, this.getName());
                //remove instance from args
                Object[] args = new Object[lastMethodArgs.length - 1];
                for (int i = 1; i <= params.length; i++)
                    args[i - 1] = ((DVMTaintMethod) lastMethodArgs[i]).getMethodReturn();
                tmethod.setMethodReturn(method.invoke(javaObject, args));
                return tmethod;
            } catch (NoSuchMethodException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            } catch (InvocationTargetException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            } catch (IllegalArgumentException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            } catch (InstantiationException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            }
            return new DVMTaintMethod(name, this.getName());
        } else {
            DVMTaintMethod tmethod = new DVMTaintMethod(name, desc);
            return tmethod;
        }
    }

    @Override
    public IDroidefenseMethod getDirectMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IDroidefenseField getStaticField(String name) {
        return (IDroidefenseField) getStaticFieldMap().get(name);
    }

    @Override
    public IDroidefenseMethod getMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IDroidefenseMethod[] getMethod(String name) {
        return new IDroidefenseMethod[0];
    }

    @Override
    public IDroidefenseField getField(String fieldName, String fieldType) {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Override
    public IDroidefenseMethod[] getAllMethods() {
        //encapsulated class must not be analyzed because they are part of sdk
        return new IDroidefenseMethod[0];
    }

    @Override
    public void addMethod(IDroidefenseMethod methodToCall) {
    }

    @Override
    public IDroidefenseMethod[] getDirectMethods() {
        return new IDroidefenseMethod[0];
    }

    public void setDirectMethods(IDroidefenseMethod[] directMethods) {
        this.directMethods = directMethods;
    }

    public IDroidefenseMethod getVirtualMethod(String name, String descriptor, Object[] args, boolean getRealMethod) {
        return searchMethod(name, descriptor, args, getRealMethod);
    }

    @Override
    public IDroidefenseMethod getVirtualMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IDroidefenseMethod[] getVirtualMethods() {
        //Collection<TaintedMethod> list = taintedMethods.values();
        //return list.toArray(new IDroidefenseMethod[list.size()]);
        return null;
    }

    public void setVirtualMethods(IDroidefenseMethod[] virtualMethods) {
        this.virtualMethods = virtualMethods;
    }

    // GETTERS & SETTERS
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public boolean isReflected() {
        return true;
    }

    //GETTERS AND SETTERS

    public Object getJavaObject() {
        return javaObject;
    }

    public void setJavaObject(Object javaObject) {
        this.javaObject = javaObject;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean anInterface) {
        isInterface = anInterface;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public IDroidefenseField[] getInstanceFields() {
        return instanceFields;
    }

    public void setInstanceFields(IDroidefenseField[] instanceFields) {
        this.instanceFields = instanceFields;
    }

    public IDroidefenseField[] getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(IDroidefenseField[] staticFields) {
        this.staticFields = staticFields;
    }

    public Hashtable getStaticFieldMap() {
        return staticFieldMap;
    }

    public void setStaticFieldMap(Hashtable staticFieldMap) {
        this.staticFieldMap = staticFieldMap;
    }

    public boolean isBinded() {
        return binded;
    }

    public void setBinded(boolean binded) {
        this.binded = binded;
    }

    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    @Override
    public String toString() {
        return "class " + getName();
    }
}