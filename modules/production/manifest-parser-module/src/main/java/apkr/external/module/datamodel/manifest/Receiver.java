package apkr.external.module.datamodel.manifest;


import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Receiver extends AbstractManifestClass {


    //object vars
    private boolean enabled;
    private boolean exported;
    private String icon;
    private boolean isolatedProcess;
    private String label;
    private String permission;
    private String process;

    //can contain
    private ArrayList<IntentFilter> intentFilterList;
    private ArrayList<Metadata> metadataList;

    public Receiver() {
        intentFilterList = new ArrayList<>();
        metadataList = new ArrayList<>();
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Application) {
            ((Application) this.parent).add(this);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isolatedProcess() {
        return isolatedProcess;
    }

    public void setIsolatedProcess(boolean isolatedProcess) {
        this.isolatedProcess = isolatedProcess;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public ArrayList<IntentFilter> getIntentFilterList() {
        return intentFilterList;
    }

    public void setIntentFilterList(ArrayList<IntentFilter> intentFilterList) {
        this.intentFilterList = intentFilterList;
    }

    public ArrayList<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(ArrayList<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public void add(Metadata m) {
        this.metadataList.add(m);
    }

    public void add(IntentFilter i) {
        this.intentFilterList.add(i);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}

