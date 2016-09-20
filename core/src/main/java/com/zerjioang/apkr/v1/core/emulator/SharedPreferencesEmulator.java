package com.zerjioang.apkr.v1.core.emulator;

import com.zerjioang.apkr.v1.core.emulator.base.AbstractAndroidEmulator;

public class SharedPreferencesEmulator extends AbstractAndroidEmulator {

    public static String SHARED_PREFERENCES_CLASS = "android/content/SharedPreferences";

    private String methodName, descriptor;
    private Object[] arguments;

    public SharedPreferencesEmulator(String methodName, String descriptor) {
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    @Override
    public void emulate() {

    }
}
