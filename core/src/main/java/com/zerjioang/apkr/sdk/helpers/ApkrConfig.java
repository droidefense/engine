package com.zerjioang.apkr.sdk.helpers;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;

import java.io.*;
import java.util.Properties;

/**
 * Created by sergio on 29/4/16.
 */
public class ApkrConfig implements Serializable {

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static ApkrConfig instance = new ApkrConfig();

    //vars
    private Properties prop = new Properties();
    private OutputStream output = null;

    private ApkrConfig() {
        //create config file if not exists
        if (!new File(CONFIG_PROPERTIES).exists())
            createConfigFile();
        //read vars
        readConfigFile();
    }

    public static ApkrConfig getInstance() {
        return instance;
    }

    private void readConfigFile() {
        InputStream input = null;

        try {
            input = new FileInputStream(CONFIG_PROPERTIES);
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not create properties.config file input stream", e, e.getLocalizedMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.write(LoggerType.ERROR, "Could not close properties.config file input stream", e, e.getLocalizedMessage());
                }
            }
        }
    }

    private void createConfigFile() {
        try {
            output = new FileOutputStream(CONFIG_PROPERTIES);

            // set the properties value
            prop.setProperty("OVERWRITE_DECODE_FOLDER", "false");
            prop.setProperty("MULTITHREAD", "false");
            prop.setProperty("DECOMPILE", "false");
            prop.setProperty("DB_STORAGE", "false");
            prop.setProperty("ENGINE_VERSION", "1.0");

            prop.setProperty("SERVER_FOLDER", "");
            prop.setProperty("RESOURCE_FOLDER", "resources");
            prop.setProperty("MODEL_FOLDER", "models");
            prop.setProperty("UNPACK_FOLDER", "unpacked");
            prop.setProperty("UPLOAD_FOLDER", "upload");
            prop.setProperty("RULE_FOLDER", "rule");
            prop.setProperty("STATIC_REPORT_FOLDER", "static");
            prop.setProperty("CVS_SPLIT", ";");
            prop.setProperty("SIGNATURE_FILE", "filetypes.csv");
            prop.setProperty("DEX_MAGIC_NUMBER_STR", "6465780A30333500");
            prop.setProperty("BEAUTIFIER_FILE", "beautifiers-dex.list");
            prop.setProperty("NATIVE_METHOD_FILE", "sdk-classes.csv");
            prop.setProperty("PLG_FOLDER_NAME", "plugins");
            prop.setProperty("BIN_MANIFEST_FOLDER", "original");
            prop.setProperty("JAVA_SDK_CLASS_HASHSET_NAME", "jdk8-classlist.map");
            prop.setProperty("ANDROID_SDK_CLASS_HASHSET_NAME", "android-sdk-classlist.map");
            prop.setProperty("ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME", "android-support-classlist.map");
            prop.setProperty("XML_EXTENSION", ".xml");
            prop.setProperty("PROJECT_DATA_FILE", "project.data");
            prop.setProperty("PROJECT_JSON_FILE", "report.json");
            prop.setProperty("PSCOUT_MODEL", "pscout.model");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            Log.write(LoggerType.ERROR, "Could not create properties.config file", io, io.getLocalizedMessage());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Log.write(LoggerType.ERROR, "Could not close properties.config file output stream", e, e.getLocalizedMessage());
                }
            }

        }
    }

    public Object getValue(String key) {
        return prop.get(key);
    }
}
