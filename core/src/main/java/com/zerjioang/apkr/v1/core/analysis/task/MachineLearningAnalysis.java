package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.WekaResultsHandler;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

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
        Log.write(LoggerType.TRACE, "\n\n --- Running apkr ML analysis ---\n\n");
        //run pscout apimodel
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
