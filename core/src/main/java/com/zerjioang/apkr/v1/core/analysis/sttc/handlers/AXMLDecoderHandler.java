package com.zerjioang.apkr.v1.core.analysis.sttc.handlers;

import axmlparser.test.AXMLPrinter;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private final ArrayList<ResourceFile> outputDir;

    public AXMLDecoderHandler(ArrayList<ResourceFile> outputDir) {
        super();
        this.outputDir = outputDir;
    }

    @Override
    public boolean doTheJob() {
        for (ResourceFile r : outputDir) {
            if (r.getThisFile().getName().toLowerCase().endsWith(ApkrConstants.XML_EXTENSION)) {
                AXMLPrinter printer;
                try {
                    printer = new AXMLPrinter(r.getThisFile());
                    String decodedContent = printer.getResult();
                    if (decodedContent != null) {
                        Util.writeToFile(r.getThisFile(), decodedContent);
                    } else {
                        Log.write(LoggerType.ERROR, r.getThisFile().getName() + " --> Error decoding android internal references");
                        return false;
                    }
                } catch (Exception e) {
                    //TODO it seems that xml decoding failed. try second methods
                    e.printStackTrace();
                    error = e;
                    return false;
                }
            }
        }
        return true;
    }
}
