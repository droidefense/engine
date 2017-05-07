package droidefense.sdk.manifest;

import droidefense.sdk.manifest.base.AbstractManifestClass;

/**
 * Created by zerjioang on 03/03/2016.
 */
public final class Data extends AbstractManifestClass {


    //object vars
    private String scheme;
    private String host;
    private String port;
    private String path;
    private String pathPattern;
    private String pathPrefix;
    private String mimeType;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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
        super.setName(name);
    }


}
