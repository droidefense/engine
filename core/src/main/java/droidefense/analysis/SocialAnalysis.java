package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.rulengine.RuleEngine;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.FileIOHandler;
import droidefense.social.GooglePlayChecker;

/**
 * Created by sergio on 16/2/16.
 */
public final class SocialAnalysis extends AbstractAndroidAnalysis {

    private transient RuleEngine engine;

    public SocialAnalysis() {
        engine = new RuleEngine(FileIOHandler.getRuleEngineDir());
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense social analysis ---\n\n");

        GooglePlayChecker googlePlayStoreChecker = new GooglePlayChecker(currentProject);
        googlePlayStoreChecker.onPreExecute();
        googlePlayStoreChecker.onExecute();
        googlePlayStoreChecker.postExecute();
        executionSuccessful = googlePlayStoreChecker.isExecutionSuccessful();
        this.timeStamp.stop();
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "Droidefense social Analysis";
    }
}
