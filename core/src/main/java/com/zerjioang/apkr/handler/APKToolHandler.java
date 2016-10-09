package com.zerjioang.apkr.handler;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.sdk.model.base.APKFile;

import java.io.File;
import java.io.IOException;

/**
 * Created by sergio on 16/2/16.
 */
public class APKToolHandler extends AbstractHandler {

    public final static short DECODE_SOURCES_NONE = 0x0000;
    public final static short DECODE_SOURCES_SMALI = 0x0001;

    public final static short DECODE_RESOURCES_NONE = 0x0000;
    public final static short DECODE_RESOURCES_FULL = 0x0001;

    private static final boolean FORCE_DELETE = true;
    private final APKFile source;
    private final File outputDir;

    public APKToolHandler(APKFile source, File outputDir) {
        super();
        this.source = source;
        this.outputDir = outputDir;
    }

    @Override
    public boolean doTheJob() {
        ApkDecoder decoder = new ApkDecoder();
        try {
            decoder.setApkFile(source.getThisFile());
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
    }
}
