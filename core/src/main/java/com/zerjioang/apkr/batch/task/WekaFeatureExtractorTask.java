package com.zerjioang.apkr.batch.task;


import apkr.external.module.AtomManifestParser;
import apkr.external.module.batch.base.IBatchTask;
import apkr.external.module.batch.base.IWekaGenerator;
import apkr.external.module.datamodel.manifest.UsesPermission;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.AXMLDecoderHandler;
import com.zerjioang.apkr.handler.DirScannerHandler;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.handler.FileUnzipHandler;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.handler.base.DirScannerFilter;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by .local on 15/04/2016.
 */
public class WekaFeatureExtractorTask implements IBatchTask, IWekaGenerator, Serializable {

    private static int counter = 0;
    private final File outputDir;
    private final String outputFileName;
    private final String sample_type;
    private File baseFolder;
    private ArrayList<ApkrFile> files;
    private ArrayList<File> xmlFiles;
    private boolean cont;

    //result
    private ArrayList<OutPutResult> data;

    public WekaFeatureExtractorTask(File baseFolder, File outputDir, String outputFileName, String sample_type) {
        this.baseFolder = baseFolder;
        this.outputFileName = outputFileName;
        this.sample_type = sample_type;
        this.outputDir = new File(outputDir.getAbsolutePath() + File.separator + "batch" + File.separator + getTaskIdName());
        xmlFiles = new ArrayList<>();
        data = new ArrayList<>();
    }

    @Override
    public void beforeTask() {
        System.out.println("---");
        System.out.println(getTaskName());
        System.out.println("---");
        System.out.println("Loading .apk files to extract features.");
        System.out.println("Running directory scanning on " + baseFolder.getAbsolutePath());
        System.out.println("Running...");
        DirScannerHandler scanner = new DirScannerHandler(baseFolder, false, new DirScannerFilter() {
            @Override
            public boolean addFile(File f) {
                return f.getName().endsWith(".apk");
            }
        });
        scanner.doTheJob();
        files = scanner.getFiles();
        cont = files != null && !files.isEmpty();
        if (!cont) {
            System.out.println("No files found!");
            return;
        }
    }

    @Override
    public void onTask() {
        createDir();
        if (cont) {
            for (int i = 0; i < files.size(); i++) {
                //only unpacks
                File out = new File(outputDir.getAbsolutePath() + File.separator + i);
                if (!out.exists()) {
                    createDir();
                    AbstractHandler handler = new FileUnzipHandler(files.get(i), out);
                    handler.doTheJob();
                }
                Log.write(LoggerType.TRACE, i);
                Log.write(LoggerType.TRACE, "Listing unpacked files...");
                //get android manifest
                ApkrFile manif = new ApkrFile(out.getAbsolutePath() + File.separator + ApkrConstants.ANDROID_MANIFEST);
                if (manif.getThisFile().exists()) {
                    Log.write(LoggerType.TRACE, "Decoding XML resources");
                    //decode unpacked files
                    ArrayList<ApkrFile> onlyManif = new ArrayList<>();
                    onlyManif.add(manif);
                    try {
                        AXMLDecoderHandler handler = new AXMLDecoderHandler(onlyManif);
                        handler.doTheJob();
                    } catch (Exception e) {
                        Log.write(LoggerType.FATAL, files.get(i).getAbsolutePath(), e.getLocalizedMessage(), e.getStackTrace());
                    }
                    Log.write(LoggerType.TRACE, "Generating file juicy information...");
                    counter++;
                    xmlFiles.add(manif.getThisFile());
                }
            }
        }
    }

    private boolean createDir() throws IllegalArgumentException {
        boolean ok = false;
        if (!outputDir.exists())
            ok = outputDir.mkdirs();
        else {
            return true;
        }
        if (!ok)
            throw new IllegalArgumentException("Could not create output directory");
        return ok;
    }

    @Override
    public void afterTask() {
        if (cont) {
            //generate info as csv file
            for (File xml : xmlFiles) {
                if (xml.exists() && xml.isFile() && xml.canRead()) {
                    try {
                        AtomManifestParser parser = new AtomManifestParser();
                        parser.parse(xml);
                        ArrayList<UsesPermission> permList = parser.getManifest().getUsesPermissionList();
                        com.zerjioang.apkr.batch.task.OutPutResult result = new com.zerjioang.apkr.batch.task.OutPutResult(permList);
                        data.add(result);
                    } catch (ParserConfigurationException | IOException e) {
                        Log.write(LoggerType.FATAL, xml.getAbsolutePath(), e.getLocalizedMessage(), e.getStackTrace());
                    } catch (Exception e) {
                        Log.write(LoggerType.FATAL, xml.getAbsolutePath(), e.getLocalizedMessage(), e.getStackTrace());
                    }
                }
            }
            toWekaData();
            //toCSV();
            System.out.println("Succesfully scanned and extracted features from " + counter + " files.");
            System.out.println("Check output folder for results: " + outputDir.getAbsolutePath());
        }
    }

    @Override
    public String getTaskName() {
        return "AndroidManifest.xml feature extraction task";
    }

    @Override
    public String getTaskIdName() {
        return "perm-feat-extract";
    }

    @Override
    public String toWekaData() {
        //convert data to weka format
        String[] names = com.zerjioang.apkr.batch.task.OutPutResult.getGlobalPermissionList();
        String header = "@relation relation\r\n";
        header += "\r\n";
        for (String attr : names) {
            header += "@attribute " + attr + " {false, true}\r\n";
        }
        header += "\r\n";
        header += "@attribute class {MALWARE, GOODWARE} \r\n";
        header += "\r\n";
        header += "@data\r\n";
        header += "\r\n";
        String body = "";
        for (com.zerjioang.apkr.batch.task.OutPutResult item : data) {
            body += item.toWekaData() + "," + sample_type + "\r\n";
        }
        String out = header + body;
        FileIOHandler.saveFile(outputDir.getParentFile().getAbsolutePath(), outputFileName + ".arff", out.getBytes());
        return out;
    }
}

