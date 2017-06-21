package droidefense.sdk.helpers;


import java.io.Serializable;

public class InternalConstant implements Serializable {

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
    public static final String ENGINE_VERSION = "0.1";
    public static final String CVS_SPLIT = ",";
    public static final String SPACE = " ";
    public static final String LEAD_DEVELOPER = "@zerjioang";
    public static final String REPO_URL = "https://github.com/droidefense/";
    public static final String ISSUES_URL = "https://github.com/droidefense/engine/issues";
    public static final String CONFIG_FOLDER = "config";
    public static boolean IS_WINDOWS_HOST = System.getProperty("os.name").toLowerCase().contains("windows");
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
    public static byte[] DEX_FILE_MAGIC = {0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00}; //"dex\n035\0"
    public String OS_NAME = System.getProperty("os.name");
}
