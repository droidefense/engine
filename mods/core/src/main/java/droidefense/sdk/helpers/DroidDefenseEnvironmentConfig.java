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
 * Created by sergio on 29/4/16.Serializable
 */
public class DroidDefenseEnvironmentConfig implements Serializable {

    private final static String UNIX_CONFIG_PROPERTIES = "config.linux.json";
    private final static String WINDOWS_CONFIG_PROPERTIES = "config.win.json";
    private final static String MAC_CONFIG_PROPERTIES = "config.mac.json";
    private final static String CONFIG_PROPERTIES = UNIX_CONFIG_PROPERTIES;
    public static final String VERSION = "0.1";
    public static final String TAG = "alpha";
    private transient static DroidDefenseEnvironmentConfig instance;

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

    public static DroidDefenseEnvironmentConfig getInstance() throws ConfigFileNotFoundException {
        if(instance==null){
            instance = new DroidDefenseEnvironmentConfig();
            instance.init();
        }
        return instance;
    }

    private DroidDefenseEnvironmentConfig() throws ConfigFileNotFoundException {
        //initialize string variables with default values
        OVERWRITE_DECODE_FOLDER = false;
        MULTITHREAD = false;
        DECOMPILE = false;
        DB_STORAGE = false;
        UNPACK_FOLDER = "./unpack";
        UPLOAD_FOLDER = "./upload";
        SERVER_FOLDER = "./server";
        RESOURCE_FOLDER = "./lib/data/";
        MODEL_FOLDER = "ml";
        RULE_FOLDER = "rules";
        STATIC_REPORT_FOLDER = "./reports";
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
        PSCOUT_MODEL = "map/pscout.model";
    }

    private void deserialize(DroidDefenseEnvironmentConfig params) {
        this.instance = params;
    }

    private void init() throws ConfigFileNotFoundException {
        Log.write(LoggerType.TRACE, "Loading Droidefense data structs...");
        String executablePath = FileIOHandler.getBaseDirPath();
        String configPath = FileIOHandler.getConfigPath();
        Log.write(LoggerType.INFO, "Execution base path is: "+executablePath);

        if(OSDetection.isWindows()){
            Log.write(LoggerType.INFO, "System detected is MS Windows");
            runconfig(configPath, WINDOWS_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isMacOSX()){
            Log.write(LoggerType.INFO, "System detected is Mac OS");
            runconfig(configPath, MAC_CONFIG_PROPERTIES);
        }
        else if(OSDetection.isUnix()){
            Log.write(LoggerType.INFO, "System detected is Unix");
            runconfig(configPath, UNIX_CONFIG_PROPERTIES);
        }
        else{
            //load linux as default
            runconfig(configPath, CONFIG_PROPERTIES);
        }
    }

    private void runconfig(String configFilePath, String name) throws ConfigFileNotFoundException {
        File configFile = new File(configFilePath, name);
        String configFullPath = configFile.getAbsolutePath();
        try {
            Log.write(LoggerType.DEBUG, "Reading config file: " + configFullPath);
            if (configFile.exists()) {
                byte[] jsonData = Util.loadFileAsBytes(configFullPath);
                if (jsonData.length == 0) {
                    throw new ConfigFileNotFoundException(configFullPath + " file content is not valid");
                } else {
                    DroidDefenseEnvironmentConfig params = (DroidDefenseEnvironmentConfig) Util.toObjectFromJson(new String(jsonData), DroidDefenseEnvironmentConfig.class);
                    deserialize(params);
                    FileIOHandler.init();
                }
            } else {
                Log.write(LoggerType.ERROR, "Config file  " + configFullPath + " was not found");
                Log.write(LoggerType.DEBUG, "Creating default config file at " + configFullPath);
                //copy and paste config file from default folder
                FileIOHandler.saveFile(configFullPath, getDefaultFileContent(name));
                FileIOHandler.init();
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not deserialize " + configFilePath + " file data", e, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Error reading " + configFilePath + " file." + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Invalid " + configFilePath + " file content." + e.getLocalizedMessage());
        }
    }

    private String getDefaultFileContent(String name) {
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
