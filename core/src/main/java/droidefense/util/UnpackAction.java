package droidefense.util;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.vfs.model.impl.VirtualFile;
import droidefense.handler.AXMLDecoderHandler;
import droidefense.sdk.model.base.HashedFile;

import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum UnpackAction {
    GENERATE_HASH {
        public static final boolean GENERATE_HASHES = true;

        @Override
        public void execute(VirtualFile vf) {

            ArrayList<HashedFile> files = new ArrayList<>();

            //TODO generate hashes of the files
            HashedFile hashedFile = new HashedFile(vf, GENERATE_HASHES);

            files.add(hashedFile);

            Log.write(LoggerType.TRACE, "Decoding XML resources");
            //decode unpacked files
            AXMLDecoderHandler decoder = new AXMLDecoderHandler(files);
            decoder.doTheJob();
        }
    };

    public abstract void execute(VirtualFile vf);
}
