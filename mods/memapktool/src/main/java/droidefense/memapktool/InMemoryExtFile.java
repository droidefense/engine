package droidefense.memapktool;

import brut.androlib.res.util.ExtFile;
import brut.directory.Directory;
import brut.directory.DirectoryException;
import brut.directory.FileDirectory;
import brut.directory.ZipRODirectory;
import droidefense.vfs.model.impl.VirtualFile;

import java.io.File;

/**
 * Created by .local on 22/11/2016.
 */
public class InMemoryExtFile extends ExtFile {

    private final VirtualFile file;
    private Directory mDirectory;

    public InMemoryExtFile(VirtualFile file) {
        super(new File(file.getPath()));
        this.file = file;
    }

    public Directory getDirectory() throws DirectoryException {
        if (this.mDirectory == null) {
            if (file.isFolder()) {
                this.mDirectory = new FileDirectory(this);
            } else {
                this.mDirectory = new ZipRODirectory(this);
            }
        }

        return this.mDirectory;
    }
}
