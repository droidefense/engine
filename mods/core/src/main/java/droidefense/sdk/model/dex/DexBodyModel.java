package droidefense.sdk.model.dex;

import droidefense.emulator.machine.base.struct.model.SharedPool;

import java.io.Serializable;

public class DexBodyModel implements Serializable {

    //set method shared pool. singleton
    public static SharedPool pool = SharedPool.getInstance();
    public int offset;
    public String[] strings;
    public String[] types;
    public String[] descriptors;
    public String[] fieldClasses;
    public String[] fieldTypes;
    public String[] fieldNames;
    public String[] methodClasses;
    public String[] methodTypes;
    public String[] methodNames;

    public DexBodyModel() {
        this.offset = -1;
    }

    public static SharedPool getPool() {
        return pool;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String[] getStrings() {
        return strings;
    }

    public void setStrings(String[] strings) {
        this.strings = strings;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String[] getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(String[] descriptors) {
        this.descriptors = descriptors;
    }

    public String[] getFieldClasses() {
        return fieldClasses;
    }

    public void setFieldClasses(String[] fieldClasses) {
        this.fieldClasses = fieldClasses;
    }

    public String[] getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(String[] fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String[] getMethodClasses() {
        return methodClasses;
    }

    public void setMethodClasses(String[] methodClasses) {
        this.methodClasses = methodClasses;
    }

    public String[] getMethodTypes() {
        return methodTypes;
    }

    public void setMethodTypes(String[] methodTypes) {
        this.methodTypes = methodTypes;
    }

    public String[] getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(String[] methodNames) {
        this.methodNames = methodNames;
    }

    public int increaseIndex(int i) {
        this.offset += i;
        return this.offset;
    }
}