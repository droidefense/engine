package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.WekaResultsHandler;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;

/**
 * Created by sergio on 25/3/16.
 */
public class MachineLearningAnalysis extends AbstractAndroidAnalysis {

    public MachineLearningAnalysis() {
        this.positiveMatch = false;
        this.status = ProcessStatus.STARTED;
    }

    @Override
    protected boolean analyze() {
        //run pscout model
        WekaResultsHandler handler = new WekaResultsHandler();
        handler.setApk(apkFile);
        handler.setProject(currentProject);
        handler.doTheJob();

        //stop timer
        stop();
        positiveMatch = !hasErrors();
        return hasErrors();
    }

    @Override
    public String getName() {
        return "Machine learning analysis";
    }
}
