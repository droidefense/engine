package droidefense.analysis.base;

import droidefense.sdk.enums.ProcessStatus;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.util.JsonStyle;

import java.io.Serializable;

/**
 * Created by sergio on 4/9/16.
 */
public abstract class PluginAnalysis implements Serializable {

    protected transient DroidefenseProject currentProject;
    protected boolean positiveMatch;
    protected ProcessStatus status;
    protected ExecutionTimer timeStamp;
    protected transient LocalApkFile apk;
    protected String html;
    protected String pluginName;

    public PluginAnalysis() {
        this.pluginName = getPluginName();
        html = "";
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

    protected String getResultAsHTML() {
        return html;
    }
}
