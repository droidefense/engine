package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DynamicUtils;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.reader.DexClassReader;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class DVMClass implements IAtomClass, Serializable {

    @JsonIgnore
    protected int flag;
    @JsonIgnore
    protected String name;
    @JsonIgnore
    protected String superClass;
    @JsonIgnore
    private boolean isInterface;

    //inherited superclass
    @JsonIgnore
    private String[] interfaces;

    @JsonIgnore
    private IAtomField[] instanceFields;
    @JsonIgnore
    private IAtomField[] staticFields;
    @JsonIgnore
    private Hashtable staticFieldMap;
    @JsonIgnore
    private IAtomMethod[] directMethods;
    @JsonIgnore
    private IAtomMethod[] virtualMethods;

    private boolean binded;

    public DVMClass() {
        super();
        interfaces = new String[0];
        instanceFields = new IAtomField[0];
        staticFields = new IAtomField[0];
        staticFieldMap = new Hashtable();

        directMethods = new IAtomMethod[0];
        virtualMethods = new IAtomMethod[0];
    }

    public IAtomMethod getDirectMethod(final String name, final String descriptor, boolean getRealMethod) {
        IAtomMethod[] currentMethods = getDirectMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IAtomMethod method = currentMethods[i];
            if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                return method;
            }
        }
        /*
        this code was not in original currentProject
        IAtomMethod method;
        String supercls = this.getSuperClass();
        do {
            IAtomClass cls = DexClassReader.getInstance().load(supercls);
            method = cls.getDirectMethod(name, descriptor);
            supercls = cls.getSuperClass();
        } while (method != null && !supercls.equals(ApkrConstants.SUPERCLASS));
        return method;
        */
        return null;
    }

    @Override
    public IAtomField getStaticField(String name) {
        return (IAtomField) getStaticFieldMap().get(name);
    }

    public IAtomMethod getMethod(final String name, final String descriptor, boolean getRealMethod) {
        IAtomMethod[] currentMethods = getAllMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IAtomMethod method = currentMethods[i];
            if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                return method;
            }
        }
        if (!getSuperClass().equalsIgnoreCase(ApkrConstants.SUPERCLASS)) {
            IAtomClass c = DexClassReader.getInstance().load(getSuperClass());
            return c.getMethod(name, descriptor, getRealMethod);
        }
        return null;
    }

    @Override
    public IAtomMethod[] getMethod(String name) {

        ArrayList<IAtomMethod> list = new ArrayList<>();
        IAtomMethod[] currentMethods = getAllMethods();
        for (int i = 0, length = currentMethods.length; i < length; i++) {
            IAtomMethod method = currentMethods[i];
            if (method.getName().equals(name)) {
                list.add(method);
            }
        }
        return list.toArray(new IAtomMethod[list.size()]);
    }

    @Override
    public IAtomField getField(String fieldName, String fieldType) {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    public IAtomMethod[] getDirectMethods() {
        if (directMethods == null)
            return new IAtomMethod[0];
        return directMethods;
    }

    @Override
    public void setDirectMethods(IAtomMethod[] directMethods) {
        this.directMethods = directMethods;
    }

    public IAtomMethod getVirtualMethod(final String name, final String descriptor, boolean getRealMethod) {
        //TODO check this endlesss loop in some conditions
        IAtomClass current = this;
        do {
            IAtomMethod[] currentMethods = current.getVirtualMethods();
            for (int i = 0, length = currentMethods.length; i < length; i++) {
                IAtomMethod method = currentMethods[i];
                if (name.equals(method.getName()) && descriptor.equals(method.getDescriptor())) {
                    return method;
                }
            }
            current = DexClassReader.getInstance().load(current.getSuperClass());
        } while (current != null /*&& !current.getSuperClass().equals(ApkrConstants.SUPERCLASS)*/);
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
    public IAtomMethod[] getAllMethods() {
        return DynamicUtils.concat(getDirectMethods(), getVirtualMethods());
    }

    @Override
    public void addMethod(IAtomMethod methodToCall) {
    }

    //GETTERS AND SETTERS

    public IAtomField getInstanceField(String fieldName, String fieldType) {
        IAtomField[] list = getInstanceFields();
        for (IAtomField f : list) {
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
    public IAtomField[] getInstanceFields() {
        return instanceFields;
    }

    @Override
    public void setInstanceFields(IAtomField[] instanceFields) {
        this.instanceFields = instanceFields;
    }

    @Override
    public IAtomField[] getStaticFields() {
        return staticFields;
    }

    @Override
    public void setStaticFields(IAtomField[] staticFields) {
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
    public IAtomMethod[] getVirtualMethods() {
        return virtualMethods;
    }

    @Override
    public void setVirtualMethods(IAtomMethod[] virtualMethods) {
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

    public IAtomMethod[] getMainMethods() {
        IAtomMethod[] all = this.getAllMethods();
        ArrayList<IAtomMethod> mains = new ArrayList<>();
        for (IAtomMethod dm : all) {
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
        return mains.toArray(new IAtomMethod[mains.size()]);
    }
}
