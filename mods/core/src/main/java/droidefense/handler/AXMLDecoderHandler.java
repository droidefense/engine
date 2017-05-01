package droidefense.handler;

import droidefense.custom.CustomAXMLDecoder;
import droidefense.handler.base.AbstractHandler;
import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.vfs.model.impl.VirtualFile;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private VirtualFile vf;

    public AXMLDecoderHandler() {
    }

    public AXMLDecoderHandler(VirtualFile vf) {
        this();
        this.vf = vf;
    }

    @Override
    public boolean doTheJob() {

        if(vf==null){
            return false;
        }

        if (vf.getName().toLowerCase().endsWith(InternalConstant.XML_EXTENSION)) {
            try {
                String decodedContent = CustomAXMLDecoder.decompressXML(vf.getContent());
                vf.setContent(decodedContent);
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

    public void setFile(VirtualFile file) {
        this.vf = file;
    }

    public VirtualFile getFile() {
        return vf;
    }
}
