package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;
import droidefense.xmodel.manifest.enums.ProtectionLevel;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Permission extends AbstractManifestClass {


    //object vars
    private String description;
    private String icon;
    private String label;
    private String permissionGroup;
    private ProtectionLevel protectionLevel;

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public ProtectionLevel getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(ProtectionLevel protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
