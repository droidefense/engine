package apkr.external.module.datamodel.manifest;


import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Metadata extends AbstractManifestClass {


    //object reference
    //activity, activity-alias, application, provider, receiver, service

    //object vars
    private String resource;
    private String value;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;

        if (parent instanceof Activity) {
            ((Activity) this.parent).add(this);
        } else if (parent instanceof ActivityAlias) {
            ((ActivityAlias) this.parent).add(this);
        } else if (parent instanceof Application) {
            ((Application) this.parent).add(this);
        } else if (parent instanceof Provider) {
            ((Provider) this.parent).add(this);
        } else if (parent instanceof Service) {
            ((Service) this.parent).add(this);
        } else if (parent instanceof Receiver) {
            ((Receiver) this.parent).add(this);
        }

        //escalate and add in manifest too for fast access
        AbstractManifestClass currentOwner, previous;
        currentOwner = parent;
        do {
            previous = currentOwner.getParent();
            currentOwner = previous;
        } while (!(currentOwner instanceof Manifest));
        ((Manifest) currentOwner).addMetadata(this);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
