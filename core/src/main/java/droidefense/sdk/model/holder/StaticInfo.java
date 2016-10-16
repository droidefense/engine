package droidefense.sdk.model.holder;


import droidefense.mod.vfs.model.impl.VirtualFile;
import droidefense.sdk.model.certificate.CertificateModel;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.manifest.Manifest;

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
    private ArrayList<VirtualFile> appFiles;

    /**
     * .apk asset list of files
     */
    private ArrayList<AbstractHashedFile> assetFiles;

    /**
     * .apk lib list of files
     */
    private ArrayList<AbstractHashedFile> libFiles;

    /**
     * .apk raw list of files
     */
    private ArrayList<AbstractHashedFile> rawFiles;

    /**
     * list of other files
     */
    private ArrayList<AbstractHashedFile> otherFiles;

    private AbstractHashedFile certFile;

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
    private AbstractHashedFile manifestFile;

    /**
     * flag that indicates if .dex files are readed
     */
    private boolean dexFileReaded;

    /**
     * list of .dex files detected
     */
    private transient ArrayList<AbstractHashedFile> dexList;

    /**
     * map that contains each of the .dex files bytes content
     */
    private transient Map<AbstractHashedFile, byte[]> dexData;

    public StaticInfo() {
        //init data structures
        certificates = new ArrayList<>();
        appFiles = new ArrayList<>();
        dexList = new ArrayList<>();
        dexData = new HashMap<>();
    }

    //GETTERS AND SETTERS

    public ArrayList<VirtualFile> getAppFiles() {
        return appFiles;
    }

    public void setAppFiles(ArrayList<VirtualFile> appFiles) {
        this.appFiles = appFiles;
    }

    public ArrayList<AbstractHashedFile> getAssetFiles() {
        return assetFiles;
    }

    public void setAssetFiles(ArrayList<AbstractHashedFile> assetFiles) {
        this.assetFiles = assetFiles;
    }

    public ArrayList<AbstractHashedFile> getRawFiles() {
        return rawFiles;
    }

    public void setRawFiles(ArrayList<AbstractHashedFile> rawFiles) {
        this.rawFiles = rawFiles;
    }

    public ArrayList<AbstractHashedFile> getLibFiles() {
        return libFiles;
    }

    public void setLibFiles(ArrayList<AbstractHashedFile> libFiles) {
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

    public AbstractHashedFile getManifestFile() {
        return manifestFile;
    }

    public void setManifestFile(AbstractHashedFile manifestFile) {
        this.manifestFile = manifestFile;
    }

    public boolean isDexFileReaded() {
        return dexFileReaded;
    }

    public void setDexFileReaded(boolean dexFileReaded) {
        this.dexFileReaded = dexFileReaded;
    }

    public ArrayList<AbstractHashedFile> getDexList() {
        return dexList;
    }

    public void setDexList(ArrayList<AbstractHashedFile> dexList) {
        this.dexList = dexList;
    }

    public Map<AbstractHashedFile, byte[]> getDexData() {
        return dexData;
    }

    public void setDexData(Map<AbstractHashedFile, byte[]> dexData) {
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

    public byte[] getDexData(AbstractHashedFile file) {
        return getDexData().get(file);
    }

    public void addDexData(AbstractHashedFile file, byte[] data) {
        getDexData().put(file, data);
    }

    public void addCertInfo(CertificateModel certInfo) {
        this.certificates.add(certInfo);
    }

    public AbstractHashedFile getCertFile() {
        return certFile;
    }

    public void setCertFile(AbstractHashedFile certFile) {
        this.certFile = certFile;
    }

    public ArrayList<AbstractHashedFile> getOtherFiles() {
        return otherFiles;
    }

    public void setOtherFiles(ArrayList<AbstractHashedFile> otherFiles) {
        this.otherFiles = otherFiles;
    }
}
