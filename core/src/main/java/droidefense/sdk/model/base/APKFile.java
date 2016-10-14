package droidefense.sdk.model.base;

import apkr.external.modules.helpers.enums.ProcessStatus;

import java.io.File;

/**
 * Created by sergio on 16/2/16.
 */
public class APKFile extends HashedFile {

    public static final byte APKTOOL = 0x0;
    public static final byte AXML = 0x1;

    private final byte technique;
    private ProcessStatus status;

    public APKFile(String apkPath, byte apktool) {
        super(apkPath);
        if (!this.f.exists())
            throw new IllegalArgumentException("APK file must exist on specified directory:\n" + apkPath);
        this.technique = apktool;
    }

    public APKFile(File f, byte apktool) {
        super(f);
        if (!this.f.exists())
            throw new IllegalArgumentException("APK file must exist on specified directory:\n" + f.getAbsolutePath());
        this.technique = apktool;
    }

    //GETTERS AND SETTERS

    public byte getTechnique() {
        return technique;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
