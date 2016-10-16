package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.FileIOHandler;
import droidefense.sdk.helpers.DroidDefenseParams;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.model.base.AbstractHashedFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class UnpackAnalysis extends AbstractAndroidAnalysis {

    private transient ArrayList<AbstractHashedFile> files;

    public UnpackAnalysis() {
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "Unpacking .apk...");
        //prepare folder
        File outputDir = FileIOHandler.getUnpackOutputFile(apkFile);
        if (outputDir.exists() && DroidDefenseParams.getInstance().OVERWRITE_DECODE_FOLDER) {
            //delete
            Util.deleteFolder(outputDir);
        }
        //unpack file
        files = apkFile.unpackWithTechnique(outputDir, currentProject);
        if (files != null) {
            //save files count
            positiveMatch = files.size() > 0;
            //SET APP FILES & CALCULATE THEIR HASHES, FUZZING HASH, EXTENSION, SIGNATURE
            currentProject.setAppFiles(getFiles());
            timeStamp.stop();
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "Android .apk unpacker";
    }

    public ArrayList<AbstractHashedFile> getFiles() {
        return files;
    }
}
