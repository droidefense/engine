package droidefense.handler;

import brut.androlib.AndrolibException;
import brut.androlib.err.CantFindFrameworkResException;
import brut.androlib.err.InFileNotFoundException;
import brut.androlib.err.OutDirExistsException;
import brut.directory.DirectoryException;
import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.memapktool.InMemoryApkDecoder;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.IOException;

/**
 * Created by sergio on 16/2/16.
 */
public final class AXMLDecoderHandler extends AbstractHandler {

    private static final InMemoryApkDecoder decoder = new InMemoryApkDecoder();
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
        try {
            decoder.setInputFile(this.vf);
            decoder.decode();
            this.vf = decoder.getOutputFile();
        } catch (OutDirExistsException ex) {
            Log.write(LoggerType.ERROR, "Destination directory already exists. Use -f switch if you want to overwrite it.");
        } catch (InFileNotFoundException ex) {
            Log.write(LoggerType.ERROR, "Input file was not found or was not readable.");
        } catch (CantFindFrameworkResException ex) {
            Log.write(LoggerType.ERROR, "Can't find framework resources for package of id: "
                            + String.valueOf(ex.getPkgId())
                            + ". You must install proper "
                            + "framework files, see project website for more info.");
        } catch (IOException ex) {
            Log.write(LoggerType.ERROR, "Could not modify file. Please ensure you have permission.");
        } catch (DirectoryException ex) {
            Log.write(LoggerType.ERROR, "Could not modify internal dex files. Please ensure you have permission.");
        } catch (AndrolibException e) {
            Log.write(LoggerType.ERROR, "AndrolibException: "+e.getLocalizedMessage());
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
