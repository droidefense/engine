package droidefense.analysis;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.vfs.model.impl.VirtualFile;

import java.util.ArrayList;

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
