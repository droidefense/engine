package com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 31/5/16.
 */
public class SharedPool implements Serializable {


    private static SharedPool instance = new SharedPool();

    private final Map<String, String> classNames;
    private String[] strings;
    private String[] types;
    private String[] descriptors;
    private String[] fieldClasses;
    private String[] fieldTypes;
    private String[] fieldNames;
    private String[] methodClasses;
    private String[] methodTypes;
    private String[] methodNames;

    public SharedPool() {
        classNames = new HashMap<String, String>();
    }

    public static SharedPool getInstance() {
        return instance;
    }

    public static void setInstance(SharedPool instance) {
        SharedPool.instance = instance;
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

    public void addClassName(String clsName) {
        if (clsName != null && !clsName.trim().isEmpty()) {
            String data = clsName.trim();
            classNames.put(data, data);
        }
    }

    public String getClassName(String data) {
        return classNames.get(data);
    }
}


