package droidefense.social;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.util.RemoteFileDownloader;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.MalformedURLException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RemoteFileDownloaderTest {

    @Test
    public void downloadTest() {
        String content;
        try {
            content = new RemoteFileDownloader().downloadFileFromUrlUsingNio("https://www.android.com/robots.txt");
            Assert.assertEquals(content, "User-agent: *\n" +
                    "Disallow: /search\n" +
                    "Disallow: /404\n" +
                    "Disallow: /payapp/\n" +
                    "Sitemap: https://www.android.com/sitemap.xml\n");
        } catch (MalformedURLException e) {
            Log.write(LoggerType.ERROR, e.getMessage());
        }

    }
}
