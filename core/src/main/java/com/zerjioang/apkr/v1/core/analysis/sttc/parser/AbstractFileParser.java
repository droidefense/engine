package com.zerjioang.apkr.v1.core.analysis.sttc.parser;

import com.zerjioang.apkr.v1.common.datamodel.base.APKFile;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;

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
