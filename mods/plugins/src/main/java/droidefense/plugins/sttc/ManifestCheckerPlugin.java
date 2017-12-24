package droidefense.plugins.sttc;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.IOException;

/**
 * Created by r00t on 07/12/2015.
 */
public class ManifestCheckerPlugin extends AbstractStaticPlugin {

    public static final String VERSION_01 = "";
    private static final String MANIFEST_FILE_PATH = "/META-INF/MANIFEST.MF";
    private AbstractHashedFile metainfManifestFile;

    @Override
    public void onPreExecute() {
        metainfManifestFile = currentProject.getMetainfManifestFile();
    }

    @Override
    public void onExecute() {
        if (metainfManifestFile != null) {
            Log.write(LoggerType.DEBUG, "Meta-inf manifest file found");
            Log.write(LoggerType.DEBUG, "Reading creator...");
            readFileCreator();
        }
    }

    private void readFileCreator() {
        try {
            String data = new String(metainfManifestFile.getContent(), "utf-8");
            String[] datalines = data.trim().split("\n");
            Log.write(LoggerType.DEBUG, "File contains: " + datalines.length + " lines");
            if (datalines.length >= 2) {
                String version = processManifestVersion(datalines[0]);
                String creator = processManifestCreator(datalines[1]);
                currentProject.setMetaManifestVersion(version);
                currentProject.setMetaManifestCreator(creator);
                Log.write(LoggerType.DEBUG, "META-INF file created with: " + creator + " and registered with version: " + version);
            }
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Could not read content of Meta-inf manifest file");
        }
    }

    private String processManifestCreator(String dataline) {
        String data = dataline.replaceAll("Created-By: ", "");
        data = data.replaceAll("\r", "");
        data = data.replaceAll("\n", "");
        return data;
    }

    private String processManifestVersion(String dataline) {
        String data = dataline.replaceAll("Manifest-Version: ", "");
        data = data.replaceAll("\r", "");
        data = data.replaceAll("\n", "");
        return data;
    }

    @Override
    protected void postExecute() {
        Log.write(LoggerType.DEBUG, "Manifest Checker Plugin finished");
    }

    @Override
    protected String getPluginName() {
        return "Manifest.MF creator detector";
    }
}