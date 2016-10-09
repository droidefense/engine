package com.zerjioang.apkr.parser;

import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.parser.base.AbstractFileParser;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 21/2/16.
 */
public class APKMetaParser extends AbstractFileParser {

    @Override
    public void parserCode() {

        //get app files
        ArrayList<ApkrFile> files = currentProject.getAppFiles();
        ArrayList<ApkrFile> dexList = new ArrayList<>();

        ArrayList<ApkrFile> assetFiles = new ArrayList<>();
        ArrayList<ApkrFile> libFiles = new ArrayList<>();
        ArrayList<ApkrFile> rawFiles = new ArrayList<>();

        //manifest file
        ApkrFile manifest = null;
        ApkrFile certFile = null;
        for (ApkrFile r : files) {
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
