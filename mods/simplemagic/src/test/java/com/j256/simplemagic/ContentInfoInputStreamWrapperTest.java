package com.j256.simplemagic;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContentInfoInputStreamWrapperTest {

    @Test
    public void testBasic() throws Exception {
        ContentInfo info = contentInfoFromStreamWrapper("/files/x.gif");
        assertNotNull(info);
        assertEquals(ContentType.GIF, info.getContentType());

        info = contentInfoFromStreamWrapper("/files/x.doc");
        assertNotNull(info);
        assertEquals(ContentType.MICROSOFT_WORD, info.getContentType());
    }

    private ContentInfo contentInfoFromStreamWrapper(String resource) throws IOException {
        InputStream resourceStream = getClass().getResourceAsStream(resource);
        ByteArrayOutputStream outputStream;
        try {
            outputStream = new ByteArrayOutputStream();
            // read it in 100 times
            for (int i = 0; i < 100; i++) {
                copyStream(resourceStream, outputStream);
                resourceStream.close();
                resourceStream = getClass().getResourceAsStream(resource);
            }
        } finally {
            resourceStream.close();
        }
        byte[] resourceBytes = outputStream.toByteArray();

        ByteArrayInputStream inputSteam = new ByteArrayInputStream(resourceBytes);
        ContentInfoInputStreamWrapper wrappedStream = new ContentInfoInputStreamWrapper(inputSteam);
        try {
            ByteArrayOutputStream checkOutputStream = new ByteArrayOutputStream();

            // read it in 100 times
            for (int i = 0; i < 10; i++) {
                // coverage
                wrappedStream.available();
                wrappedStream.skip(10);
                wrappedStream.read(new byte[10]);
                wrappedStream.read(new byte[10], 2, 5);
                wrappedStream.read();
                wrappedStream.mark(10);
                wrappedStream.reset();
                wrappedStream.markSupported();
                copyStream(wrappedStream, checkOutputStream);
            }

            return wrappedStream.findMatch();
        } finally {
            wrappedStream.close();
        }
    }

    private void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int numRead = input.read(buffer);
            if (numRead < 0) {
                return;
            }
            output.write(buffer, 0, numRead);
        }
    }
}
