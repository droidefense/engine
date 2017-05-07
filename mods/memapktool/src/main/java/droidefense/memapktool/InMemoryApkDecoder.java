package droidefense.memapktool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.IOException;

/**
 * Created by .local on 02/05/2017.
 */
public class InMemoryApkDecoder extends ApkDecoder {

    private VirtualFile inputFile, outputFile;

    public InMemoryApkDecoder() {
    }

    public InMemoryApkDecoder(VirtualFile inputFile) {
        this.inputFile = inputFile;
        this.outputFile = VirtualFile.createFile(inputFile.getName());
    }

    public void decode() throws AndrolibException, IOException, DirectoryException {
        /*if (hasResources()) {
            switch (mDecodeResources) {
                case DECODE_RESOURCES_NONE:
                    mAndrolib.decodeResourcesRaw(mApkFile, outputFile);
                    break;
                case DECODE_RESOURCES_FULL:
                    setTargetSdkVersion();
                    setAnalysisMode(mAnalysisMode, true);

                    if (hasManifest()) {
                        mAndrolib.decodeManifestWithResources(mApkFile, outputFile, getResTable());
                    }
                    mAndrolib.decodeResourcesFull(mApkFile, outputFile, getResTable());
                    break;
            }
        } else {
            // if there's no resources.asrc, decode the droidefense.sdk.manifest without looking
            // up attribute references
            if (hasManifest()) {
                switch (mDecodeResources) {
                    case DECODE_RESOURCES_NONE:
                        mAndrolib.decodeManifestRaw(mApkFile, outputFile);
                        break;
                    case DECODE_RESOURCES_FULL:
                        mAndrolib.decodeManifestFull(mApkFile, outputFile, getResTable());
                        break;
                }
            }
        }

        mAndrolib.decodeRawFiles(mApkFile, outputFile);
        mAndrolib.decodeUnknownFiles(mApkFile, outputFile, mResTable);
        mUncompressedFiles = new ArrayList<String>();
        mAndrolib.recordUncompressedFiles(mApkFile, mUncompressedFiles);
        mAndrolib.writeOriginalFiles(mApkFile, outputFile);
        writeMetaFile();
        */
    }

    //getters and setters


    public VirtualFile getInputFile() {
        return inputFile;
    }

    public void setInputFile(VirtualFile inputFile) {
        this.inputFile = inputFile;
        this.outputFile = VirtualFile.createFile(inputFile.getName());
    }

    public VirtualFile getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(VirtualFile outputFile) {
        this.outputFile = outputFile;
    }
}
