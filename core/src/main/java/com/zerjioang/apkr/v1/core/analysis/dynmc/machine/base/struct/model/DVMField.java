package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.constants.TypeDescriptorSemantics;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.exceptions.VirtualMachineRuntimeException;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomField;

import java.io.Serializable;

public class DVMField implements IAtomField, Serializable {

    @JsonIgnore
    protected final IAtomClass ownerClass;
    @JsonIgnore
    protected int flag;
    @JsonIgnore
    protected boolean isInstance;
    @JsonIgnore
    protected String name;
    @JsonIgnore
    protected String type;
    @JsonIgnore
    protected int intValue;
    @JsonIgnore
    protected long longValue;

    @JsonIgnore
    protected Object objectValue;

    public DVMField(final IAtomClass cls) {
        this.ownerClass = cls;
    }

    public String toString() {
        String value;
        switch (type.charAt(0)) {
            case TypeDescriptorSemantics.DESC_V:
                //void method. no value
                return ownerClass.getName() + "." + name + " = void";
            case TypeDescriptorSemantics.DESC_F:
                value = (float) intValue + " (float)";
                break;
            case TypeDescriptorSemantics.DESC_D:
                value = (double) intValue + " (double)";
                break;
            case TypeDescriptorSemantics.DESC_C:
                value = (char) intValue + " (char)";
                break;
            case TypeDescriptorSemantics.DESC_B:
                value = (byte) intValue + " (byte)";
                break;
            case TypeDescriptorSemantics.DESC_S:
                value = (short) intValue + " (short)";
                break;
            case TypeDescriptorSemantics.DESC_I:
                value = intValue + " (int)";
                break;
            case TypeDescriptorSemantics.DESC_Z:
                value = (intValue != 0) + " (boolean)";
                break;
            case TypeDescriptorSemantics.DESC_J:
                value = longValue + " (long)";
                break;
            case TypeDescriptorSemantics.DESC_CLASSNAME:
                value = objectValue + " (" + type.substring(1, type.length() - 1) + ")";
                break;
            case TypeDescriptorSemantics.DESC_DESCRIPTOR:
                value = objectValue + " (" + type + ")";
                break;
            default:
                throw new VirtualMachineRuntimeException("not supported field type: " + type);
        }
        return ownerClass.getName() + "." + name + " = " + value;
    }

    public IAtomField copy() {
        IAtomField copy = new DVMField(ownerClass);
        copy.setFlag(flag);
        copy.setName(name);
        copy.setType(type);
        copy.setIntValue(intValue);
        copy.setLongValue(longValue);
        copy.setObjectValue(objectValue);
        return copy;
    }

    //GETTERS AND SETTERS

    public IAtomClass getOwnerClass() {
        return ownerClass;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isInstance() {
        return isInstance;
    }

    public void setInstance(boolean instance) {
        isInstance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(Object objectValue) {
        this.objectValue = objectValue;
    }
}
