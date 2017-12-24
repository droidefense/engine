package droidefense.sdk.model.holder;


import droidefense.sdk.holder.SDKCompatibility;
import droidefense.sdk.manifest.Manifest;
import droidefense.sdk.model.certificate.CertificateModel;
import droidefense.sdk.model.dex.DexBodyModel;
import droidefense.sdk.enums.SDK_VERSION;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.io.DexHashedFile;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 18/2/16.
 */
public class StaticInfo implements Serializable {

    /**
     * in-memory unpacked .apk list of files
     */
    private transient ArrayList<VirtualFile> appFiles;

    /**
     * local storage unpacked .apk list of files
     */
    private transient ArrayList<AbstractHashedFile> localAppFiles;

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
     * list of other developer made files
     */
    private ArrayList<AbstractHashedFile> otherFiles;

    /**
     * List of default android generated files
     */
    private ArrayList<AbstractHashedFile> defaultFiles;

    /**
     * List of internal xml files
     */
    private ArrayList<VirtualFile> xmlFiles;

    /**
     * List of internal nine patch image files
     */
    private ArrayList<VirtualFile> ninePatchImageFiles;

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
     * name/version of the tool used to build manifest.mf file
     */
    private String metaManifestCreator;

    /**
     * version of the manifest.mf file
     */
    private String metaManifestVersion;

    /**
     * apk certificates info
     */
    private ArrayList<CertificateModel> certificates;

    /**
     * Parsed droidefense.sdk.manifest info
     */
    private Manifest manifestInfo;

    /**
     * Readed metainf-manifest file
     */
    private AbstractHashedFile metainfManifestFile;

    /**
     * flag that indicates if droidefense.sdk.manifest classnames has package name with them
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
    private transient Map<String, DexHashedFile> dexList;

    /**
     * SDK compatibility window
     */
    private SDKCompatibility compatibilityWindow;

    /**
     * Dex file body model
     */
    private DexBodyModel dexBodyModel;

    public StaticInfo() {
        //init data structures
        certificates = new ArrayList<>();
        appFiles = new ArrayList<>();
        dexList = new HashMap<>();
        compatibilityWindow = new SDKCompatibility();
    }

    //GETTERS AND SETTERS

    public ArrayList<VirtualFile> getAppFiles() {
        return appFiles;
    }

    public void setAppFiles(ArrayList<VirtualFile> appFiles) {
        this.appFiles = appFiles;
    }

    public void setLocalAppFiles(ArrayList<AbstractHashedFile> appFiles) {
        this.localAppFiles = appFiles;
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
                '}';
    }

    //useful methods

    public byte[] getDexData(DexHashedFile file) throws IOException {
        AbstractHashedFile recovered = this.dexList.get(file.getName());
        return recovered.getContent();
    }

    public void addDexData(String name, DexHashedFile file) {
        this.dexList.put(name, file);
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

    public ArrayList<AbstractHashedFile> getResourceFiles() {
        ArrayList<AbstractHashedFile> list = getAssetFiles();
        list.addAll(getRawFiles());
        return list;
    }

    public void setTarget(SDK_VERSION sdkVersion) {
        this.compatibilityWindow.setTarget(sdkVersion);
    }

    public void setMinimum(SDK_VERSION sdkVersion) {
        this.compatibilityWindow.setMinimum(sdkVersion);
    }

    public void setMaximum(SDK_VERSION sdkVersion) {
        this.compatibilityWindow.setMaximum(sdkVersion);
    }

    public ArrayList<DexHashedFile> getDexList() {
        return new ArrayList<>(dexList.values());
    }

    public void setDexList(ArrayList<DexHashedFile> dexList) {
        this.dexList.clear();
        for (DexHashedFile ahf : dexList) {
            this.dexList.put(ahf.getName(), ahf);
        }
    }

    public void addDexBodyModel(DexBodyModel dexBodyModel) {
        this.dexBodyModel = dexBodyModel;
    }

    public ArrayList<AbstractHashedFile> getDefaultFiles() {
        return defaultFiles;
    }

    public void setDefaultFiles(ArrayList<AbstractHashedFile> defaultFiles) {
        this.defaultFiles = defaultFiles;
    }

    public ArrayList<VirtualFile> getXmlFiles() {
        return xmlFiles;
    }

    public void setXmlFiles(ArrayList<VirtualFile> xmlFiles) {
        this.xmlFiles = xmlFiles;
    }

    public ArrayList<VirtualFile> getNinePatchImageFiles() {
        return ninePatchImageFiles;
    }

    public void setNinePatchImageFiles(ArrayList<VirtualFile> ninePatchImageFiles) {
        this.ninePatchImageFiles = ninePatchImageFiles;
    }

    public AbstractHashedFile getMetainfManifestFile() {
        return metainfManifestFile;
    }

    public void setMetainfManifestFile(AbstractHashedFile metainfManifestFile) {
        this.metainfManifestFile = metainfManifestFile;
    }

    public String getMetaManifestCreator() {
        return metaManifestCreator;
    }

    public void setMetaManifestCreator(String metaManifestCreator) {
        this.metaManifestCreator = metaManifestCreator;
    }

    public String getMetaManifestVersion() {
        return metaManifestVersion;
    }

    public void setMetaManifestVersion(String metaManifestVersion) {
        this.metaManifestVersion = metaManifestVersion;
    }
}
