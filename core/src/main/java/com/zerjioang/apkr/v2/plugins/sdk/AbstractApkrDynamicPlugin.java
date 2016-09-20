package com.zerjioang.apkr.v2.plugins.sdk;


import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;

/**
 * Created by sergio on 18/2/16.
 */
public abstract class AbstractApkrDynamicPlugin extends PluginAnalysis {

    protected String html;
    protected boolean positiveMatch;
    protected transient ApkrProject currentProject;
    private ProcessStatus status;
    private AtomTimeStamp timeStamp;

    public void analyze() {
        status = ProcessStatus.WAITING;
        this.timeStamp = new AtomTimeStamp();
        this.positiveMatch = false;
        status = ProcessStatus.STARTED;
        //pre execute
        onPreExecute();
        status = ProcessStatus.EXECUTING;
        //execute
        onExecute();
        //post execute
        postExecute();
        status = ProcessStatus.FINISHED;
        //stop time counter
        this.timeStamp.stop();
    }

    protected abstract void onPreExecute();

    protected abstract void onExecute();

    protected abstract void postExecute();

    protected abstract String getPluginName();

    protected String getResultAsJson() {
        return "";
    }

    protected String getResultAsHTML() {
        return html;
    }

    public ApkrProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ApkrProject currentProject) {
        this.currentProject = currentProject;
    }
}
