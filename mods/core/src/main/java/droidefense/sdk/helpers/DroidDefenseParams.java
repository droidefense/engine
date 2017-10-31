package droidefense.sdk.helpers;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.system.OSDetection;
import droidefense.exception.ConfigFileNotFoundException;
import droidefense.handler.FileIOHandler;
import droidefense.util.JsonStyle;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by sergio on 29/4/16.
 */
public class DroidDefenseParams implements Serializable {

    private final static String UNIX_CONFIG_PROPERTIES = "config.linux.json";
    private final static String WINDOWS_CONFIG_PROPERTIES = "config.win.json";
    private final static String MAC_CONFIG_PROPERTIES = "config.mac.json";
    private final static String CONFIG_PROPERTIES = UNIX_CONFIG_PROPERTIES;
    public static final String VERSION = "1.0";
    public static final String TAG = "alpha-unstable";
    private static DroidDefenseParams instance = new DroidDefenseParams();

    //object var - flags
    public boolean OVERWRITE_DECODE_FOLDER;
    public boolean MULTITHREAD;
    public boolean DECOMPILE;
    public boolean DB_STORAGE;

    //object vars
    public String SERVER_FOLDER;
    public String RESOURCE_FOLDER;
    public String MODEL_FOLDER;
    public String UNPACK_FOLDER;
    public String UPLOAD_FOLDER;
    public String RULE_FOLDER;
    public String STATIC_REPORT_FOLDER;
    public String STATIC_PLG_FOLDER;
    public String DYNAMIC_PLG_FOLDER;
    public String BIN_MANIFEST_FOLDER;

    public String CVS_SPLIT;
    public String SIGNATURE_FILE;
    public String BEAUTIFIER_FILE;
    public String NATIVE_METHOD_FILE;

    public String DEX_MAGIC_NUMBER_STR;

    public String PLG_FOLDER_NAME;

    public String PROJECT_DATA_FILE;
    public String PROJECT_JSON_FILE;
    public String PSCOUT_MODEL;

    public String JAVA_SDK_CLASS_HASHSET_NAME;
    public String ANDROID_SDK_CLASS_HASHSET_NAME;
    public String ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME;
    public String XML_EXTENSION;

    public DroidDefenseParams() {
        //initialize string variables with default values
        OVERWRITE_DECODE_FOLDER = false;
        MULTITHREAD = false;
        DECOMPILE = false;
        DB_STORAGE = false;
        UNPACK_FOLDER = "../unpack";
        UPLOAD_FOLDER = "../upload";
        SERVER_FOLDER = "../server";
        RESOURCE_FOLDER = "../lib/data/";
        MODEL_FOLDER = "models";
        RULE_FOLDER = "rules";
        STATIC_REPORT_FOLDER = "../reports";
        STATIC_PLG_FOLDER = "";
        DYNAMIC_PLG_FOLDER = "";
        CVS_SPLIT = ";";
        SIGNATURE_FILE = "csv/filetypes.csv";
        DEX_MAGIC_NUMBER_STR = "6465780A30333500";
        BEAUTIFIER_FILE = "list/beautifiers-dex.list";
        NATIVE_METHOD_FILE = "csv/sdk-classes.csv";
        PLG_FOLDER_NAME = "plugins";
        BIN_MANIFEST_FOLDER = "original";
        JAVA_SDK_CLASS_HASHSET_NAME = "map/jdk8-classlist.map";
        ANDROID_SDK_CLASS_HASHSET_NAME = "map/android-sdk-classlist.map";
        ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME = "map/android-support-classlist.map";
        XML_EXTENSION = ".xml";
        PROJECT_DATA_FILE = "project.data";
        PROJECT_JSON_FILE = "report.json";
        PSCOUT_MODEL = "map/pscout.map";
    }

    private static void deserialize(DroidDefenseParams params) {
        instance = params;
    }

    public static DroidDefenseParams getInstance() {
        return instance;
    }

    public static void init() throws ConfigFileNotFoundException {
        File base = new File("");
        String execPath = base.getAbsolutePath();
        String basePath = execPath+File.separator+InternalConstant.CONFIG_FOLDER;
        Log.write(LoggerType.INFO, "Execution base path is: "+execPath);

        if(OSDetection.isWindows()){
            Log.write(LoggerType.INFO, "System detected is MS Windows");
            runconfig(basePath+File.separator, WINDOWS_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isMacOSX()){
            Log.write(LoggerType.INFO, "System detected is Mac OS");
            runconfig(basePath+File.separator, MAC_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isUnix()){
            Log.write(LoggerType.INFO, "System detected is Unix");
            runconfig(base.getAbsolutePath()+File.separator, UNIX_CONFIG_PROPERTIES);
        }
        else{
            //load linux as default
            runconfig(base.getAbsolutePath()+File.separator, CONFIG_PROPERTIES);
        }
    }

    private static void runconfig(String configFilePath, String name) throws ConfigFileNotFoundException {
        configFilePath = configFilePath+name;
        try {
            Log.write(LoggerType.DEBUG, "Reading config file: " + configFilePath);
            if (new File(configFilePath).exists()) {
                byte[] jsonData = Util.loadFileAsBytes(configFilePath);
                if (jsonData.length == 0) {
                    throw new ConfigFileNotFoundException(configFilePath + " file content is not valid");
                } else {
                    DroidDefenseParams params = (DroidDefenseParams) Util.toObjectFromJson(new String(jsonData), DroidDefenseParams.class);
                    deserialize(params);
                }
            } else {
                Log.write(LoggerType.ERROR, "Config file  " + configFilePath + " was not found");
                Log.write(LoggerType.DEBUG, "Creating default config file at " + configFilePath);
                //copy and paste config file from default folder
                FileIOHandler.saveFile(configFilePath, getDefaultFileContent(name));
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not deserialize " + configFilePath + " file data", e, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Error reading " + configFilePath + " file." + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Invalid " + configFilePath + " file content." + e.getLocalizedMessage());
        }
    }

    private static String getDefaultFileContent(String name) {
        String defaultContent;
        try {
            defaultContent = Util.readFileFromInternalResourcesAsString("config/"+name);
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            defaultContent = Util.toJson(instance, JsonStyle.JSON_BEAUTY);
        }
        System.out.println("Readed file content is: "+defaultContent);
        return defaultContent;
    }
}
