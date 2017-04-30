package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class ActivityAlias extends AbstractManifestClass {


    //object vars
    private boolean enabled;
    private boolean exported;
    private String icon;
    private String label;
    private String permission;
    private String targetActivity;

    //can contain
    private ArrayList<IntentFilter> intentFilter;
    private ArrayList<Metadata> metadata;

    public ActivityAlias() {
        intentFilter = new ArrayList<>();
        metadata = new ArrayList<>();
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

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
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
        return "ActivityAlias{" +
                "enabled=" + enabled +
                ", exported=" + exported +
                ", icon='" + icon + '\'' +
                ", label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", permission='" + permission + '\'' +
                ", targetActivity='" + targetActivity + '\'' +
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