package com.zerjioang.apkr.batch.task;

import apkr.external.module.batch.base.IBatchTask;
import apkr.external.module.batch.base.IWekaGenerator;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.DirScannerHandler;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.handler.FileUnzipHandler;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.handler.base.DirScannerFilter;
import com.zerjioang.apkr.parser.AndroidCertParser;
import com.zerjioang.apkr.parser.base.ParserFactory;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.certificate.CertificateModel;
import com.zerjioang.apkr.sdk.model.certificate.CertificateSubject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 3/6/16.
 */
public class WekaCertTask implements IBatchTask, IWekaGenerator, Serializable {

    private static int counter = 0;
    private final File outputDir;
    private final String outputFileName;
    private final String sample_type;
    private File baseFolder;
    private ArrayList<ApkrFile> files;
    private ArrayList<File> certList;
    private boolean cont;

    //result
    private ArrayList<CertificateModel> data;

    public WekaCertTask(File baseFolder, File outputDir, String outputFileName, String sample_type) {
        this.baseFolder = baseFolder;
        this.outputFileName = outputFileName;
        this.sample_type = sample_type;
        this.outputDir = new File(outputDir.getAbsolutePath() + File.separator + "batch" + File.separator + getTaskIdName());
        certList = new ArrayList<>();
        data = new ArrayList<>();
    }

    @Override
    public void beforeTask() {
        System.out.println("---");
        System.out.println(getTaskName());
        System.out.println("---");
        System.out.println("Loading .apk files to extract certificate features.");
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
        }
    }

    @Override
    public void onTask() {
        createDir();
        if (cont) {
            for (int i = 0; i < files.size(); i++) {
                if (i == 500)
                    break;
                System.out.println("Unpacking file " + i + " of" + files.size());
                //only unpacks
                File out = new File(outputDir.getAbsolutePath() + File.separator + i);
                if (!out.exists()) {
                    createDir();
                    AbstractHandler handler = new FileUnzipHandler(files.get(i), out);
                    handler.doTheJob();
                }
                DirScannerHandler handler = new DirScannerHandler(out, false, new DirScannerFilter() {
                    @Override
                    public boolean addFile(File f) {
                        return f.getName().toLowerCase().endsWith(".rsa");
                    }
                });
                handler.doTheJob();
                ArrayList<ApkrFile> certs = handler.getFiles();
                for (ApkrFile r : certs) {
                    certList.add(r.getThisFile());
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
            for (File cert : certList) {
                if (cert.exists() && cert.isFile() && cert.canRead()) {
                    //parse cert file
                    AndroidCertParser parser = (AndroidCertParser) ParserFactory.getParser(ParserFactory.CERTIFICATE_PARSER);
                    try {
                        parser.extractCertInfo(cert);
                        Log.write(LoggerType.TRACE, "Generating file juicy information...");
                        CertificateModel model = parser.getCertInfo();
                        data.add(model);
                        counter++;
                    } catch (IOException e) {
                        e.printStackTrace();
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
        return "Android certificate feature (subject items length) extraction task";
    }

    @Override
    public String getTaskIdName() {
        return "perm-feat-extract";
    }

    @Override
    public String toWekaData() {
        //convert data to weka format
        String[] names = {"commonName", "organizationalUnit", "organization", "locality", "stateOrProvinceName", "countryName"};
        String header = "@relation relation\r\n";
        header += "\r\n";
        for (String attr : names) {
            header += "@attribute " + attr + " string\r\n";
        }
        header += "\r\n";
        header += "@attribute class {WEIRD, NORMAL} \r\n";
        header += "\r\n";
        header += "@data\r\n";
        header += "\r\n";
        String body = "";
        for (CertificateModel model : data) {
            body += getItemData(model) + "," + sample_type + "\r\n";
        }
        String out = header + body;
        FileIOHandler.saveFile(outputDir.getParentFile().getAbsolutePath(), outputFileName + ".arff", out.getBytes());
        return out;
    }

    private String getItemData(CertificateModel model) {
        CertificateSubject s = model.getSubject();
        return s.getCommonName()
                + "," + s.getCountryName()
                + "," + s.getLocality()
                + "," + s.getOrganization()
                + "," + s.getOrganizationalUnit()
                + "," + s.getStateOrProvinceName();
    }
}

