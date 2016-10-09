package com.zerjioang.apkr.handler;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.helpers.system.OSDetection;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergio on 6/3/16.
 */
public class ObjdumpHandler extends AbstractHandler {

    private ArrayList<ApkrFile> list;

    @Override
    public boolean doTheJob() {

        Log.write(LoggerType.TRACE, "Starting OBJDUMP...");

        if (list == null || list.size() == 0) {
            Log.write(LoggerType.TRACE, "No files to dump. Process finished!");
            return true;
        }

        Log.write(LoggerType.TRACE, "Dumping .so file content...");

        String path = null;

        if (OSDetection.isMacOSX()) {
            path = FileIOHandler.getRuleEngineDir().getAbsolutePath() + File.separator + "mac" + File.separator + "bin" + File.separator + "objdump";
        } else if (OSDetection.isUnix()) {
            path = FileIOHandler.getRuleEngineDir().getAbsolutePath() + File.separator + "nix" + File.separator + "bin" + File.separator + "objdump";
        } else if (OSDetection.isWindows()) {
            path = FileIOHandler.getRuleEngineDir().getAbsolutePath() + File.separator + "win" + File.separator + "bin" + File.separator + "objdump.exe";
        } else {
            path = null;
        }

        if (path != null) {
            File exec = new File(path);
            if (exec.exists()) {
                if (exec.canExecute()) {
                    for (ApkrFile r : list) {
                        String params = "-archive-header -c -D –file-header –debugging –stabs –help –info –private-headers –prefix-addresses –reloc –dynamic-reloc –full-contents –source –all-headers –disassemble-zeroes";
                        String command = path + " " + params + " " + r.getAbsolutePath();
                        try {
                            FileIOHandler.callSystemExec(command);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    error = new IllegalArgumentException("Dumper can not be executed.");
                }
            } else {
                error = new IllegalArgumentException("Dumper does not exist.");
            }
        }
        Log.write(LoggerType.TRACE, "OBJDUMP finished!");
        return path == null;
    }

    public ArrayList<ApkrFile> getList() {
        return list;
    }

    public void setList(ArrayList<ApkrFile> list) {
        this.list = list;
    }
}
