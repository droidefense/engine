package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic;

import java.util.Hashtable;

/**
 * Created by sergio on 25/3/16.
 */
public interface IAtomInstance {

    String toString();

    IAtomField getField(final String className, final String fieldName);

    //GETTERS AND SETTERS

    IAtomClass getOwnerClass();

    Hashtable getFieldsOfClasses();

    Object getParentInstance();

    void setParentInstance(Object parentInstance);
}
