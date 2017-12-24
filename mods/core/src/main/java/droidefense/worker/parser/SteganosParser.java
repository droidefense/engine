package droidefense.worker.parser;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.worker.base.AbstractFileParser;

import java.util.ArrayList;

/**
 * Created by r00t on 30/11/2015.
 */
public class SteganosParser extends AbstractFileParser {

    public SteganosParser(LocalApkFile apk, DroidefenseProject currentProject) {
        super(apk, currentProject);
    }

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nSearching for Steganos content...\n\n");
        ArrayList<VirtualFile> fileList = currentProject.getAppFiles();

        /*for (AbstractHashedFile r : fileList) {
            //TODO implement at least LSB algorithm
        }*/
    }
}
