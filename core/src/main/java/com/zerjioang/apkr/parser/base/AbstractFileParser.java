package com.zerjioang.apkr.parser.base;

import com.zerjioang.apkr.sdk.model.base.APKFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

import java.io.Serializable;

/**
 * Created by r00t on 24/10/15.
 */
public abstract class AbstractFileParser implements Serializable {

    protected transient APKFile apk;
    protected transient ApkrProject currentProject;
    protected AtomTimeStamp timestamp;

    public void parse() {
        timestamp = new AtomTimeStamp();
        parserCode();
        timestamp.stop();
    }

    public abstract void parserCode();

    public APKFile getApk() {
        return apk;
    }

    public void setApk(APKFile apk) {
        this.apk = apk;
        this.currentProject = ApkrProject.getProject(apk);
    }

    public ApkrProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(ApkrProject currentProject) {
        this.currentProject = currentProject;
    }
}
