package apkr.external.module.datamodel.manifest;

import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;
import apkr.external.module.datamodel.manifest.enums.InstallLocation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Manifest extends AbstractManifestClass {
    //reference object


    private final HashMap<String, Object> notMapped;
    //fast access
    private final ArrayList<IntentFilter> allFilters;
    private final ArrayList<Metadata> allMetadata;
    //object vars
    private String packageName;
    private String sharedUserId;
    private String sharedUserLabel;
    private String versionName;
    private int versionCode;
    private InstallLocation installLocation;
    //must contain
    private Application application;
    //can contain
    private ArrayList<CompatibleScreens> compatibleScreensList;
    private ArrayList<Instrumentation> instrumentationList;
    private ArrayList<Permission> permissionList;
    private ArrayList<PermissionGroup> permissionGroupList;
    private ArrayList<PermissionTree> permissionTreeList;
    private ArrayList<SupportGLTextures> supportsGlTextureList;
    private ArrayList<SupportsScreens> supportsScreensList;
    private ArrayList<UsesConfiguration> usesConfigurationList;
    private ArrayList<UsesFeature> usesFeatureList;
    private ArrayList<UsesPermission> usesPermissionList;
    private ArrayList<UsesPermissionSDK23> usesPermissionSdk23List;
    private ArrayList<UsesSDK> usesSdkList;

    public Manifest() {
        //must have
        application = new Application();

        //can have
        compatibleScreensList = new ArrayList<>();
        instrumentationList = new ArrayList<>();
        permissionList = new ArrayList<>();
        permissionGroupList = new ArrayList<>();
        permissionTreeList = new ArrayList<>();
        supportsGlTextureList = new ArrayList<>();
        supportsScreensList = new ArrayList<>();
        usesConfigurationList = new ArrayList<>();
        usesFeatureList = new ArrayList<>();
        usesPermissionList = new ArrayList<>();
        usesPermissionSdk23List = new ArrayList<>();
        usesSdkList = new ArrayList<>();

        //fast access
        allFilters = new ArrayList<>();
        allMetadata = new ArrayList<>();

        //for unkwnon values
        notMapped = new HashMap<>();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSharedUserId() {
        return sharedUserId;
    }

    public void setSharedUserId(String sharedUserId) {
        this.sharedUserId = sharedUserId;
    }

    public String getSharedUserLabel() {
        return sharedUserLabel;
    }

    public void setSharedUserLabel(String sharedUserLabel) {
        this.sharedUserLabel = sharedUserLabel;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public InstallLocation getInstallLocation() {
        return installLocation;
    }

    public void setInstallLocation(InstallLocation installLocation) {
        this.installLocation = installLocation;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public ArrayList<CompatibleScreens> getCompatibleScreensList() {
        return compatibleScreensList;
    }

    public void setCompatibleScreensList(ArrayList<CompatibleScreens> compatibleScreensList) {
        this.compatibleScreensList = compatibleScreensList;
    }

    public ArrayList<Instrumentation> getInstrumentationList() {
        return instrumentationList;
    }

    public void setInstrumentationList(ArrayList<Instrumentation> instrumentationList) {
        this.instrumentationList = instrumentationList;
    }

    public ArrayList<Permission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(ArrayList<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    public ArrayList<PermissionGroup> getPermissionGroupList() {
        return permissionGroupList;
    }

    public void setPermissionGroupList(ArrayList<PermissionGroup> permissionGroupList) {
        this.permissionGroupList = permissionGroupList;
    }

    public ArrayList<PermissionTree> getPermissionTreeList() {
        return permissionTreeList;
    }

    public void setPermissionTreeList(ArrayList<PermissionTree> permissionTreeList) {
        this.permissionTreeList = permissionTreeList;
    }

    public ArrayList<SupportGLTextures> getSupportsGlTextureList() {
        return supportsGlTextureList;
    }

    public void setSupportsGlTextureList(ArrayList<SupportGLTextures> supportsGlTextureList) {
        this.supportsGlTextureList = supportsGlTextureList;
    }

    public ArrayList<SupportsScreens> getSupportsScreensList() {
        return supportsScreensList;
    }

    public void setSupportsScreensList(ArrayList<SupportsScreens> supportsScreensList) {
        this.supportsScreensList = supportsScreensList;
    }

    public ArrayList<UsesConfiguration> getUsesConfigurationList() {
        return usesConfigurationList;
    }

    public void setUsesConfigurationList(ArrayList<UsesConfiguration> usesConfigurationList) {
        this.usesConfigurationList = usesConfigurationList;
    }

    public ArrayList<UsesFeature> getUsesFeatureList() {
        return usesFeatureList;
    }

    public void setUsesFeatureList(ArrayList<UsesFeature> usesFeatureList) {
        this.usesFeatureList = usesFeatureList;
    }

    public ArrayList<UsesPermission> getUsesPermissionList() {
        return usesPermissionList;
    }

    public void setUsesPermissionList(ArrayList<UsesPermission> usesPermissionList) {
        this.usesPermissionList = usesPermissionList;
    }

    public ArrayList<UsesPermissionSDK23> getUsesPermissionSdk23List() {
        return usesPermissionSdk23List;
    }

    public void setUsesPermissionSdk23List(ArrayList<UsesPermissionSDK23> usesPermissionSdk23List) {
        this.usesPermissionSdk23List = usesPermissionSdk23List;
    }

    public ArrayList<UsesSDK> getUsesSdkList() {
        return usesSdkList;
    }

    public void setUsesSdkList(ArrayList<UsesSDK> usesSdkList) {
        this.usesSdkList = usesSdkList;
    }

    public ArrayList<IntentFilter> getAllFilters() {
        return allFilters;
    }

    public ArrayList<Metadata> getAllMetadata() {
        return allMetadata;
    }

    public void addFilter(IntentFilter f) {
        this.allFilters.add(f);
    }

    public void addMetadata(Metadata m) {
        this.allMetadata.add(m);
    }

    @Override
    public void saveInMap(String key, Object value) {
        notMapped.put(key, value);
    }

    public HashMap<String, Object> getNotMapped() {
        return notMapped;
    }

    @Override
    public String toString() {
        return "Manifest{" +
                "packageName='" + packageName + '\'' +
                ", sharedUserId='" + sharedUserId + '\'' +
                ", sharedUserLabel='" + sharedUserLabel + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", installLocation=" + installLocation +
                ", application=" + application +
                ", compatibleScreensList=" + compatibleScreensList +
                ", instrumentationList=" + instrumentationList +
                ", permissionList=" + permissionList +
                ", permissionGroupList=" + permissionGroupList +
                ", permissionTreeList=" + permissionTreeList +
                ", supportsGlTextureList=" + supportsGlTextureList +
                ", supportsScreensList=" + supportsScreensList +
                ", usesConfigurationList=" + usesConfigurationList +
                ", usesFeatureList=" + usesFeatureList +
                ", usesPermissionList=" + usesPermissionList +
                ", usesPermissionSdk23List=" + usesPermissionSdk23List +
                ", usesSdkList=" + usesSdkList +
                '}';
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        //do nothing. This is the root
    }

    public void add(Application a) {
        this.application = a;
    }

    public void add(CompatibleScreens cs) {
        this.compatibleScreensList.add(cs);
    }

    public void add(Instrumentation ins) {
        this.instrumentationList.add(ins);
    }

    public void add(Permission p) {
        this.permissionList.add(p);
    }

    public void add(PermissionGroup pg) {
        this.permissionGroupList.add(pg);
    }

    public void add(PermissionTree pt) {
        this.permissionTreeList.add(pt);
    }

    public void add(SupportGLTextures gl) {
        this.supportsGlTextureList.add(gl);
    }

    public void add(SupportsScreens sc) {
        this.supportsScreensList.add(sc);
    }

    public void add(UsesConfiguration cfg) {
        this.usesConfigurationList.add(cfg);
    }

    public void add(UsesFeature f) {
        this.usesFeatureList.add(f);
    }

    public void add(UsesPermission up) {
        this.usesPermissionList.add(up);
    }

    public void add(UsesPermissionSDK23 up) {
        this.usesPermissionSdk23List.add(up);
    }

    public void add(UsesSDK s) {
        this.usesSdkList.add(s);
    }


    @Override
    public void setName(String name) {
        super.setName(name);
    }


}



