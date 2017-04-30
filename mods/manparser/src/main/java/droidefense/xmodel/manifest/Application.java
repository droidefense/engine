package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;
import droidefense.xmodel.manifest.enums.UiOptions;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Application extends AbstractManifestClass {


    //object vars
    private boolean allowTaskReparenting;
    private boolean allowBackup;
    private String backupAgent;
    private String banner;
    private boolean debuggable;
    private String description;
    private boolean enabled;
    private boolean hasCode;
    private boolean hardwareAccelerated;
    private String icon;
    private boolean isGame;
    private boolean killAfterRestore;
    private boolean largeHeap;
    private String label;
    private String logo;
    private String manageSpaceActivity;
    private String permission;
    private boolean persistent;
    private String process;
    private boolean restoreAnyVersion;
    private String requiredAccountType;
    private String restrictedAccountType;
    private boolean supportsRtl;
    private String taskAffinity;
    private boolean testOnly;
    private String theme;
    private UiOptions uiOptions;
    private boolean usesCleartextTraffic;
    private boolean vmSafeMode;

    //can contain
    private ArrayList<Activity> activities;
    private ArrayList<ActivityAlias> alias;
    private ArrayList<Metadata> metadata;
    private ArrayList<Service> services;
    private ArrayList<Receiver> receivers;
    private ArrayList<Provider> providers;
    private ArrayList<UsesLibrary> libraries;

    public Application() {
        activities = new ArrayList<>();
        alias = new ArrayList<>();
        metadata = new ArrayList<>();
        services = new ArrayList<>();
        receivers = new ArrayList<>();
        providers = new ArrayList<>();
        libraries = new ArrayList<>();
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    public boolean isAllowTaskReparenting() {
        return allowTaskReparenting;
    }

    public void setAllowTaskReparenting(boolean allowTaskReparenting) {
        this.allowTaskReparenting = allowTaskReparenting;
    }

    public boolean isAllowBackup() {
        return allowBackup;
    }

    public void setAllowBackup(boolean allowBackup) {
        this.allowBackup = allowBackup;
    }

    public String getBackupAgent() {
        return backupAgent;
    }

    public void setBackupAgent(String backupAgent) {
        this.backupAgent = backupAgent;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public void setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHasCode() {
        return hasCode;
    }

    public void setHasCode(boolean hasCode) {
        this.hasCode = hasCode;
    }

    public boolean isHardwareAccelerated() {
        return hardwareAccelerated;
    }

    public void setHardwareAccelerated(boolean hardwareAccelerated) {
        this.hardwareAccelerated = hardwareAccelerated;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isGame() {
        return isGame;
    }

    public void setIsGame(boolean isGame) {
        this.isGame = isGame;
    }

    public boolean isKillAfterRestore() {
        return killAfterRestore;
    }

    public void setKillAfterRestore(boolean killAfterRestore) {
        this.killAfterRestore = killAfterRestore;
    }

    public boolean isLargeHeap() {
        return largeHeap;
    }

    public void setLargeHeap(boolean largeHeap) {
        this.largeHeap = largeHeap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getManageSpaceActivity() {
        return manageSpaceActivity;
    }

    public void setManageSpaceActivity(String manageSpaceActivity) {
        this.manageSpaceActivity = manageSpaceActivity;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean isRestoreAnyVersion() {
        return restoreAnyVersion;
    }

    public void setRestoreAnyVersion(boolean restoreAnyVersion) {
        this.restoreAnyVersion = restoreAnyVersion;
    }

    public String getRequiredAccountType() {
        return requiredAccountType;
    }

    public void setRequiredAccountType(String requiredAccountType) {
        this.requiredAccountType = requiredAccountType;
    }

    public String getRestrictedAccountType() {
        return restrictedAccountType;
    }

    public void setRestrictedAccountType(String restrictedAccountType) {
        this.restrictedAccountType = restrictedAccountType;
    }

    public boolean isSupportsRtl() {
        return supportsRtl;
    }

    public void setSupportsRtl(boolean supportsRtl) {
        this.supportsRtl = supportsRtl;
    }

    public String getTaskAffinity() {
        return taskAffinity;
    }

    public void setTaskAffinity(String taskAffinity) {
        this.taskAffinity = taskAffinity;
    }

    public boolean isTestOnly() {
        return testOnly;
    }

    public void setTestOnly(boolean testOnly) {
        this.testOnly = testOnly;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public UiOptions getUiOptions() {
        return uiOptions;
    }

    public void setUiOptions(UiOptions uiOptions) {
        this.uiOptions = uiOptions;
    }

    public boolean isUsesCleartextTraffic() {
        return usesCleartextTraffic;
    }

    public void setUsesCleartextTraffic(boolean usesCleartextTraffic) {
        this.usesCleartextTraffic = usesCleartextTraffic;
    }

    public boolean isVmSafeMode() {
        return vmSafeMode;
    }

    public void setVmSafeMode(boolean vmSafeMode) {
        this.vmSafeMode = vmSafeMode;
    }

    public ArrayList<Activity> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<Activity> activities) {
        this.activities = activities;
    }

    public ArrayList<ActivityAlias> getAlias() {
        return alias;
    }

    public void setAlias(ArrayList<ActivityAlias> alias) {
        this.alias = alias;
    }

    public ArrayList<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<Metadata> metadata) {
        this.metadata = metadata;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ArrayList<Receiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(ArrayList<Receiver> receivers) {
        this.receivers = receivers;
    }

    public ArrayList<Provider> getProviders() {
        return providers;
    }

    public void setProviders(ArrayList<Provider> providers) {
        this.providers = providers;
    }

    public ArrayList<UsesLibrary> getLibraries() {
        return libraries;
    }

    public void setLibraries(ArrayList<UsesLibrary> libraries) {
        this.libraries = libraries;
    }

    @Override
    public String toString() {
        return "Application{" +
                "allowTaskReparenting=" + allowTaskReparenting +
                ", allowBackup=" + allowBackup +
                ", backupAgent='" + backupAgent + '\'' +
                ", banner='" + banner + '\'' +
                ", debuggable=" + debuggable +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", hasCode=" + hasCode +
                ", hardwareAccelerated=" + hardwareAccelerated +
                ", icon='" + icon + '\'' +
                ", isGame=" + isGame +
                ", killAfterRestore=" + killAfterRestore +
                ", largeHeap=" + largeHeap +
                ", label='" + label + '\'' +
                ", logo='" + logo + '\'' +
                ", manageSpaceActivity='" + manageSpaceActivity + '\'' +
                ", name='" + name + '\'' +
                ", permission='" + permission + '\'' +
                ", persistent=" + persistent +
                ", process='" + process + '\'' +
                ", restoreAnyVersion=" + restoreAnyVersion +
                ", requiredAccountType='" + requiredAccountType + '\'' +
                ", restrictedAccountType='" + restrictedAccountType + '\'' +
                ", supportsRtl=" + supportsRtl +
                ", taskAffinity='" + taskAffinity + '\'' +
                ", testOnly=" + testOnly +
                ", theme='" + theme + '\'' +
                ", uiOptions=" + uiOptions +
                ", usesCleartextTraffic=" + usesCleartextTraffic +
                ", vmSafeMode=" + vmSafeMode +
                ", activities=" + activities +
                ", alias=" + alias +
                ", metadata=" + metadata +
                ", services=" + services +
                ", receivers=" + receivers +
                ", providers=" + providers +
                ", libraries=" + libraries +
                '}';
    }

    public void add(Activity activity) {
        this.activities.add(activity);
    }

    public void add(ActivityAlias alias) {
        this.alias.add(alias);
    }

    public void add(Metadata m) {
        this.metadata.add(m);
    }

    public void add(Service s) {
        this.services.add(s);
    }

    public void add(Receiver r) {
        this.receivers.add(r);
    }

    public void add(Provider p) {
        this.providers.add(p);
    }

    public void add(UsesLibrary l) {
        this.libraries.add(l);
    }


    @Override
    public void setName(String name) {
        super.setName(name);
    }


}