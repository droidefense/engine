package com.zerjioang.apkr.v1.core.analysis.sttc.parser;

import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.util.ArrayList;

/**
 * Created by r00t on 30/11/2015.
 */
public class SteganosParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nSearching for Steganos content...\n\n");
        ArrayList<ResourceFile> fileList = currentProject.getAppFiles();
        /*for (ResourceFile r : fileList) {
            //TODO implement at least LSB algorithm
        }*/
    }
}
