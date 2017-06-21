package com.j256.simplemagic;

import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PerformanceTest {

    private static final String DIFFICULT_FILE_TYPE_RESOURCE = "/files/exif2.jpg";

    @Test
    public void testLargeRun() throws Exception {
        InputStream stream = getClass().getResourceAsStream(DIFFICULT_FILE_TYPE_RESOURCE);
        assertNotNull(stream);
        byte[] bytes = new byte[ContentInfoUtil.DEFAULT_READ_SIZE];
        int numRead;
        try {
            numRead = stream.read(bytes);
            assertTrue(numRead > 0);
        } finally {
            stream.close();
        }
        if (numRead < bytes.length) {
            // move the bytes into a smaller array
            bytes = Arrays.copyOf(bytes, numRead);
        }
        ContentInfoUtil contentInfoUtil = new ContentInfoUtil();
        for (int i = 0; i < 200; i++) {
            ContentInfo details = contentInfoUtil.findMatch(bytes);
            assertNotNull("not expecting the content type to be null", details);
            assertEquals("bad content-type", ContentType.JPEG, details.getContentType());
            assertEquals("bad name", "jpeg", details.getName());
            assertEquals("bad mime-type", "image/jpeg", details.getMimeType());
            assertEquals("bad message", "JPEG image data, EXIF standard 2.2", details.getMessage());
        }
    }
}
