package droidefense.pscout;

import java.io.Serializable;

public final class PScoutItem implements Serializable {

    private static final boolean USER_PARAMS_AS_KEY = false;
    private String callerClass, callerMethod, callerMethodDesc, permission, version;

    //Constructor
    public PScoutItem(String callerClass, String callerMethod, String callerMethodDesc, String permission, String version) {
        this.callerClass = callerClass;
        this.callerMethod = callerMethod;
        this.callerMethodDesc = callerMethodDesc;
        this.permission = permission;
        this.version = version;
    }

    //GETTERS AND SETTERS

    public String getCallerClass() {
        return callerClass;
    }

    public void setCallerClass(String callerClass) {
        this.callerClass = callerClass;
    }

    public String getCallerMethod() {
        return callerMethod;
    }

    public void setCallerMethod(String callerMethod) {
        this.callerMethod = callerMethod;
    }

    public String getCallerMethodDesc() {
        return callerMethodDesc;
    }

    public void setCallerMethodDesc(String callerMethodDesc) {
        this.callerMethodDesc = callerMethodDesc;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        if (USER_PARAMS_AS_KEY) {
            return getCallerClass() + "." + getCallerMethod() + getCallerMethodDesc();
        }
        return getCallerClass() + "." + getCallerMethod();
    }


}
