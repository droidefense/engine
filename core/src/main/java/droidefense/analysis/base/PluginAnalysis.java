package droidefense.analysis.base;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.model.base.APKFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
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

    protected String getResultAsJson() {
        return Util.toJson(this, JsonStyle.JSON_COMPRESSED);
    }

    protected String getResultAsHTML() {
        return html;
    }
}
