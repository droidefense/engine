package droidefense;

import droidefense.exception.InvalidScanParametersException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScanTest {

    @Test
    public void t00_scan_creation() throws InvalidScanParametersException {
        String path = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "screen-filter-v1.3.apk";

        File file = new File(path);
        Assert.assertNotNull(file);
        System.out.println("Sample path for test: " + file.getAbsolutePath());
        Assert.assertEquals(file.exists(), true);

        /*DroidefenseScan scan = new DroidefenseScan(new String[]{
                "-i",
                file.getAbsolutePath(),
                "-v"
        });
        scan.stop();*/
    }
}

