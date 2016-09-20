package com.zerjioang.apkr.v1.core.analysis.sttc.parser;

import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 21/2/16.
 */
public class APKMetaParser extends AbstractFileParser {

    @Override
    public void parserCode() {

        //get app files
        ArrayList<ResourceFile> files = currentProject.getAppFiles();
        ArrayList<ResourceFile> dexList = new ArrayList<>();

        ArrayList<ResourceFile> assetFiles = new ArrayList<>();
        ArrayList<ResourceFile> libFiles = new ArrayList<>();
        ArrayList<ResourceFile> rawFiles = new ArrayList<>();

        //manifest file
        ResourceFile manifest = null;
        ResourceFile certFile = null;
        for (ResourceFile r : files) {
            //count dex files and search manifest file
            if (r.getThisFile().getName().toLowerCase().endsWith(ApkrConstants.DEX_EXTENSION)) {
                dexList.add(r);
            } else if (r.getThisFile().getName().toLowerCase().endsWith(ApkrConstants.CERTIFICATE_EXTENSION)) {
                certFile = r;
            } else if (r.getThisFile().getName().equals(ApkrConstants.ANDROID_MANIFEST) && manifest == null) {
                manifest = r;
            } else if (r.getAbsolutePath().contains(File.separator + "assets" + File.separator)) {
                assetFiles.add(r);
            } else if (r.getAbsolutePath().contains(File.separator + "lib" + File.separator)) {
                libFiles.add(r);
            } else if (r.getAbsolutePath().contains(File.separator + "res" + File.separator + "raw")) {
                rawFiles.add(r);
            }
        }

        //set dex files
        this.currentProject.setDexList(dexList);

        //set assets, raw and lib files
        this.currentProject.setRawFiles(rawFiles);
        this.currentProject.setAssetsFiles(assetFiles);
        this.currentProject.setLibFiles(libFiles);

        //set number of dex files
        this.currentProject.setNumberofDex(dexList.size());

        //set manifest file
        this.currentProject.setManifestFile(manifest);

        //set certificate file
        this.currentProject.setCertificateFile(certFile);

        //set currentProject folder name
        this.currentProject.setProjectFolderName(FileIOHandler.getUnpackOutputPath(getApk()));
    }
}
