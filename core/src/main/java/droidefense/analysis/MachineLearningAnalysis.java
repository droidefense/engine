package droidefense.analysis;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.WekaResultsHandler;

/**
 * Created by sergio on 25/3/16.
 */
public final class MachineLearningAnalysis extends AbstractAndroidAnalysis {

    public MachineLearningAnalysis() {
        this.positiveMatch = false;
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
        positiveMatch = !hasErrors();
        return hasErrors();
    }

    @Override
    public String getName() {
        return "Machine learning analysis";
    }
}
