package apkr.external.module.datamodel.manifest.base;

import apkr.external.module.datamodel.manifest.Manifest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by sergio on 8/3/16.
 */
@JsonIgnoreProperties(value = { "parent" })
public abstract class AbstractManifestClass implements Serializable {

    public static final boolean PRESENT_IN_MANIFEST = true;
    public static final boolean NOT_IN_MANIFEST = false;

    protected String name;
    protected transient AbstractManifestClass parent;


    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    private void add(AbstractManifestClass manifestClass) {
        if (this.parent != null) {
            this.parent.add(manifestClass);
        }
    }

    public AbstractManifestClass getParent() {
        return parent;
    }

    public void setParent(AbstractManifestClass parent) {
        if (this.parent == null)
            this.parent = parent;
        this.parent.add(this);
    }

    protected void saveInMap(String key, Object value) {
        //1 escalate and go to manifest object
        //escalate and add in manifest too for fast access
        AbstractManifestClass currentOwner, previous;
        currentOwner = parent;
        do {
            previous = currentOwner.getParent();
            currentOwner = previous;
        } while (!(currentOwner instanceof Manifest));
        currentOwner.saveInMap(key, value);
    }
}
