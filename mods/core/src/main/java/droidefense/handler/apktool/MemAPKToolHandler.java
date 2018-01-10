package droidefense.handler.apktool;

import brut.androlib.AndrolibException;
import brut.directory.DirectoryException;
import droidefense.handler.base.AbstractHandler;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;

import java.io.IOException;

/**
 * Created by sergio on 16/2/16.
 */
public class MemAPKToolHandler extends AbstractHandler {

    public final static short DECODE_SOURCES_NONE = 0x0000;
    public final static short DECODE_SOURCES_SMALI = 0x0001;

    public final static short DECODE_RESOURCES_NONE = 0x0000;
    public final static short DECODE_RESOURCES_FULL = 0x0001;

    private static final boolean FORCE_DELETE = true;

    public MemAPKToolHandler(DroidefenseProject project, LocalApkFile source) {
        super();
        this.project = project;
        this.apk = source;
    }

    @Override
    public boolean doTheJob() {
        //TODO add new in-memory apktool decoding

        //OLD local files based apktool unpacking
        ApkDecoderWrapper decoder = new ApkDecoderWrapper();
        try {
            decoder.setFile(apk.getThisFile());
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
    }
}
