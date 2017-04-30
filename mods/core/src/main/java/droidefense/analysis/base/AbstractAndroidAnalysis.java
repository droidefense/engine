package droidefense.analysis.base;

import droidefense.helpers.enums.ProcessStatus;
import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.sdk.model.io.LocalApkFile;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AbstractAndroidAnalysis implements Serializable {

    protected ExecutionTimer timeStamp;
    protected ProcessStatus status;
    protected boolean executionSuccessful;
    protected String result;

    protected transient LocalApkFile apkFile;
    protected transient DroidefenseProject currentProject;
    //for error handing
    protected transient ArrayList<Exception> errorList;
    protected String name;

    public AbstractAndroidAnalysis() {
        errorList = new ArrayList<>();
        this.status = ProcessStatus.STARTED;
        this.executionSuccessful = true;
    }

    public boolean analyzeCode() {

        //preload
        name = getName();

        status = ProcessStatus.STARTED;
        executionSuccessful = runAnalysis();
        return executionSuccessful;
    }

    public boolean runAnalysis(){
        start();
        status = ProcessStatus.EXECUTING;
        boolean result = analyze();
        status = ProcessStatus.FINISHED;
        stop();
        return result;
    }

    public boolean noErrors() {
        return errorList.isEmpty();
    }

    public void addError(Exception e) {
        this.errorList.add(e);
    }

    public void addError(Throwable e) {
        this.errorList.add(new Exception(e));
    }

    protected abstract boolean analyze();

    public final LocalApkFile getApkFile() {
        return apkFile;
    }

    public final void setApkFile(LocalApkFile apkFile) {
        this.apkFile = apkFile;
        this.currentProject = DroidefenseProject.getProject(apkFile);
    }

    public final ExecutionTimer getTimeStamp() {
        return timeStamp;
    }

    public final void setTimeStamp(ExecutionTimer timeStamp) {
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
            timeStamp = new ExecutionTimer();
        timeStamp.start();
    }

    public void stop() {
        timeStamp.stop();
    }

    public DroidefenseProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(DroidefenseProject currentProject) {
        this.currentProject = currentProject;
    }

    public boolean isExecutionSuccessful() {
        return executionSuccessful;
    }

    public void setExecutionSuccessful(boolean executionSuccessful) {
        this.executionSuccessful = executionSuccessful;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public abstract String getName();
}
