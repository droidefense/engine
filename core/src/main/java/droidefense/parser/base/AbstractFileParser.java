package droidefense.parser.base;

import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.sdk.model.base.LocalApkFile;

import java.io.Serializable;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AbstractFileParser implements Serializable {

    protected transient LocalApkFile apk;
    protected transient DroidefenseProject currentProject;
    protected ExecutionTimer timestamp;

    public void parse() {
        timestamp = new ExecutionTimer();
        parserCode();
        timestamp.stop();
    }

    public abstract void parserCode();

    public LocalApkFile getApk() {
        return apk;
    }

    public void setApk(LocalApkFile apk) {
        this.apk = apk;
        this.currentProject = DroidefenseProject.getProject(apk);
    }

    public DroidefenseProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(DroidefenseProject currentProject) {
        this.currentProject = currentProject;
    }
}
