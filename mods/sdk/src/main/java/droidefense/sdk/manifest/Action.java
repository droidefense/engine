package droidefense.sdk.manifest;

import droidefense.sdk.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Action extends AbstractManifestClass {

    public Action() {
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof IntentFilter) {
            ((IntentFilter) this.parent).add(this);
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
