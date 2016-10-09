package com.zerjioang.apkr.parser;

import apkr.external.module.AtomManifestParser;
import apkr.external.module.datamodel.manifest.Manifest;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.parser.base.AbstractFileParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class AndroidManifestParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        try {
            AtomManifestParser externalParser = new AtomManifestParser();
            File manifestFile = currentProject.getManifestFile();
            externalParser.parse(manifestFile);
            Manifest man = externalParser.getManifest();
            currentProject.setManifestInfo(man);
            currentProject.setEntryPoints(externalParser.getEntryPoints());
            //class with package name
            try {
                this.currentProject.setClassWithPackageName(man.getApplication().getActivities().get(0).getName().contains(man.getPackageName()));
            } catch (Exception e) {
                Log.write(LoggerType.ERROR, "Atom om could not determine if this sample has built-in package name in source code classes", e.getLocalizedMessage());
                this.currentProject.setClassWithPackageName(false);
            }

            //search for main class if exists
            if (externalParser.hasMainClass())
                this.currentProject.setMainClass(externalParser.getMainClass().getName());
            else
                this.currentProject.setMainClass("None");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
