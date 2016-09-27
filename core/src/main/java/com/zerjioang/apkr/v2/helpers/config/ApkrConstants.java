package com.zerjioang.apkr.v2.helpers.config;


import java.io.Serializable;

public class ApkrConstants implements Serializable {

    public static final String SUPERCLASS = "java/lang/Object";
    public static final String DEX_EXTENSION = ".dex";
    public static final String APK_EXTENSION = ".apk";
    public static final String ANDROID_MANIFEST = "AndroidManifest.xml";
    public static final String NONE = "";
    public static final String ANALYSIS_METADATA_FILE = "analysis.metadata";
    public static final String EMPTY_STRING = "";
    public static final String JAVA_EXTENSION = ".java";
    public static final String CERTIFICATE_EXTENSION = ".rsa";
    public static final String WEKA_FEATURES_FILE = "features.arff";
    public static final int SHA256_LENGTH = 64;
    public static final String INTERNAL_DATA_FOLDER = "internal_temp";
    public static String PROJECT_JSON_FILE;
    public static String JARFILE_EXTENSION = ".jar";
    public static String COMPILED_JAVA_EXTENSION = ".class";
    public static int HEADER_SIZE = 0x70;
    public static int SHA_SIGNATURE_SIZE = 20;
    public static int CHECKSUM_SIZE = 4;
    public static int ENDIAN_CONSTANT = 0x12345678;
    public static int REVERSE_ENDIAN_CONSTANT = 0x78563412;
    public static int NO_INDEX = 0xffffffff;// == -1 if treated as a signed int
    public static String JAVA_SDK_CLASS_HASHSET_NAME = "jdk8-classlist.map";
    public static String ANDROID_SDK_CLASS_HASHSET_NAME = "android-sdk-classlist.map";
    public static String ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME = "android-support-classlist.map";
    public static String XML_EXTENSION = ".xml";
    public static String OS_NAME;
    public static boolean IS_WINDOWS_HOST;
    public static byte[] DEX_FILE_MAGIC = {0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00}; //"dex\n035\0"
    //external variables
    public static String SERVER_FOLDER;
    public static String RESOURCE_FOLDER;
    public static String UNPACK_FOLDER;
    public static String UPLOAD_FOLDER;
    public static String STATIC_REPORT_FOLDER;
    public static String DECOMPILE;
    public static String CVS_SPLIT;
    public static String SIGNATURE_FILE;
    public static String DEX_MAGIC_NUMBER_STR;
    public static String BEAUTIFIER_FILE;
    public static String NATIVE_METHOD_FILE;
    public static String BIN_MANIFEST_FOLDER;
    public static boolean DB_STORAGE;
    public static String ENGINE_VERSION;
    public static String PROJECT_DATA_FILE;
    public static boolean OVERWRITE_DECODE_FOLDER;
    public static String STATIC_PLG_FOLDER_NAME;
    public static String DYNAMIC_PLG_FOLDER_NAME;
    public static String PSCOUT_MODEL;
    public static String RULE_FOLDER;
    public static String MODEL_FOLDER;
    //external config reader
    private static ApkrConfig configurator = ApkrConfig.getInstance();
    private static boolean MULTITHREAD;

    public static void init() {

        OS_NAME = System.getProperty("os.name");
        IS_WINDOWS_HOST = OS_NAME.toLowerCase().contains("windows");

        //load external variables
        OVERWRITE_DECODE_FOLDER = configurator.getValue("OVERWRITE_DECODE_FOLDER").equals("true");
        MULTITHREAD = configurator.getValue("MULTITHREAD").equals("true");
        SERVER_FOLDER = (String) configurator.getValue("SERVER_FOLDER");
        UPLOAD_FOLDER = (String) configurator.getValue("UPLOAD_FOLDER");
        RULE_FOLDER = (String) configurator.getValue("RULE_FOLDER");
        MODEL_FOLDER = (String) configurator.getValue("MODEL_FOLDER");
        STATIC_REPORT_FOLDER = (String) configurator.getValue("STATIC_REPORT_FOLDER");


        DB_STORAGE = configurator.getValue("DB_STORAGE").equals("true");
        ENGINE_VERSION = (String) configurator.getValue("ENGINE_VERSION");
        DECOMPILE = (String) configurator.getValue("DECOMPILE");
        SERVER_FOLDER = (String) configurator.getValue("SERVER_FOLDER");
        RESOURCE_FOLDER = (String) configurator.getValue("RESOURCE_FOLDER");
        UNPACK_FOLDER = (String) configurator.getValue("UNPACK_FOLDER");
        CVS_SPLIT = (String) configurator.getValue("CVS_SPLIT");
        SIGNATURE_FILE = (String) configurator.getValue("SIGNATURE_FILE");
        DEX_MAGIC_NUMBER_STR = (String) configurator.getValue("DEX_MAGIC_NUMBER_STR");
        BEAUTIFIER_FILE = (String) configurator.getValue("BEAUTIFIER_FILE");
        NATIVE_METHOD_FILE = (String) configurator.getValue("NATIVE_METHOD_FILE");

        PSCOUT_MODEL = (String) configurator.getValue("PSCOUT_MODEL");

        STATIC_PLG_FOLDER_NAME = (String) configurator.getValue("STATIC_PLG_FOLDER_NAME");
        DYNAMIC_PLG_FOLDER_NAME = (String) configurator.getValue("DYNAMIC_PLG_FOLDER_NAME");

        BIN_MANIFEST_FOLDER = (String) configurator.getValue("BIN_MANIFEST_FOLDER");
        JAVA_SDK_CLASS_HASHSET_NAME = (String) configurator.getValue("JAVA_SDK_CLASS_HASHSET_NAME");
        ANDROID_SDK_CLASS_HASHSET_NAME = (String) configurator.getValue("ANDROID_SDK_CLASS_HASHSET_NAME");
        ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME = (String) configurator.getValue("ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME");
        XML_EXTENSION = (String) configurator.getValue("XML_EXTENSION");
        PROJECT_DATA_FILE = (String) configurator.getValue("PROJECT_DATA_FILE");
        PROJECT_JSON_FILE = (String) configurator.getValue("PROJECT_JSON_FILE");
    }
}
