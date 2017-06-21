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
        //initialize string variables
        SERVER_FOLDER = "";
        RESOURCE_FOLDER = "";
        MODEL_FOLDER = "";
        UNPACK_FOLDER = "";
        UPLOAD_FOLDER = "";
        RULE_FOLDER = "";
        STATIC_REPORT_FOLDER = "";
        STATIC_PLG_FOLDER = "";
        DYNAMIC_PLG_FOLDER = "";
        CVS_SPLIT = "";
        SIGNATURE_FILE = "";
        DEX_MAGIC_NUMBER_STR = "";
        BEAUTIFIER_FILE = "";
        NATIVE_METHOD_FILE = "";
        PLG_FOLDER_NAME = "";
        BIN_MANIFEST_FOLDER = "";
        JAVA_SDK_CLASS_HASHSET_NAME = "";
        ANDROID_SDK_CLASS_HASHSET_NAME = "";
        ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME = "";
        XML_EXTENSION = "";
        PROJECT_DATA_FILE = "";
        PROJECT_JSON_FILE = "";
        PSCOUT_MODEL = "";
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
            runconfig(basePath+File.separator+WINDOWS_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isMacOSX()){
            Log.write(LoggerType.INFO, "System detected is Mac OS");
            runconfig(basePath+File.separator+MAC_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isUnix()){
            Log.write(LoggerType.INFO, "System detected is Unix");
            runconfig(base.getAbsolutePath()+File.separator+UNIX_CONFIG_PROPERTIES);
        }
        else{
            //load linux as default
            runconfig(base.getAbsolutePath()+File.separator+CONFIG_PROPERTIES);
        }
    }

    private static void runconfig(String configFilePath) throws ConfigFileNotFoundException {
        try {
            if (new File(configFilePath).exists()) {
                byte[] jsonData = Util.loadFileAsBytes(configFilePath);
                if (jsonData.length == 0) {
                    throw new ConfigFileNotFoundException(configFilePath + " file content is not valid");
                } else {
                    DroidDefenseParams params = (DroidDefenseParams) Util.toObjectFromJson(new String(jsonData), DroidDefenseParams.class);
                    deserialize(params);
                }
            } else {
                //create mockup file
                FileIOHandler.saveFile(configFilePath, Util.toJson(instance, JsonStyle.JSON_BEAUTY));
                //throw no file found exception
                Log.write(LoggerType.ERROR, "Config file, " + configFilePath + " was not found");
                throw new ConfigFileNotFoundException("Config file, " + configFilePath + " was not found");
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not deserialize " + configFilePath + " file data", e, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Error reading " + configFilePath + " file." + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Invalid " + configFilePath + " file content." + e.getLocalizedMessage());
        }
    }
}
