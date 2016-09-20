package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic;

/**
 * Created by sergio on 25/3/16.
 */
public interface IAtomField {

    String toString();

    IAtomField copy();

    //GETTERS AND SETTERS

    IAtomClass getOwnerClass();

    int getFlag();

    void setFlag(int flag);

    boolean isInstance();

    void setInstance(boolean instance);

    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    int getIntValue();

    void setIntValue(int intValue);

    long getLongValue();

    void setLongValue(long longValue);

    Object getObjectValue();

    void setObjectValue(Object objectValue);
}
