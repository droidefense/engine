package com.zerjioang.apkr.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.parser.base.AbstractFileParser;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.util.ArrayList;

/**
 * Created by r00t on 30/11/2015.
 */
public class SteganosParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nSearching for Steganos content...\n\n");
        ArrayList<ApkrFile> fileList = currentProject.getAppFiles();
        /*for (ApkrFile r : fileList) {
            //TODO implement at least LSB algorithm
        }*/
    }
}
