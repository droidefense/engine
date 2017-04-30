package com.j256.simplemagic;

import java.io.*;

/**
 * Created by .local on 18/10/2016.
 */
public class MagicNumberCalculator {

    private ContentInfo contentInfoFromStreamWrapper(ByteArrayInputStream inputSteam) throws IOException {
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
