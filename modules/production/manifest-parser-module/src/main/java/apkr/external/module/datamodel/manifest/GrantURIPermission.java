package apkr.external.module.datamodel.manifest;


import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class GrantURIPermission extends AbstractManifestClass {


    //object vars
    private String path;
    private String pathPattern;
    private String pathPrefix;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    @Override
    public void setParent(AbstractManifestClass parent) {

        if (this.parent == null)
            this.parent = parent;
        if (parent instanceof Provider) {
            ((Provider) this.parent).add(this);
        }
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }


}
