package com.zerjioang.apkr.v1.common.datamodel.base;

import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;

/**
 * Created by sergio on 16/2/16.
 */
public class APKFile extends ResourceFile {

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
