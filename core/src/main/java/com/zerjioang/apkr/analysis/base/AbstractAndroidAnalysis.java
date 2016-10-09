package com.zerjioang.apkr.analysis.base;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.sdk.model.base.APKFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AbstractAndroidAnalysis implements Serializable {

    protected AtomTimeStamp timeStamp;
    protected ProcessStatus status;
    protected boolean positiveMatch;
    protected String result;

    protected transient APKFile apkFile;
    protected transient ApkrProject currentProject;
    //for error handing
    protected transient ArrayList<Exception> errorList;
    private String name;

    public AbstractAndroidAnalysis() {
        errorList = new ArrayList<>();
    }

    public boolean analyzeCode() {

        //preload
        name = getName();

        status = ProcessStatus.STARTED;
        start();
        status = ProcessStatus.EXECUTING;
        analyze();
        status = ProcessStatus.FINISHED;
        return hasErrors();
    }

    public boolean hasErrors() {
        return !errorList.isEmpty();
    }

    public void addError(Exception e) {
        this.errorList.add(e);
    }

    public void addError(Throwable e) {
        this.errorList.add(new Exception(e));
    }

    protected abstract boolean analyze();

    public final APKFile getApkFile() {
        return apkFile;
    }

    public final void setApkFile(APKFile apkFile) {
        this.apkFile = apkFile;
        this.currentProject = ApkrProject.getProject(apkFile);
    }

    public final AtomTimeStamp getTimeStamp() {
        return timeStamp;
    }

    public final void setTimeStamp(AtomTimeStamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public final void log(Object o, final int count) {
        String separator = "";
        for (int i = 0; i < count; i++)
            separator += "\t";
        Log.write(LoggerType.TRACE, separator + o);
    }

    public void start() {
        if (timeStamp == null)
            timeStamp = new AtomTimeStamp();
        timeStamp.start();
    }

    public void stop() {
        timeStamp.stop();
    }

    public ApkrProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ApkrProject currentProject) {
        this.currentProject = currentProject;
    }

    public boolean isPositiveMatch() {
        return positiveMatch;
    }

    public void setPositiveMatch(boolean positiveMatch) {
        this.positiveMatch = positiveMatch;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public abstract String getName();
}
