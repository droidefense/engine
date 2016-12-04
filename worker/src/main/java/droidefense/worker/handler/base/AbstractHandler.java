package droidefense.worker.handler.base;

import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;

import java.io.Serializable;

/**
 * Created by sergio on 16/2/16.
 */
public abstract class AbstractHandler implements Serializable {

    protected LocalApkFile apk;
    protected Exception error;
    protected DroidefenseProject project;

    public abstract boolean doTheJob();

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public LocalApkFile getApk() {
        return apk;
    }

    public void setApk(LocalApkFile apk) {
        this.apk = apk;
    }

    public DroidefenseProject getProject() {
        return project;
    }

    public void setProject(DroidefenseProject project) {
        this.project = project;
    }
}
