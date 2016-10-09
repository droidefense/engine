package com.zerjioang.apkr.sdk;


import apkr.external.modules.helpers.enums.ProcessStatus;
import com.zerjioang.apkr.analysis.base.PluginAnalysis;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

/**
 * Created by sergio on 18/2/16.
 */
public abstract class AbstractApkrDynamicPlugin extends PluginAnalysis {

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
        //todo convert into json
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
