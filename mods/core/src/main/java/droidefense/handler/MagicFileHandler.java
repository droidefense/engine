package droidefense.handler;

import droidefense.handler.base.AbstractHandler;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.system.SystemCallReturn;

import java.io.IOException;

/**
 * Created by sergio on 6/3/16.
 */
public class MagicFileHandler extends AbstractHandler {

    private static final String EXEC_PATH = "file ";
    private SystemCallReturn ret;

    private AbstractHashedFile file;

    @Override
    public boolean doTheJob() {
        if (file != null && file.isFile() && file.canRead()) {
            try {
                ret = FileIOHandler.callSystemExec(EXEC_PATH + file.getAbsolutePath());
                return true;
            } catch (IOException e) {
                Log.write(LoggerType.ERROR, "Could not use file command to detect filetype", e.getLocalizedMessage());
            }
        }
        return false;
    }

    //GETTERS AND SETTERS

    public String getAnswer() {
        String splitter = ": ";
        if (ret.getAnswer() == null || ret.getAnswer().isEmpty()) {
            return "";
        }
        String data = ret.getAnswer().get(0);
        int idx = data.indexOf(splitter);
        if (idx > -1)
            return data.substring(idx + splitter.length());
        return data;
    }

    public String getStdError() {
        return ret.getError().get(0);
    }

    public String getCommand() {
        return ret.getCommand().get(0);
    }

    public void setResource(AbstractHashedFile resource) {
        this.file = resource;
    }
}
