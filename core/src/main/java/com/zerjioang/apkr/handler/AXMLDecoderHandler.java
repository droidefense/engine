package com.zerjioang.apkr.handler;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import axmlparser.test.AXMLPrinter;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.helpers.Util;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private final ArrayList<ApkrFile> outputDir;

    public AXMLDecoderHandler(ArrayList<ApkrFile> outputDir) {
        super();
        this.outputDir = outputDir;
    }

    @Override
    public boolean doTheJob() {
        for (ApkrFile r : outputDir) {
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
