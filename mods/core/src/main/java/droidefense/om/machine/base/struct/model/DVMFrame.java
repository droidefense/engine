/*
 * Developed by Koji Hisano <koji.hisano@eflow.jp>
 *
 * Copyright (C) 2009 eflow Inc. <http://www.eflow.jp/en/>
 *
 * This file is a part of Android Dalvik VM on Java.
 * http://code.google.com/p/android-dalvik-vm-on-java/
 *
 * This project is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package droidefense.om.machine.base.struct.model;

import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;

import java.io.Serializable;

public final class DVMFrame implements IAtomFrame, Serializable {

    private static final int DEFAULT_REGISTER_SIZE = 16;

    private final AbstractDVMThread thread;

    //arguments
    private int[] intArguments = new int[DEFAULT_REGISTER_SIZE];
    private Object[] objectArguments = new Object[DEFAULT_REGISTER_SIZE];

    //returns
    private int singleReturn;
    private long doubleReturn;
    private Object objectReturn;
    private Throwable throwableReturn;

    //method to execute
    private IDroidefenseMethod method;

    //registers to be used
    private boolean[] isObjectRegister = new boolean[DEFAULT_REGISTER_SIZE];
    private int[] intRegisters = new int[DEFAULT_REGISTER_SIZE];
    private Object[] objectRegisters = new Object[DEFAULT_REGISTER_SIZE];

    //othres
    private boolean isChangeThreadFrame;
    private int pc;
    private int registerCount;
    private int argumentCount;
    private Object monitor;

    public DVMFrame(final AbstractDVMThread thread) {
        this.thread = thread;
    }

    public static int getDefaultRegisterSize() {
        return DEFAULT_REGISTER_SIZE;
    }

    public String toString() {
        return method.getOwnerClass().getName() + "." + method.getName() + method.getDescriptor();
    }

    // Don't implement the initialization of this class in the constructor to re-use this instance
    public void init(final IDroidefenseMethod method) {
        init(method, false);
    }

    // Don't implement the initialization of this class in the constructor to re-use this instance
    public void init(final IDroidefenseMethod method, final boolean isChangeThreadFrame) {
        this.method = method;
        this.isChangeThreadFrame = isChangeThreadFrame;

        pc = 0;

        int newRegisterCount = method.getRegisterCount();
        this.registerCount = newRegisterCount;
        if (intRegisters.length < newRegisterCount) {
            isObjectRegister = new boolean[newRegisterCount];
            intRegisters = new int[newRegisterCount];
            objectRegisters = new Object[newRegisterCount];
        }

        int newArgumentCount = method.getOutgoingArgumentCount();
        this.argumentCount = newArgumentCount;
        if (intArguments.length < newArgumentCount) {
            intArguments = new int[newArgumentCount];
            objectArguments = new Object[newArgumentCount];
        }
    }

    public void setArgument(final int index, final int value) {
        intArguments[index] = value;
    }

    public void setArgument(final int index, final long value) {
        DynamicUtils.setLong(intArguments, index, value);
    }

    public void setArgument(final int index, final Object value) {
        objectArguments[index] = value;
    }

    // This method is used to set arguments before calling
    public void intArgument(final int index, final Object value) {
        int position = registerCount - index - 1;
        if (position >= 0 && position < objectRegisters.length)
            objectRegisters[position] = value;
    }

    //GETTERS AND SETTERS

    public void destroy() {
        for (int i = 0, length = registerCount; i < length; i++) {
            isObjectRegister[i] = false;
            intRegisters[i] = 0;
            objectRegisters[i] = null;
        }
        for (int i = 0, length = argumentCount; i < length; i++) {
            intArguments[i] = 0;
            objectArguments[i] = null;
        }
        singleReturn = 0;
        doubleReturn = 0;
        objectReturn = null;
        throwableReturn = null;

        if (method.isSynchronized()) {
            Object monitor = this.monitor;
            this.monitor = null;
            thread.releaseLock(monitor);
        }
    }

    public AbstractDVMThread getThread() {
        return thread;
    }

    public int[] getIntArguments() {
        return intArguments;
    }

    public void setIntArguments(int[] intArguments) {
        this.intArguments = intArguments;
    }

    public Object[] getObjectArguments() {
        return objectArguments;
    }

    public void setObjectArguments(Object[] objectArguments) {
        this.objectArguments = objectArguments;
    }

    public int getSingleReturn() {
        return singleReturn;
    }

    public void setSingleReturn(int singleReturn) {
        this.singleReturn = singleReturn;
    }

    public long getDoubleReturn() {
        return doubleReturn;
    }

    public void setDoubleReturn(long doubleReturn) {
        this.doubleReturn = doubleReturn;
    }

    public Object getObjectReturn() {
        return objectReturn;
    }

    public void setObjectReturn(Object objectReturn) {
        this.objectReturn = objectReturn;
    }

    public Throwable getThrowableReturn() {
        return throwableReturn;
    }

    public void setThrowableReturn(Throwable throwableReturn) {
        this.throwableReturn = throwableReturn;
    }

    public IDroidefenseMethod getMethod() {
        return method;
    }

    public void setMethod(IDroidefenseMethod method) {
        this.method = method;
    }

    public boolean isChangeThreadFrame() {
        return isChangeThreadFrame;
    }

    public void setChangeThreadFrame(boolean changeThreadFrame) {
        isChangeThreadFrame = changeThreadFrame;
    }

    public int getPc() {
        return pc;
    }

    private void setPc(int pc) {
        this.pc = pc;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(int registerCount) {
        this.registerCount = registerCount;
    }

    public boolean[] getIsObjectRegister() {
        return isObjectRegister;
    }

    public void setIsObjectRegister(boolean[] isObjectRegister) {
        this.isObjectRegister = isObjectRegister;
    }

    public int[] getIntRegisters() {
        return intRegisters;
    }

    public void setIntRegisters(int[] intRegisters) {
        this.intRegisters = intRegisters;
    }

    public Object[] getObjectRegisters() {
        return objectRegisters;
    }

    public void setObjectRegisters(Object[] objectRegisters) {
        this.objectRegisters = objectRegisters;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
    }

    public Object getMonitor() {
        return monitor;
    }

    public void setMonitor(Object monitor) {
        this.monitor = monitor;
    }

    public int increasePc() {
        int oldPc = getPc();
        setPc(oldPc + 1);
        return oldPc;
    }

    public int increasePc(int add) {
        setPc(getPc() + add);
        return getPc();
    }

    public void resetPc(int point) {
        setPc(point);
    }
}
