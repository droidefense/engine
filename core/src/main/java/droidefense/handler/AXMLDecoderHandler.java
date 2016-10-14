package droidefense.handler;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import axmlparser.test.AXMLPrinter;
import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.model.base.HashedFile;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private final ArrayList<HashedFile> outputDir;

    public AXMLDecoderHandler(ArrayList<HashedFile> outputDir) {
        super();
        this.outputDir = outputDir;
    }

    @Override
    public boolean doTheJob() {
        for (HashedFile r : outputDir) {
            if (r.getThisFile().getName().toLowerCase().endsWith(InternalConstant.XML_EXTENSION)) {
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
