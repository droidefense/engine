package apkr.external.module.datamodel.manifest;


import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class UsesPermission extends AbstractManifestClass {

    //object vars
    private int maxSdkVersion;

    public int getMaxSdkVersion() {
        return maxSdkVersion;
    }

    public void setMaxSdkVersion(int maxSdkVersion) {
        this.maxSdkVersion = maxSdkVersion;
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }
}
