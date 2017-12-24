package droidefense.analysis;

import droidefense.sdk.helpers.APKUnpacker;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.vfs.model.impl.VirtualFile;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class DecodeAnalysis extends AbstractAndroidAnalysis {

    @Override
    public boolean analyze() {
        Log.write(LoggerType.INFO, "In-memory .apk decoder...");
        //unpack file
        if(currentProject.getUsedUnpacker() == APKUnpacker.ZIP) {
            //since files are extracted using zip algorithm. no resource decoding nor xml deconfing is done.
            //now it is the time to do so.
            ArrayList<VirtualFile> files = currentProject.getAppFiles();
            executionSuccessful = !files.isEmpty();
            if (executionSuccessful) {
                apkFile.decodeWithTechnique(files);
                currentProject.setAppFiles(files);
            }
        }
        timeStamp.stop();
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "In-memory .apk decoder";
    }
}
