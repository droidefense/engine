package droidefense.sdk.manifest;

import droidefense.sdk.manifest.base.AbstractManifestClass;

import java.util.ArrayList;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Provider extends AbstractManifestClass {


    //object vars
    private ArrayList<String> authorities;
    private boolean enabled;
    private boolean exported;
    private boolean grantUriPermissions;
    private String icon;
    private int initOrder;
    private String label;
    private boolean multiprocess;
    private String permission;
    private String process;
    private String readPermission;
    private boolean syncable;
    private String writePermission;

    //can contain
    private ArrayList<Metadata> metadataList;
    private ArrayList<GrantURIPermission> grantURIPermissionList;
    private ArrayList<PathPermission> pathPermissionList;

    public Provider() {
        authorities = new ArrayList<>();
        metadataList = new ArrayList<>();
        grantURIPermissionList = new ArrayList<>();
        pathPermissionList = new ArrayList<>();
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Application) {
            ((Application) this.parent).add(this);
        }
    }

    public ArrayList<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(ArrayList<String> authorities) {
        this.authorities = authorities;
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

    public boolean isGrantUriPermissions() {
        return grantUriPermissions;
    }

    public void setGrantUriPermissions(boolean grantUriPermissions) {
        this.grantUriPermissions = grantUriPermissions;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getInitOrder() {
        return initOrder;
    }

    public void setInitOrder(int initOrder) {
        this.initOrder = initOrder;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isMultiprocess() {
        return multiprocess;
    }

    public void setMultiprocess(boolean multiprocess) {
        this.multiprocess = multiprocess;
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

    public String getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(String readPermission) {
        this.readPermission = readPermission;
    }

    public boolean isSyncable() {
        return syncable;
    }

    public void setSyncable(boolean syncable) {
        this.syncable = syncable;
    }

    public String getWritePermission() {
        return writePermission;
    }

    public void setWritePermission(String writePermission) {
        this.writePermission = writePermission;
    }

    public ArrayList<Metadata> getMetadataList() {
        return metadataList;
    }

    public void setMetadataList(ArrayList<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public ArrayList<GrantURIPermission> getGrantURIPermissionList() {
        return grantURIPermissionList;
    }

    public void setGrantURIPermissionList(ArrayList<GrantURIPermission> grantURIPermissionList) {
        this.grantURIPermissionList = grantURIPermissionList;
    }

    public ArrayList<PathPermission> getPathPermissionList() {
        return pathPermissionList;
    }

    public void setPathPermissionList(ArrayList<PathPermission> pathPermissionList) {
        this.pathPermissionList = pathPermissionList;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "authorities=" + authorities +
                ", enabled=" + enabled +
                ", exported=" + exported +
                ", grantUriPermissions=" + grantUriPermissions +
                ", icon='" + icon + '\'' +
                ", initOrder=" + initOrder +
                ", label='" + label + '\'' +
                ", multiprocess=" + multiprocess +
                ", name='" + name + '\'' +
                ", permission='" + permission + '\'' +
                ", process='" + process + '\'' +
                ", readPermission='" + readPermission + '\'' +
                ", syncable=" + syncable +
                ", writePermission='" + writePermission + '\'' +
                ", metadataList=" + metadataList +
                ", grantURIPermissionList=" + grantURIPermissionList +
                ", pathPermissionList=" + pathPermissionList +
                '}';
    }

    public void add(PathPermission p) {
        this.pathPermissionList.add(p);
    }

    public void add(Metadata m) {
        this.metadataList.add(m);
    }

    public void add(GrantURIPermission gp) {
        this.grantURIPermissionList.add(gp);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
