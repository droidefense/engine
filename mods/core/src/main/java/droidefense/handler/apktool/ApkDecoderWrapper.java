package droidefense.handler.apktool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;

import java.io.File;
import java.io.IOException;

public class ApkDecoderWrapper {

    private ApkDecoder decoder;

    public ApkDecoderWrapper() {
        decoder = new ApkDecoder();
    }

    public void setFile(File f) {
        this.decoder.setApkFile(f);
    }

    public void setOutDir(File outdir) throws AndrolibException {
        this.decoder.setOutDir(outdir);
    }

    public void setDecodeSources(short value) throws AndrolibException {
        this.decoder.setDecodeSources(value);
    }

    public void setForceDelete(boolean forceDelete) {
        this.decoder.setForceDelete(forceDelete);
    }

    public void decode() throws DirectoryException, IOException, AndrolibException {
        this.decoder.decode();
    }
}
