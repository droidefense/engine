package com.zerjioang.apkr.v2.plugins.sdk;

import com.zerjioang.apkr.v1.common.datamodel.base.APKFile;

/**
 * Created by sergio on 4/9/16.
 */
public class PluginAnalysis {

    protected transient APKFile apk;

    public void log(Object o, int level) {
        //todo fin
    }

    public APKFile getApk() {
        return apk;
    }

    public void setApk(APKFile apk) {
        this.apk = apk;
    }
}
