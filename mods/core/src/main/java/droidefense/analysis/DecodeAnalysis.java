package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.mod.vfs.model.impl.VirtualFile;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class DecodeAnalysis extends AbstractAndroidAnalysis {

    @Override
    public boolean analyze() {
        Log.write(LoggerType.INFO, "In-memory .apk decoder...");
        //unpack file
        ArrayList<VirtualFile> files = currentProject.getAppFiles();
        executionSuccessful = !files.isEmpty();
        if (executionSuccessful) {
            apkFile.decodeWithTechnique(files);
            currentProject.setAppFiles(files);
        }
        timeStamp.stop();
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "In-memory .apk decoder";
    }
}
