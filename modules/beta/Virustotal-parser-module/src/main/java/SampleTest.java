import vt.VirusTotalReader;

import java.io.IOException;

/**
 * Created by B328316 on 09/05/2016.
 */
public class SampleTest {

    public static void main(String[] args) throws IOException, IllegalAccessException {
        VirusTotalReader.DEBUG = true;
        final String url = "https://www.virustotal.com/es/file/bed05d8eace6a7ebc5dec7141ea4b9cc559f1b2aab8848e2c79df7a79de39b9d/analysis/";
        final String domain = "www.virustotal.com";
        VirusTotalReader reader = new VirusTotalReader(url, domain);
        String parsed = reader.getParsedData();
        System.out.println(parsed);
    }
}
