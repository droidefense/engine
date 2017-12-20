package droidefense.sdk.model.io;

import droidefense.vfs.model.impl.VirtualFile;

import java.io.Serializable;

public final class DexHashedFile extends VirtualHashedFile implements Serializable {

    private boolean dexHeaderReaded;

    public DexHashedFile(VirtualFile vf, boolean generateInformation) {
        super(vf, generateInformation);
    }

    public boolean isHeaderReaded() {
        return dexHeaderReaded;
    }

    public void setDexHeaderReaded(boolean dexHeaderReaded) {
        this.dexHeaderReaded = dexHeaderReaded;
    }
}
