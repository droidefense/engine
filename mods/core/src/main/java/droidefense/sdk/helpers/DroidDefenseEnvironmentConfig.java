package droidefense.sdk.helpers;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.exception.ConfigFileNotFoundException;
import droidefense.handler.FileIOHandler;
import droidefense.sdk.util.JsonStyle;
import droidefense.sdk.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by sergio on 29/4/16
 */
public class DroidDefenseEnvironmentConfig implements Serializable {

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
        PSCOUT_MODEL = "pscout/pscout.model";
    }

    public static DroidDefenseEnvironmentConfig getInstance(boolean autoinit) throws ConfigFileNotFoundException {
        if (instance == null) {
            instance = new DroidDefenseEnvironmentConfig();
            if (autoinit) {
                instance.init();
            }
        }
        return instance;
    }

    public static DroidDefenseEnvironmentConfig getInstance() throws ConfigFileNotFoundException {
        return getInstance(false);
    }

    private void deserialize(DroidDefenseEnvironmentConfig params) {
        instance = params;
        FileIOHandler.setEnvironmentConfiguration(instance);
    }

    private void init() throws ConfigFileNotFoundException {
        Log.write(LoggerType.TRACE, "Loading Droidefense environment...");
        runconfig();
    }

    private void runconfig() throws ConfigFileNotFoundException {
        File configFile = FileIOHandler.getConfigurationFile();
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
                }
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not deserialize " + configFullPath + " file data", e, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Error reading " + configFullPath + " file." + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            throw new ConfigFileNotFoundException("Invalid " + configFullPath + " file content." + e.getLocalizedMessage());
        }
    }

    public boolean createDefaultConfigJsonFile() {
        String configFullPath = FileIOHandler.getConfigurationFile().getAbsolutePath();
        Log.write(LoggerType.DEBUG, "Creating default config file at " + configFullPath);
        //copy and paste config file from default folder
        String defaultContent = getDefaultConfigFileContent();
        return FileIOHandler.saveFile(configFullPath, defaultContent);
    }

    private String getDefaultConfigFileContent() {
        try {
            return Util.readFileFromInternalResourcesAsString("config/config.json");
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            return Util.toJson(instance, JsonStyle.JSON_BEAUTY);
        }
    }
}
