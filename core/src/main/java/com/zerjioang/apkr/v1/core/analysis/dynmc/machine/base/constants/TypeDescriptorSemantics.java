package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.constants;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DynamicUtils;

import java.io.Serializable;

public class TypeDescriptorSemantics implements Serializable {

    public static final String DESC_RESOLVED_V = "void"; //only valid for return types
    public static final String DESC_RESOLVED_Z = "boolean";
    public static final String DESC_RESOLVED_B = "byte";
    public static final String DESC_RESOLVED_S = "short";
    public static final String DESC_RESOLVED_C = "char";
    public static final String DESC_RESOLVED_I = "int";
    public static final String DESC_RESOLVED_J = "long";
    public static final String DESC_RESOLVED_F = "float";
    public static final String DESC_RESOLVED_D = "double";
    public static final String DESC_RESOLVED_CLASSNAME = "Classname";
    public static final String DESC_RESOLVED_DESCRIPTOR = "Descriptor";

    public static final byte DESC_V = 'V';
    public static final byte DESC_Z = 'Z';
    public static final byte DESC_B = 'B';
    public static final byte DESC_S = 'S';
    public static final byte DESC_C = 'C';
    public static final byte DESC_I = 'I';
    public static final byte DESC_J = 'J';
    public static final byte DESC_F = 'F';
    public static final byte DESC_D = 'D';
    public static final byte DESC_CLASSNAME = 'L';

    // for example Lfully/qualified/Name;   the cls fully.qualified.Name

    public static final byte DESC_DESCRIPTOR = '[';

    //[descriptor   array of descriptor, usable recursively for arrays-of-arrays, though it is invalid to have more than 255 dimensions.

    public static String resolveDescriptor(String data) {
        if (data == null)
            return "";
        int idx = data.indexOf(")");
        if (idx != -1) {
            data = data.substring(idx + 1);
        } else {
            return "";
        }
        byte desc = data.getBytes()[0];
        switch (desc) {
            case DESC_V:
                return DESC_RESOLVED_V;
            case DESC_Z:
                return DESC_RESOLVED_Z;
            case DESC_B:
                return DESC_RESOLVED_B;
            case DESC_S:
                return DESC_RESOLVED_S;
            case DESC_C:
                return DESC_RESOLVED_C;
            case DESC_I:
                return DESC_RESOLVED_I;
            case DESC_J:
                return DESC_RESOLVED_J;
            case DESC_F:
                return DESC_RESOLVED_F;
            case DESC_D:
                return DESC_RESOLVED_D;
            case DESC_DESCRIPTOR:
                return DESC_RESOLVED_DESCRIPTOR;
            case DESC_CLASSNAME:
                return DynamicUtils.classNameToJava(data.substring(1, data.length() - 1));
            default:
                return null;
        }
    }
}
