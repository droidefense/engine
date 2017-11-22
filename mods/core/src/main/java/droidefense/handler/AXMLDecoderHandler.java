package droidefense.handler;


import droidefense.handler.base.AbstractHandler;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.worker.parser.InMemoryAXMLParser;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private static final InMemoryAXMLParser decoder = new InMemoryAXMLParser();
    private VirtualFile vf;

    public AXMLDecoderHandler() {}

    public AXMLDecoderHandler(VirtualFile vf) {
        this.vf = vf;
    }

    @Override
    public boolean doTheJob() {
        if(vf==null){
            return false;
        }
        else{
            decoder.setInputFile(this.vf);
            decoder.decode();
            return this.vf != null;
        }
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
