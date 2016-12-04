package droidefense.om.machine.base.struct.generic;

import java.util.Hashtable;

/**
 * Created by sergio on 25/3/16.
 */
public interface IAtomClass {

    String toString();

    IAtomMethod getVirtualMethod(final String name, final String descriptor, boolean getRealMethod);

    IAtomMethod getDirectMethod(final String name, final String descriptor, boolean getRealMethod);

    IAtomField getStaticField(final String name);

    //GETTERS AND SETTERS

    String getName();

    void setName(String name);

    int getFlag();

    void setFlag(int flag);

    boolean isInterface();

    void setInterface(boolean anInterface);

    String getSuperClass();

    void setSuperClass(String superClass);

    String[] getInterfaces();

    void setInterfaces(String[] interfaces);

    IAtomField[] getInstanceFields();

    void setInstanceFields(IAtomField[] instanceFields);

    IAtomField[] getStaticFields();

    void setStaticFields(IAtomField[] staticFields);

    Hashtable getStaticFieldMap();

    void setStaticFieldMap(Hashtable staticFieldMap);

    IAtomMethod[] getDirectMethods();

    void setDirectMethods(IAtomMethod[] directMethods);

    IAtomMethod[] getVirtualMethods();

    void setVirtualMethods(IAtomMethod[] virtualMethods);

    boolean isBinded();

    void setBinded(boolean binded);

    IAtomMethod getMethod(String name, String descriptor, boolean getRealMethod);

    IAtomMethod[] getMethod(String name);

    IAtomField getField(String fieldName, String fieldType);

    boolean isFake();

    IAtomMethod[] getAllMethods();

    void addMethod(IAtomMethod methodToCall);
}
