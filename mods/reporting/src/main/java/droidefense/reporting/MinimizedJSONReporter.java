package droidefense.reporting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by .local on 03/05/2017.
 */
public class MinimizedJSONReporter extends AbstractReporter {

    private String data;

    public MinimizedJSONReporter(File reportFile, String data) {
        this.reportFile = reportFile;
        this.data = data;
        if(data!=null){
            this.data = data.replace("\r", "");
            this.data = data.replace("\t", "");
            this.data = data.replace("\n", "");
        }
    }

    @Override
    public boolean generateReport() {
        try {
            FileOutputStream fos = new FileOutputStream( reportFile );
            fos.write(data.getBytes());
            fos.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
