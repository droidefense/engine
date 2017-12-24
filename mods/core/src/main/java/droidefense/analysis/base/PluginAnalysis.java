package droidefense.analysis.base;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.enums.ProcessStatus;
import droidefense.sdk.util.Util;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.util.ExecutionTimer;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.sdk.util.JsonStyle;

import java.io.Serializable;

/**
 * Created by sergio on 4/9/16.
 */
public abstract class PluginAnalysis implements Serializable {

    protected transient DroidefenseProject currentProject;
    protected transient LocalApkFile apk;

    protected boolean positiveMatch;
    protected ProcessStatus status;
    protected ExecutionTimer timeStamp;
    protected String pluginName;

    public PluginAnalysis() {
        this.pluginName = getPluginName();
    }

    public void log(Object o, int level) {
        Log.write(LoggerType.TRACE, " " + o);
    }

    public LocalApkFile getApk() {
        return apk;
    }

    public void setApk(LocalApkFile apk) {
        this.apk = apk;
    }

    protected abstract String getPluginName();

    protected String getResultAsJson() {
        return Util.toJson(this, JsonStyle.JSON_COMPRESSED);
    }
}
