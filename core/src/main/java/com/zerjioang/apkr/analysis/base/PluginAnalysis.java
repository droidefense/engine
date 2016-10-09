package com.zerjioang.apkr.analysis.base;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.sdk.model.base.APKFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

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
