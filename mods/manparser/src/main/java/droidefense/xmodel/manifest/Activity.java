package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;
import droidefense.xmodel.manifest.enums.*;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Activity extends AbstractManifestClass {


    //object vars
    private boolean allowEmbebbed;
    private boolean allowTaskReparenting;
    private boolean alwaysRetainTaskState;
    private boolean autoRemoveFromRecents;
    private String banner;
    private boolean clearTaskOnLaunch;
    private ArrayList<ConfigChanges> configChanges;
    private DocumentLaunchMode documentLaunchMode;
    private boolean enabled;
    private boolean excludeFromRecents;
    private boolean exported;
    private boolean finishOnTaskLaunch;
    private boolean hardwareAccelerated;
    private String icon;
    private String label;
    private LaunchMode launchMode;
    private int maxRecents;
    private boolean multiprocess;
    private boolean noHistory;
    private String parentActivityName;
    private String permission;
    private String process;
    private boolean relinquishTaskIdentity;
    private ScreenOrientation screenOrientation;
    private boolean stateNotNeeded;
    private String taskAffinity;
    private String theme;
    private UiOptions uiOptions;
    private WindowSoftInputMode windowSoftInputMode;

    //can contain
    private ArrayList<IntentFilter> intentFilter;
    private ArrayList<Metadata> metadata;

    public Activity() {
        configChanges = new ArrayList<>();
        intentFilter = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        //add class check. for security
        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Application) {
            ((Application) this.parent).add(this);
        }
    }

    public boolean isAllowEmbebbed() {
        return allowEmbebbed;
    }

    public void setAllowEmbebbed(boolean allowEmbebbed) {
        this.allowEmbebbed = allowEmbebbed;
    }

    public boolean isAllowTaskReparenting() {
        return allowTaskReparenting;
    }

    public void setAllowTaskReparenting(boolean allowTaskReparenting) {
        this.allowTaskReparenting = allowTaskReparenting;
    }

    public boolean isAlwaysRetainTaskState() {
        return alwaysRetainTaskState;
    }

    public void setAlwaysRetainTaskState(boolean alwaysRetainTaskState) {
        this.alwaysRetainTaskState = alwaysRetainTaskState;
    }

    public boolean isAutoRemoveFromRecents() {
        return autoRemoveFromRecents;
    }

    public void setAutoRemoveFromRecents(boolean autoRemoveFromRecents) {
        this.autoRemoveFromRecents = autoRemoveFromRecents;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public boolean isClearTaskOnLaunch() {
        return clearTaskOnLaunch;
    }

    public void setClearTaskOnLaunch(boolean clearTaskOnLaunch) {
        this.clearTaskOnLaunch = clearTaskOnLaunch;
    }

    public ArrayList<ConfigChanges> getConfigChanges() {
        return configChanges;
    }

    public void setConfigChanges(ArrayList<ConfigChanges> configChanges) {
        this.configChanges = configChanges;
    }

    public DocumentLaunchMode getDocumentLaunchMode() {
        return documentLaunchMode;
    }

    public void setDocumentLaunchMode(DocumentLaunchMode documentLaunchMode) {
        this.documentLaunchMode = documentLaunchMode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExcludeFromRecents() {
        return excludeFromRecents;
    }

    public void setExcludeFromRecents(boolean excludeFromRecents) {
        this.excludeFromRecents = excludeFromRecents;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public boolean isFinishOnTaskLaunch() {
        return finishOnTaskLaunch;
    }

    public void setFinishOnTaskLaunch(boolean finishOnTaskLaunch) {
        this.finishOnTaskLaunch = finishOnTaskLaunch;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LaunchMode getLaunchMode() {
        return launchMode;
    }

    public void setLaunchMode(LaunchMode launchMode) {
        this.launchMode = launchMode;
    }

    public int getMaxRecents() {
        return maxRecents;
    }

    public void setMaxRecents(int maxRecents) {
        this.maxRecents = maxRecents;
    }

    public boolean isMultiprocess() {
        return multiprocess;
    }

    public void setMultiprocess(boolean multiprocess) {
        this.multiprocess = multiprocess;
    }

    public boolean isNoHistory() {
        return noHistory;
    }

    public void setNoHistory(boolean noHistory) {
        this.noHistory = noHistory;
    }

    public String getParentActivityName() {
        return parentActivityName;
    }

    public void setParentActivityName(String parentActivityName) {
        this.parentActivityName = parentActivityName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean isRelinquishTaskIdentity() {
        return relinquishTaskIdentity;
    }

    public void setRelinquishTaskIdentity(boolean relinquishTaskIdentity) {
        this.relinquishTaskIdentity = relinquishTaskIdentity;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public boolean isStateNotNeeded() {
        return stateNotNeeded;
    }

    public void setStateNotNeeded(boolean stateNotNeeded) {
        this.stateNotNeeded = stateNotNeeded;
    }

    public String getTaskAffinity() {
        return taskAffinity;
    }

    public void setTaskAffinity(String taskAffinity) {
        this.taskAffinity = taskAffinity;
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

    public WindowSoftInputMode getWindowSoftInputMode() {
        return windowSoftInputMode;
    }

    public void setWindowSoftInputMode(WindowSoftInputMode windowSoftInputMode) {
        this.windowSoftInputMode = windowSoftInputMode;
    }

    public ArrayList<IntentFilter> getIntentFilter() {
        return intentFilter;
    }

    public void setIntentFilter(ArrayList<IntentFilter> intentFilter) {
        this.intentFilter = intentFilter;
    }

    public ArrayList<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(ArrayList<Metadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "allowEmbebbed=" + allowEmbebbed +
                ", allowTaskReparenting=" + allowTaskReparenting +
                ", alwaysRetainTaskState=" + alwaysRetainTaskState +
                ", autoRemoveFromRecents=" + autoRemoveFromRecents +
                ", banner='" + banner + '\'' +
                ", clearTaskOnLaunch=" + clearTaskOnLaunch +
                ", configChanges=" + configChanges +
                ", documentLaunchMode=" + documentLaunchMode +
                ", enabled=" + enabled +
                ", excludeFromRecents=" + excludeFromRecents +
                ", exported=" + exported +
                ", finishOnTaskLaunch=" + finishOnTaskLaunch +
                ", hardwareAccelerated=" + hardwareAccelerated +
                ", icon='" + icon + '\'' +
                ", label='" + label + '\'' +
                ", launchMode=" + launchMode +
                ", maxRecents=" + maxRecents +
                ", multiprocess=" + multiprocess +
                ", name='" + name + '\'' +
                ", noHistory=" + noHistory +
                ", parentActivityName='" + parentActivityName + '\'' +
                ", permission='" + permission + '\'' +
                ", process='" + process + '\'' +
                ", relinquishTaskIdentity=" + relinquishTaskIdentity +
                ", screenOrientation=" + screenOrientation +
                ", stateNotNeeded=" + stateNotNeeded +
                ", taskAffinity='" + taskAffinity + '\'' +
                ", theme='" + theme + '\'' +
                ", uiOptions=" + uiOptions +
                ", windowSoftInputMode=" + windowSoftInputMode +
                ", intentFilter=" + intentFilter +
                ", metadata=" + metadata +
                '}';
    }

    public void add(IntentFilter i) {
        this.intentFilter.add(i);
    }

    public void add(Metadata m) {
        this.metadata.add(m);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
