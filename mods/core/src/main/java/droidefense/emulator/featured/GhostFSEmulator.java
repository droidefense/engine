package droidefense.emulator.featured;

import droidefense.emulator.featured.base.AbstractAndroidEmulator;

import java.io.File;

/**
 * Created by sergio on 9/4/16.
 */
public class GhostFSEmulator extends AbstractAndroidEmulator {

    private static File rootPath = new File(new File("").getAbsolutePath() + File.separator + "fs");

    public GhostFSEmulator() {
        System.out.println("FS emulator setup at: " + rootPath.getAbsolutePath());
    }

    @Override
    public void emulate() {

    }
}
