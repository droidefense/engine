import droidefense.reporting.HTMLReporter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by .local on 03/05/2017.
 */
public class HTMLReportTest {

    @Test
    public void test(){
        HTMLReporter html = new HTMLReporter();
        assertEquals( html.generateReport(), true);
    }

}