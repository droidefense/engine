package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.mod.vfs.model.impl.VirtualFile;

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
        ArrayList<VirtualFile> files = apkFile.unpackWithTechnique();
        executionSuccessful = !files.isEmpty();
        currentProject.setCorrectUnpacked(executionSuccessful);
        currentProject.setAppFiles(files);
        timeStamp.stop();
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "In-memory .apk unpacker";
    }
}
