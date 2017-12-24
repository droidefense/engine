package droidefense.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by .local on 03/05/2017.
 */
public class BeautifiedJSONReporter extends AbstractReporter {

    private final String data;

    public BeautifiedJSONReporter(File reportFile, String data) {
        this.reportFile = reportFile;
        this.data = data;
    }

    @Override
    public boolean generateReport() throws IOException {
        FileOutputStream fos = new FileOutputStream(reportFile);
        fos.write(data.getBytes());
        fos.close();
        return true;
    }
}
