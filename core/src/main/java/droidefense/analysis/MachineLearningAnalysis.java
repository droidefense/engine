package droidefense.analysis;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.worker.handler.WekaResultsHandler;

/**
 * Created by sergio on 25/3/16.
 */
public final class MachineLearningAnalysis extends AbstractAndroidAnalysis {

    public MachineLearningAnalysis() {
        this.executionSuccessful = false;
        this.status = ProcessStatus.STARTED;
    }

    @Override
    protected boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense ML analysis ---\n\n");
        //run pscout apimodel
        WekaResultsHandler handler = new WekaResultsHandler();
        handler.setApk(apkFile);
        handler.setProject(currentProject);
        handler.doTheJob();

        //stop timer
        stop();
        return true;
    }

    @Override
    public String getName() {
        return "Machine learning analysis";
    }
}
