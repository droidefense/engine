package com.zerjioang.apkr.v2.plugins.sdk;

import com.zerjioang.apkr.v1.common.datamodel.base.APKFile;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.Serializable;

/**
 * Created by sergio on 4/9/16.
 */
public abstract class PluginAnalysis implements Serializable {

    protected transient ApkrProject currentProject;
    protected boolean positiveMatch;
    protected ProcessStatus status;
    protected AtomTimeStamp timeStamp;
    protected transient APKFile apk;
    protected String html;
    protected String pluginName;

    public PluginAnalysis() {
        this.pluginName = getPluginName();
        html = "";
    }

    public void log(Object o, int level) {
        Log.write(LoggerType.TRACE, level + " " + o);
    }

    public APKFile getApk() {
        return apk;
    }

    public void setApk(APKFile apk) {
        this.apk = apk;
    }

    protected abstract String getPluginName();
}
