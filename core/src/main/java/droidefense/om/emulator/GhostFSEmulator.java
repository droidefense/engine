package droidefense.om.emulator;

import droidefense.handler.FileIOHandler;
import droidefense.om.emulator.base.AbstractAndroidEmulator;

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
