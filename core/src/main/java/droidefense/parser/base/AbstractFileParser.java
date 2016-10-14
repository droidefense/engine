package droidefense.parser.base;

import droidefense.sdk.model.base.APKFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.io.Serializable;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AbstractFileParser implements Serializable {

    protected transient APKFile apk;
    protected transient DroidefenseProject currentProject;
    protected ExecutionTimer timestamp;

    public void parse() {
        timestamp = new ExecutionTimer();
        parserCode();
        timestamp.stop();
    }

    public abstract void parserCode();

    public APKFile getApk() {
        return apk;
    }

    public void setApk(APKFile apk) {
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
