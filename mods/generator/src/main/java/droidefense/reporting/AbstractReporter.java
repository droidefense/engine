package droidefense.reporting;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by .local on 03/05/2017.
 */
public abstract class AbstractReporter {

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
