package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.AbstractDVMThread;

/**
 * Created by sergio on 25/3/16.
 */
public interface IAtomFrame {

    String toString();

    void init(final IAtomMethod method);

    void init(final IAtomMethod method, final boolean isChangeThreadFrame);

    void setArgument(final int index, final int value);

    void setArgument(final int index, final long value);

    void setArgument(final int index, final Object value);

    void intArgument(final int index, final Object value);

    void destroy();

    AbstractDVMThread getThread();

    int[] getIntArguments();

    void setIntArguments(int[] intArguments);

    Object[] getObjectArguments();

    void setObjectArguments(Object[] objectArguments);

    int getSingleReturn();

    void setSingleReturn(int singleReturn);

    long getDoubleReturn();

    void setDoubleReturn(long doubleReturn);

    Object getObjectReturn();

    void setObjectReturn(Object objectReturn);

    Throwable getThrowableReturn();

    void setThrowableReturn(Throwable throwableReturn);

    IAtomMethod getMethod();

    void setMethod(IAtomMethod method);

    boolean isChangeThreadFrame();

    void setChangeThreadFrame(boolean changeThreadFrame);

    int getPc();

    int getRegisterCount();

    void setRegisterCount(int registerCount);

    boolean[] getIsObjectRegister();

    void setIsObjectRegister(boolean[] isObjectRegister);

    int[] getIntRegisters();

    void setIntRegisters(int[] intRegisters);

    Object[] getObjectRegisters();

    void setObjectRegisters(Object[] objectRegisters);

    int getArgumentCount();

    void setArgumentCount(int argumentCount);

    Object getMonitor();

    void setMonitor(Object monitor);

    int increasePc();

    int increasePc(int add);

    void resetPc(int point);
}
