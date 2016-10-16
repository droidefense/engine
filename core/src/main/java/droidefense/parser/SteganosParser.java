package droidefense.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.parser.base.AbstractFileParser;
import droidefense.sdk.model.base.AbstractHashedFile;

import java.util.ArrayList;

/**
 * Created by r00t on 30/11/2015.
 */
public class SteganosParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nSearching for Steganos content...\n\n");
        ArrayList<AbstractHashedFile> fileList = currentProject.getAppFiles();
        /*for (AbstractHashedFile r : fileList) {
            //TODO implement at least LSB algorithm
        }*/
    }
}
