package com.zerjioang.apkr.analysis.emulator;

import com.zerjioang.apkr.analysis.emulator.base.AbstractAndroidEmulator;
import com.zerjioang.apkr.analysis.handlers.FileIOHandler;

import java.io.File;

/**
 * Created by sergio on 9/4/16.
 */
public class GhostFSEmulator extends AbstractAndroidEmulator {

    private static File rootPath = new File(FileIOHandler.getBaseDirPath() + File.separator + "fs");

    public GhostFSEmulator() {
        System.out.println("FS emulator setup at: " + rootPath.getAbsolutePath());
    }

    @Override
    public void emulate() {

    }
}
