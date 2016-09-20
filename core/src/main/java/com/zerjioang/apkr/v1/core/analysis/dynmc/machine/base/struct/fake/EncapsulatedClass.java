package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.fake;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DynamicUtils;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * Created by r00t on 25/10/15.
 */
@JsonIgnoreProperties(value = {"aClass", "javaObject"})
public class EncapsulatedClass implements IAtomClass, Serializable {

    //encapsulated vars
    protected String name;
    private int flag;
    private boolean isInterface;
    private String superClass;
    private String[] interfaces;
    private IAtomField[] instanceFields;
    private IAtomField[] staticFields;
    private Hashtable staticFieldMap;
    private IAtomMethod[] directMethods;
    private IAtomMethod[] virtualMethods;
    private boolean binded;
    private Class<?> aClass;
    private Object javaObject;

    public EncapsulatedClass(String name) {
        super();
        this.name = name;
        this.setSuperClass(ApkrConstants.SUPERCLASS);
        javaObject = null;
    }

    protected IAtomMethod searchMethod(String name, String desc, Object[] lastMethodArgs, boolean getRealMethod) {
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
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return new DVMTaintMethod(name, this.getName());
        } else {
            DVMTaintMethod tmethod = new DVMTaintMethod(name, desc);
            return tmethod;
        }
    }

    @Override
    public IAtomMethod getDirectMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IAtomField getStaticField(String name) {
        return (IAtomField) getStaticFieldMap().get(name);
    }

    @Override
    public IAtomMethod getMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IAtomMethod[] getMethod(String name) {
        return new IAtomMethod[0];
    }

    @Override
    public IAtomField getField(String fieldName, String fieldType) {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Override
    public IAtomMethod[] getAllMethods() {
        //encapsulated class must not be analyzed because they are part of sdk
        return new IAtomMethod[0];
    }

    @Override
    public void addMethod(IAtomMethod methodToCall) {
    }

    @Override
    public IAtomMethod[] getDirectMethods() {
        return new IAtomMethod[0];
    }

    public void setDirectMethods(IAtomMethod[] directMethods) {
        this.directMethods = directMethods;
    }

    public IAtomMethod getVirtualMethod(String name, String descriptor, Object[] args, boolean getRealMethod) {
        return searchMethod(name, descriptor, args, getRealMethod);
    }

    @Override
    public IAtomMethod getVirtualMethod(String name, String descriptor, boolean getRealMethod) {
        return searchMethod(name, descriptor, null, getRealMethod);
    }

    @Override
    public IAtomMethod[] getVirtualMethods() {
        //Collection<TaintedMethod> list = taintedMethods.values();
        //return list.toArray(new IAtomMethod[list.size()]);
        return null;
    }

    public void setVirtualMethods(IAtomMethod[] virtualMethods) {
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

    public IAtomField[] getInstanceFields() {
        return instanceFields;
    }

    public void setInstanceFields(IAtomField[] instanceFields) {
        this.instanceFields = instanceFields;
    }

    public IAtomField[] getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(IAtomField[] staticFields) {
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