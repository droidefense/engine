package droidefense.worker.handler;

import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.worker.handler.base.AbstractHandler;

/**
 * Created by sergio on 16/2/16.
 */
public class APKToolHandler extends AbstractHandler {

    public final static short DECODE_SOURCES_NONE = 0x0000;
    public final static short DECODE_SOURCES_SMALI = 0x0001;

    public final static short DECODE_RESOURCES_NONE = 0x0000;
    public final static short DECODE_RESOURCES_FULL = 0x0001;

    private static final boolean FORCE_DELETE = true;

    public APKToolHandler(DroidefenseProject project, LocalApkFile source) {
        super();
        this.project = project;
        this.apk = source;
    }

    @Override
    public boolean doTheJob() {
        return true;

        //TODO add new in-memory apktool decoding

        //OLD local files based apktool unpacking
        /*
        ApkDecoder decoder = new ApkDecoder();
        try {
            decoder.setFile(apk.getThisFile());
            decoder.setOutDir(outputDir);
            //force output folder overwrite
            decoder.setForceDelete(FORCE_DELETE);
            //do not decode dex files into smali code
            decoder.setDecodeSources(DECODE_RESOURCES_NONE);
            decoder.decode();
            return true;
        } catch (AndrolibException e) {
            Log.write(LoggerType.ERROR, "Error while decoding with APKTOOL", e, e.getLocalizedMessage());
            error = e;
        } catch (DirectoryException e) {
            Log.write(LoggerType.ERROR, "Error while decoding with APKTOOL", e, e.getLocalizedMessage());
            error = e;
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "Error while decoding with APKTOOL", e, e.getLocalizedMessage());
            error = e;
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "Error while decoding with APKTOOL", e, e.getLocalizedMessage());
            error = e;
        }
        return false;
        */
    }
}
