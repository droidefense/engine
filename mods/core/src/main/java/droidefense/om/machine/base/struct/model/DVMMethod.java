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

import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.sdk.helpers.DroidDefenseEnvironment;

import java.io.Serializable;

public class DVMMethod implements IDroidefenseMethod, Serializable {

    //shared by all classes. Pool

    private static SharedPool pool = SharedPool.getInstance();

    private IDroidefenseClass ownerClass;

    private int flag;
    private boolean isInstance;
    private boolean isSynchronized;
    private String name;
    private String descriptor;
    private int stackSize;
    private int variableSize;
    private byte[] byteCode;
    private int[] exceptionPositions;
    private String[] exceptionClasses;
    private int registerCount;
    private int incomingArgumentCount;
    private int outgoingArgumentCount;
    private int[] opcodes;
    private int[] registercodes;
    private int[] index;
    private int[] exceptionStartAddresses;
    private int[] exceptionEndAdresses;
    private int[] exceptionHandlerIndexes;
    private String[][] exceptionHandlerTypes;
    private int[][] exceptionHandlerAddresses;

    public DVMMethod(final IDroidefenseClass cls) {
        this.ownerClass = cls;
    }

    //TOSTRING

    public String toString() {
        return DynamicUtils.classNameToJava(ownerClass.getName()) + "." + getName();
    }

    //GETTERS AND SETTERS

    public IDroidefenseClass getOwnerClass() {
        return ownerClass;
    }

    @Override
    public void setOwnerClass(IDroidefenseClass cls) {
        this.ownerClass = cls;
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

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }

    public int getVariableSize() {
        return variableSize;
    }

    public void setVariableSize(int variableSize) {
        this.variableSize = variableSize;
    }

    public byte[] getByteCode() {
        if (byteCode == null)
            return new byte[0];
        return byteCode;
    }

    public void setByteCode(byte[] byteCode) {
        this.byteCode = byteCode;
    }

    public int[] getExceptionPositions() {
        return exceptionPositions;
    }

    public void setExceptionPositions(int[] exceptionPositions) {
        this.exceptionPositions = exceptionPositions;
    }

    public String[] getExceptionClasses() {
        return exceptionClasses;
    }

    public void setExceptionClasses(String[] exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(int registerCount) {
        this.registerCount = registerCount;
    }

    public int getIncomingArgumentCount() {
        return incomingArgumentCount;
    }

    public void setIncomingArgumentCount(int incomingArgumentCount) {
        this.incomingArgumentCount = incomingArgumentCount;
    }

    public int getOutgoingArgumentCount() {
        return outgoingArgumentCount;
    }

    public void setOutgoingArgumentCount(int outgoingArgumentCount) {
        this.outgoingArgumentCount = outgoingArgumentCount;
    }

    public int[] getOpcodes() {
        return opcodes;
    }

    public void setOpcodes(int[] opcodes) {
        this.opcodes = opcodes;
    }

    public int[] getRegistercodes() {
        return registercodes;
    }

    public void setRegistercodes(int[] registercodes) {
        this.registercodes = registercodes;
    }

    public int[] getIndex() {
        return index;
    }

    public void setIndex(int[] index) {
        this.index = index;
    }

    public String[] getStrings() {
        return pool.getStrings();
    }

    public void setStrings(String[] strings) {
        pool.setStrings(strings);
    }

    public String[] getTypes() {
        return pool.getTypes();
    }

    public void setTypes(String[] types) {
        pool.setTypes(types);
    }

    public String[] getDescriptors() {
        return pool.getDescriptors();
    }

    public void setDescriptors(String[] descriptors) {
        pool.setDescriptors(descriptors);
    }

    public String[] getFieldClasses() {
        return pool.getFieldClasses();
    }

    public void setFieldClasses(String[] fieldClasses) {
        pool.setFieldClasses(fieldClasses);
    }

    public String[] getFieldTypes() {
        return pool.getFieldTypes();
    }

    public void setFieldTypes(String[] fieldTypes) {
        pool.setFieldTypes(fieldTypes);
    }

    public String[] getFieldNames() {
        return pool.getFieldNames();
    }

    public void setFieldNames(String[] fieldNames) {
        pool.setFieldNames(fieldNames);
    }

    public String[] getMethodClasses() {
        return pool.getMethodClasses();
    }

    public void setMethodClasses(String[] methodClasses) {
        pool.setMethodClasses(methodClasses);
    }

    public String[] getMethodTypes() {
        return pool.getMethodTypes();
    }

    public void setMethodTypes(String[] methodTypes) {
        pool.setMethodClasses(methodTypes);
    }

    public String[] getMethodNames() {
        return pool.getMethodNames();
    }

    public void setMethodNames(String[] methodNames) {
        pool.setMethodNames(methodNames);
    }

    public int[] getExceptionStartAddresses() {
        return exceptionStartAddresses;
    }

    public void setExceptionStartAddresses(int[] exceptionStartAddresses) {
        this.exceptionStartAddresses = exceptionStartAddresses;
    }

    public int[] getExceptionEndAdresses() {
        return exceptionEndAdresses;
    }

    public void setExceptionEndAdresses(int[] exceptionEndAdresses) {
        this.exceptionEndAdresses = exceptionEndAdresses;
    }

    public int[] getExceptionHandlerIndexes() {
        return exceptionHandlerIndexes;
    }

    public void setExceptionHandlerIndexes(int[] exceptionHandlerIndexes) {
        this.exceptionHandlerIndexes = exceptionHandlerIndexes;
    }

    public String[][] getExceptionHandlerTypes() {
        return exceptionHandlerTypes;
    }

    public void setExceptionHandlerTypes(String[][] exceptionHandlerTypes) {
        this.exceptionHandlerTypes = exceptionHandlerTypes;
    }

    public int[][] getExceptionHandlerAddresses() {
        return exceptionHandlerAddresses;
    }

    public void setExceptionHandlerAddresses(int[][] exceptionHandlerAddresses) {
        this.exceptionHandlerAddresses = exceptionHandlerAddresses;
    }

    @Override
    public String getReturnType() {
        return null;
    }

    @Override
    public boolean isFake() {
        return false;
    }

    @Override
    public IDroidefenseClass getTopClass() {
        return DroidDefenseEnvironment.getInstance().getParentClass(this.getOwnerClass());
    }
}
