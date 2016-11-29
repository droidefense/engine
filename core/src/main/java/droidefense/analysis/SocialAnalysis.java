package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;

/**
 * Created by sergio on 16/2/16.
 */
public final class SocialAnalysis extends AbstractAndroidAnalysis {

    public SocialAnalysis() {
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running social analysis ---\n\n");
        this.timeStamp.stop();
        return false;
    }

    @Override
    public String getName() {
        return "Social Analysis";
    }
}
