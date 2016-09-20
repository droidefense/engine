package com.zerjioang.apkr.v2.helpers.system;

/**
 * Created by sergio on 31/5/16.
 */
public class OSDetection {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMacOSX() {
        return (OS.contains("mac"));
    }

    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 || (OS.contains("sunos")));
    }

    public static String returnOS() {
        return OS;
    }
}
