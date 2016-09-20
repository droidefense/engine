package apkr.external.module.datamodel.manifest;


import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class UsesFeature extends AbstractManifestClass {


    private boolean required;
    private int glEsVersion;

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Manifest) {
            ((Manifest) this.parent).add(this);
        }
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getGlEsVersion() {
        return glEsVersion;
    }

    public void setGlEsVersion(int glEsVersion) {
        this.glEsVersion = glEsVersion;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
