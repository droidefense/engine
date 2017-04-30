package droidefense.analysis;

import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;

/**
 * Created by sergio on 16/2/16.
 */
public final class SocialAnalysis extends AbstractAndroidAnalysis {

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running social analysis ---\n\n");
        executionSuccessful = false;
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "Social Analysis";
    }
}
