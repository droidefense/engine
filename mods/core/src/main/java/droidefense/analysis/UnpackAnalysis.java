package droidefense.analysis;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;

/**
 * Created by sergio on 16/2/16.
 */
public final class UnpackAnalysis extends AbstractAndroidAnalysis {

    public UnpackAnalysis() {
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.INFO, "Unpacking .apk...");
        //unpack file
        executionSuccessful = apkFile.unpackWithTechnique();
        currentProject.setCorrectUnpacked(executionSuccessful);
        timeStamp.stop();
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "In-memory .apk unpacker";
    }
}
