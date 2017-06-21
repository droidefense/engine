package droidefense.worker.parser;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.worker.base.AbstractFileParser;
import droidefense.xmodel.base.ManifestParser;
import droidefense.sdk.manifest.Manifest;
import droidefense.sdk.manifest.UsesSDK;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class AndroidManifestParser extends AbstractFileParser {

    private Manifest man;

    public AndroidManifestParser(LocalApkFile apk, DroidefenseProject currentProject) {
        super(apk, currentProject);
    }

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nParsing AndroidManifest.xml...\n");
        try {
            ManifestParser externalParser = new ManifestParser();
            byte[] manifestContent = currentProject.getManifestFile();
            externalParser.parse(manifestContent);
            man = externalParser.getManifest();
            if(man!=null){
                currentProject.setManifestInfo(man);
                currentProject.setEntryPoints(externalParser.getEntryPoints());
                definePackageNameTypes();
                defineMainClass(externalParser);
                defineCompatibilityWindow();
            }
        } catch (ParserConfigurationException e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
        }
    }

    private void definePackageNameTypes() {
        try {
            this.currentProject.setClassWithPackageName(man.getApplication().getActivities().get(0).getName().contains(man.getPackageName()));
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "Droidefense could not determine if this sample has built-in package name in source code classes", e.getLocalizedMessage());
            this.currentProject.setClassWithPackageName(false);
        }
    }

    private void defineMainClass(ManifestParser externalParser) {
        if (externalParser.hasMainClass())
            this.currentProject.setMainClass(externalParser.getMainClass().getName());
        else
            this.currentProject.setMainClass("None");
    }

    private void defineCompatibilityWindow() {
        //set compatibilitywindow if valid
        if (man != null) {
            ArrayList<UsesSDK> list = man.getUsesSdkList();
            if (list != null && !list.isEmpty()) {
                UsesSDK sdk = list.get(0);
                this.currentProject.setMinVersionWindow(sdk.getMinSdkVersion());
                this.currentProject.setMaxVersionWindow(sdk.getMaxSdkVersion());
                this.currentProject.setTargetVersionWindow(sdk.getTargetSdkVersion());
            }
        }
    }
}
