package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic;

/**
 * Created by sergio on 25/3/16.
 */
public interface IAtomMethod {

    //TO STRING

    String toString();

    //GETTERS AND SETTERS

    IAtomClass getOwnerClass();

    void setOwnerClass(IAtomClass cls);

    int getFlag();

    void setFlag(int flag);

    boolean isInstance();

    void setInstance(boolean instance);

    boolean isSynchronized();

    void setSynchronized(boolean aSynchronized);

    String getName();

    void setName(String name);

    String getDescriptor();

    void setDescriptor(String descriptor);

    int getStackSize();

    void setStackSize(int stackSize);

    int getVariableSize();

    void setVariableSize(int variableSize);

    byte[] getByteCode();

    void setByteCode(byte[] byteCode);

    int[] getExceptionPositions();

    void setExceptionPositions(int[] exceptionPositions);

    String[] getExceptionClasses();

    void setExceptionClasses(String[] exceptionClasses);

    int getRegisterCount();

    void setRegisterCount(int registerCount);

    int getIncomingArgumentCount();

    void setIncomingArgumentCount(int incomingArgumentCount);

    int getOutgoingArgumentCount();

    void setOutgoingArgumentCount(int outgoingArgumentCount);

    int[] getOpcodes();

    void setOpcodes(int[] opcodes);

    int[] getRegistercodes();

    void setRegistercodes(int[] registercodes);

    int[] getIndex();

    void setIndex(int[] index);

    String[] getStrings();

    void setStrings(String[] strings);

    String[] getTypes();

    void setTypes(String[] types);

    String[] getDescriptors();

    void setDescriptors(String[] descriptors);

    String[] getFieldClasses();

    void setFieldClasses(String[] fieldClasses);

    String[] getFieldTypes();

    void setFieldTypes(String[] fieldTypes);

    String[] getFieldNames();

    void setFieldNames(String[] fieldNames);

    String[] getMethodClasses();

    void setMethodClasses(String[] methodClasses);

    String[] getMethodTypes();

    void setMethodTypes(String[] methodTypes);

    String[] getMethodNames();

    void setMethodNames(String[] methodNames);

    int[] getExceptionStartAddresses();

    void setExceptionStartAddresses(int[] exceptionStartAddresses);

    int[] getExceptionEndAdresses();

    void setExceptionEndAdresses(int[] exceptionEndAdresses);

    int[] getExceptionHandlerIndexes();

    void setExceptionHandlerIndexes(int[] exceptionHandlerIndexes);

    String[][] getExceptionHandlerTypes();

    void setExceptionHandlerTypes(String[][] exceptionHandlerTypes);

    int[][] getExceptionHandlerAddresses();

    void setExceptionHandlerAddresses(int[][] exceptionHandlerAddresses);

    String getReturnType();

    boolean isFake();
}
