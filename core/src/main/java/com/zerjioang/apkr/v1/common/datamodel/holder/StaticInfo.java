package com.zerjioang.apkr.v1.common.datamodel.holder;


import apkr.external.module.datamodel.manifest.Manifest;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.datamodel.certificate.CertificateModel;

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
    private ArrayList<ResourceFile> appFiles;

    /**
     * .apk asset list of files
     */
    private ArrayList<ResourceFile> assetFiles;

    /**
     * .apk lib list of files
     */
    private ArrayList<ResourceFile> libFiles;

    /**
     * .apk raw list of files
     */
    private ArrayList<ResourceFile> rawFiles;

    private ResourceFile certFile;

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
    private ResourceFile manifestFile;

    /**
     * flag that indicates if .dex files are readed
     */
    private boolean dexFileReaded;

    /**
     * list of .dex files detected
     */
    private transient ArrayList<ResourceFile> dexList;

    /**
     * map that contains each of the .dex files bytes content
     */
    private transient Map<ResourceFile, byte[]> dexData;

    public StaticInfo() {
        //init data structures
        certificates = new ArrayList<>();
        appFiles = new ArrayList<>();
        dexList = new ArrayList<>();
        dexData = new HashMap<>();
    }

    //GETTERS AND SETTERS

    public ArrayList<ResourceFile> getAppFiles() {
        return appFiles;
    }

    public void setAppFiles(ArrayList<ResourceFile> appFiles) {
        this.appFiles = appFiles;
    }

    public ArrayList<ResourceFile> getAssetFiles() {
        return assetFiles;
    }

    public void setAssetFiles(ArrayList<ResourceFile> assetFiles) {
        this.assetFiles = assetFiles;
    }

    public ArrayList<ResourceFile> getRawFiles() {
        return rawFiles;
    }

    public void setRawFiles(ArrayList<ResourceFile> rawFiles) {
        this.rawFiles = rawFiles;
    }

    public ArrayList<ResourceFile> getLibFiles() {
        return libFiles;
    }

    public void setLibFiles(ArrayList<ResourceFile> libFiles) {
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

    public ResourceFile getManifestFile() {
        return manifestFile;
    }

    public void setManifestFile(ResourceFile manifestFile) {
        this.manifestFile = manifestFile;
    }

    public boolean isDexFileReaded() {
        return dexFileReaded;
    }

    public void setDexFileReaded(boolean dexFileReaded) {
        this.dexFileReaded = dexFileReaded;
    }

    public ArrayList<ResourceFile> getDexList() {
        return dexList;
    }

    public void setDexList(ArrayList<ResourceFile> dexList) {
        this.dexList = dexList;
    }

    public Map<ResourceFile, byte[]> getDexData() {
        return dexData;
    }

    public void setDexData(Map<ResourceFile, byte[]> dexData) {
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

    public byte[] getDexData(ResourceFile file) {
        return getDexData().get(file);
    }

    public void addDexData(ResourceFile file, byte[] data) {
        getDexData().put(file, data);
    }

    public void addCertInfo(CertificateModel certInfo) {
        this.certificates.add(certInfo);
    }

    public ResourceFile getCertFile() {
        return certFile;
    }

    public void setCertFile(ResourceFile certFile) {
        this.certFile = certFile;
    }
}
