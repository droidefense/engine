package droidefense.sdk.model.base;

import apkr.external.modules.helpers.enums.ProcessStatus;
import droidefense.cli.APKUnpacker;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public class LocalApkFile extends LocalHashedFile {

    private final APKUnpacker technique;
    private ProcessStatus status;

    public LocalApkFile(File f, APKUnpacker unpacker) {
        super(f, true);
        if (!this.f.exists())
            throw new IllegalArgumentException("APK file must exist on specified directory:\n" + f.getAbsolutePath());
        this.technique = unpacker;
    }

    //GETTERS AND SETTERS

    public APKUnpacker getTechnique() {
        return technique;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public ArrayList<AbstractHashedFile> unpackWithTechnique(File outputDir, DroidefenseProject currentProject) {
        return this.technique.unpackWithTechnique(currentProject, this, outputDir);
    }
}
