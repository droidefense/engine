package droidefense;

import droidefense.exception.InvalidScanParametersException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LastBuildTest {

    @Test
    public void t00_scan_creation() throws InvalidScanParametersException, FileNotFoundException {
        String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "lastbuild";
        File last = new File(path);
        Assert.assertNotNull(last);

        try (PrintStream out = new PrintStream(new FileOutputStream(last))) {
            String timeStamp = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format(Calendar.getInstance().getTime());
            Assert.assertNotNull(timeStamp);

            out.print(timeStamp);
        }
    }
}

