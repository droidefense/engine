package droidefense.sdk.model.io;

import droidefense.sdk.helpers.APKUnpacker;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public class LocalApkFile extends LocalHashedFile {

    private final APKUnpacker unpackingTechnique;
    private transient DroidefenseProject project;

    public LocalApkFile(File f, DroidefenseProject project, APKUnpacker unpacker) {
        super(f, true);
        if (!this.f.exists())
            throw new IllegalArgumentException("APK file must exist on specified directory:\n" + f.getAbsolutePath());
        this.project = project;
        this.unpackingTechnique = unpacker;
    }

    //GETTERS AND SETTERS

    public APKUnpacker getUnpackingTechnique() {
        return unpackingTechnique;
    }

    public boolean unpackWithTechnique() {
        return this.unpackingTechnique.unpackWithTechnique(project, this);
    }

    public ArrayList<VirtualFile> decodeWithTechnique(ArrayList<VirtualFile> files) {
        return this.unpackingTechnique.decodeWithTechnique(project, files);
    }

    public DroidefenseProject getProject() {
        return project;
    }
}
