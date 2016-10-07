package com.zerjioang.apkr.sdk.model.holder;


import apkr.external.module.datamodel.manifest.Manifest;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.certificate.CertificateModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 18/2/16.
 */
public class StaticInfo implements Serializable {

    /**
     * .apk list of files
     */
    private ArrayList<ApkrFile> appFiles;

    /**
     * .apk asset list of files
     */
    private ArrayList<ApkrFile> assetFiles;

    /**
     * .apk lib list of files
     */
    private ArrayList<ApkrFile> libFiles;

    /**
     * .apk raw list of files
     */
    private ArrayList<ApkrFile> rawFiles;

    private ApkrFile certFile;

    /**
     * In case of existance, the name of the main class
     */
    private String mainClassName;
    /**
     * number of .dex files inside .apk
     */
    private int numberOfDexFiles;

    /**
     * apk certificate number
     */
    private int certNumber;

    /**
     * apk certificates info
     */
    private ArrayList<CertificateModel> certificates;

    /**
     * Parsed manifest info
     */
    private Manifest manifestInfo;

    /**
     * flag that indicates if manifest classnames has package name with them
     */
    private boolean classNameWithPkgName;

    /**
     * Location of the currentProject folder on HDD
     */
    private transient String projectFolderName;

    /**
     * .apk file number
     */
    private int filesNumber;

    /**
     * .apk folder number
     */
    private int foldersNumber;

    /**
     * Reference to AndroidManifest.xml file
     */
    private ApkrFile manifestFile;

    /**
     * flag that indicates if .dex files are readed
     */
    private boolean dexFileReaded;

    /**
     * list of .dex files detected
     */
    private transient ArrayList<ApkrFile> dexList;

    /**
     * map that contains each of the .dex files bytes content
     */
    private transient Map<ApkrFile, byte[]> dexData;

    public StaticInfo() {
        //init data structures
        certificates = new ArrayList<>();
        appFiles = new ArrayList<>();
        dexList = new ArrayList<>();
        dexData = new HashMap<>();
    }

    //GETTERS AND SETTERS

    public ArrayList<ApkrFile> getAppFiles() {
        return appFiles;
    }

    public void setAppFiles(ArrayList<ApkrFile> appFiles) {
        this.appFiles = appFiles;
    }

    public ArrayList<ApkrFile> getAssetFiles() {
        return assetFiles;
    }

    public void setAssetFiles(ArrayList<ApkrFile> assetFiles) {
        this.assetFiles = assetFiles;
    }

    public ArrayList<ApkrFile> getRawFiles() {
        return rawFiles;
    }

    public void setRawFiles(ArrayList<ApkrFile> rawFiles) {
        this.rawFiles = rawFiles;
    }

    public ArrayList<ApkrFile> getLibFiles() {
        return libFiles;
    }

    public void setLibFiles(ArrayList<ApkrFile> libFiles) {
        this.libFiles = libFiles;
    }

    public String getMainClassName() {
        if (classNameWithPkgName)
            return mainClassName;
        return getManifestInfo().getPackageName() + mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public int getNumberOfDexFiles() {
        return numberOfDexFiles;
    }

    public void setNumberOfDexFiles(int numberOfDexFiles) {
        this.numberOfDexFiles = numberOfDexFiles;
    }

    public int getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(int certNumber) {
        this.certNumber = certNumber;
    }

    public ArrayList<CertificateModel> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<CertificateModel> certificates) {
        this.certificates = certificates;
    }

    public Manifest getManifestInfo() {
        return manifestInfo;
    }

    public void setManifestInfo(Manifest manifestInfo) {
        this.manifestInfo = manifestInfo;
    }

    public boolean isClassNameWithPkgName() {
        return classNameWithPkgName;
    }

    public void setClassNameWithPkgName(boolean classNameWithPkgName) {
        this.classNameWithPkgName = classNameWithPkgName;
    }

    public String getProjectFolderName() {
        return projectFolderName;
    }

    public void setProjectFolderName(String projectFolderName) {
        this.projectFolderName = projectFolderName;
    }

    public int getFilesNumber() {
        return filesNumber;
    }

    public void setFilesNumber(int filesNumber) {
        this.filesNumber = filesNumber;
    }

    public int getFoldersNumber() {
        return foldersNumber;
    }

    public void setFoldersNumber(int foldersNumber) {
        this.foldersNumber = foldersNumber;
    }

    public ApkrFile getManifestFile() {
        return manifestFile;
    }

    public void setManifestFile(ApkrFile manifestFile) {
        this.manifestFile = manifestFile;
    }

    public boolean isDexFileReaded() {
        return dexFileReaded;
    }

    public void setDexFileReaded(boolean dexFileReaded) {
        this.dexFileReaded = dexFileReaded;
    }

    public ArrayList<ApkrFile> getDexList() {
        return dexList;
    }

    public void setDexList(ArrayList<ApkrFile> dexList) {
        this.dexList = dexList;
    }

    public Map<ApkrFile, byte[]> getDexData() {
        return dexData;
    }

    public void setDexData(Map<ApkrFile, byte[]> dexData) {
        this.dexData = dexData;
    }

    //TOSTRING


    @Override
    public String toString() {
        return "StaticInfo{" +
                ", appFiles=" + appFiles +
                ", mainClassName='" + mainClassName + '\'' +
                ", numberOfDexFiles=" + numberOfDexFiles +
                ", certNumber=" + certNumber +
                ", certificates=" + certificates +
                ", manifestInfo=" + manifestInfo +
                ", classNameWithPkgName=" + classNameWithPkgName +
                ", projectFolderName='" + projectFolderName + '\'' +
                ", filesNumber=" + filesNumber +
                ", foldersNumber=" + foldersNumber +
                ", manifestFile=" + manifestFile +
                ", dexFileReaded=" + dexFileReaded +
                ", dexList=" + dexList +
                ", dexData=" + dexData +
                '}';
    }

    //useful methods

    public byte[] getDexData(ApkrFile file) {
        return getDexData().get(file);
    }

    public void addDexData(ApkrFile file, byte[] data) {
        getDexData().put(file, data);
    }

    public void addCertInfo(CertificateModel certInfo) {
        this.certificates.add(certInfo);
    }

    public ApkrFile getCertFile() {
        return certFile;
    }

    public void setCertFile(ApkrFile certFile) {
        this.certFile = certFile;
    }
}
