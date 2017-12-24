package droidefense.sdk;


import droidefense.handler.AXMLDecoderHandler;
import droidefense.sdk.model.io.VirtualHashedFile;
import droidefense.vfs.model.impl.VirtualFile;

/**
 * Created by .local on 14/10/2016.
 */
public enum UnpackAction {

    GENERATE_HASH {
        public static final boolean GENERATE_HASHES = true;

        @Override
        public VirtualFile execute(VirtualFile vf) {
            //information is generated
            VirtualHashedFile hashedFile = new VirtualHashedFile(vf, GENERATE_HASHES);
            return hashedFile.getThisFile();
        }
    }, DECODE {
        @Override
        public VirtualFile execute(VirtualFile vf) {
            //decode unpacked files
            AXMLDecoderHandler decoder = new AXMLDecoderHandler(vf);
            decoder.doTheJob();
            return decoder.getDecodedFile();
        }
    };

    public abstract VirtualFile execute(VirtualFile vf);
}
