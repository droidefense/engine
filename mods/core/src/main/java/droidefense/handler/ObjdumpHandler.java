package droidefense.handler;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.system.OSDetection;
import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergio on 6/3/16.
 */
public class ObjdumpHandler extends AbstractHandler {

    private ArrayList<AbstractHashedFile> list;

    @Override
    public boolean doTheJob() {

        Log.write(LoggerType.TRACE, "Starting OBJDUMP...");

        if (list == null || list.size() == 0) {
            Log.write(LoggerType.TRACE, "No files to dump. Process finished!");
            return true;
        }

        Log.write(LoggerType.TRACE, "Dumping .so file content...");

        String path = null;
        File exec = null;

        //linux found
        boolean isLinux = false;

        if (OSDetection.isMacOSX()) {
            path = FileIOHandler.getToolsDir().getAbsolutePath() + File.separator + "mac" + File.separator + "bin" + File.separator + "objdump";
            exec = new File(path);
        } else if (OSDetection.isUnix()) {
            isLinux = true;
            path = "objdump";
        } else if (OSDetection.isWindows()) {
            path = FileIOHandler.getToolsDir().getAbsolutePath() + File.separator + "win" + File.separator + "bin" + File.separator + "objdump.exe";
            exec = new File(path);
        } else {
            path = null;
        }

        if(isLinux){
            runObjdump(path);
        }
        else{
            if (exec!=null && exec.exists() ) {
                if (exec.canExecute()) {
                    runObjdump(path);
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

    private void runObjdump(String path) {
        for (AbstractHashedFile r : list) {
            String params = "-archive-header -c -D –file-header –debugging –stabs –help –info –private-headers –prefix-addresses –reloc –dynamic-reloc –full-contents –source –all-headers –disassemble-zeroes";
            String command = path + " " + params + " " + r.getAbsolutePath();
            try {
                FileIOHandler.callSystemExec(command);
            } catch (IOException e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            }
        }
    }

    public ArrayList<AbstractHashedFile> getList() {
        return list;
    }

    public void setList(ArrayList<AbstractHashedFile> list) {
        this.list = list;
    }
}
