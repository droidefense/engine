package droidefense.handler;

import droidefense.axml.AXMLPrinter;
import droidefense.handler.base.AbstractHandler;
import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.vfs.model.impl.VirtualFile;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private final VirtualFile vf;

    public AXMLDecoderHandler(VirtualFile vf) {
        this.vf = vf;
    }

    @Override
    public boolean doTheJob() {
        if (vf.getName().toLowerCase().endsWith(InternalConstant.XML_EXTENSION)) {
            AXMLPrinter printer;
            try {
                printer = new AXMLPrinter(vf.getContent());
                String decodedContent = printer.getResult();
                if (decodedContent != null) {
                    vf.setContent(decodedContent);
                }
            } catch (Exception e) {
                //TODO it seems that xml decoding failed. try second methods
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
                error = e;
                return false;
            }
        }
        return true;
    }

    public VirtualFile getDecodedFile() {
        return vf;
    }
}
