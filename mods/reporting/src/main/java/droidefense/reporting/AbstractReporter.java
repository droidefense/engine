package droidefense.reporting;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by .local on 03/05/2017.
 */
public abstract class AbstractReporter implements Serializable{

    protected File reportFile;

    public abstract boolean generateReport() throws IOException;

    public void open() {
        openReportOnBorwser();
    }

    private void openReportOnBorwser() {
        try {
            Desktop.getDesktop().open(reportFile);
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "Could no automatically open sample report on user browser", e.getLocalizedMessage());
        }
    }
}
