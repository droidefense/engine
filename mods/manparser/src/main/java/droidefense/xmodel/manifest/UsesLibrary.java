package droidefense.xmodel.manifest;

import droidefense.xmodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class UsesLibrary extends AbstractManifestClass {


    private boolean required;

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Application) {
            ((Application) this.parent).add(this);
        }
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
