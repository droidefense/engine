package droidefense.handler.base;

import droidefense.exception.ConfigFileNotFoundException;
import droidefense.sdk.helpers.DroidDefenseEnvironmentConfig;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;

import java.io.Serializable;

/**
 * Created by sergio on 16/2/16.
 */
public abstract class AbstractHandler implements Serializable {

    protected transient DroidDefenseEnvironmentConfig environment;

    protected LocalApkFile apk;
    protected Exception error;
    protected DroidefenseProject project;

    public AbstractHandler(){
        try {
            environment = DroidDefenseEnvironmentConfig.getInstance();
        } catch (ConfigFileNotFoundException e) {
            Log.write(LoggerType.FATAL, "Could not retrieve droidefense environment config file data");
        }
    }

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
