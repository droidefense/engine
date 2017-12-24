package droidefense.social;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class RemoteFileDownloader {


    /**
     * Download remote file from URL using non blocking IO
     *
     * @param url url to be downloaded
     * @return returns content of downloaded file if success, null otherwise
     * @throws MalformedURLException
     */
    public String downloadFileFromUrlUsingNio(String url) throws MalformedURLException {

        StringBuilder content = new StringBuilder();
        URL urlObj = null;
        ReadableByteChannel inChannel = null;
        String data = null;

        if (url == null || url.trim().length() == 0) {
            data = null;
        } else {
            try {
                urlObj = new URL(url);
                inChannel = Channels.newChannel(urlObj.openStream());

                ByteBuffer buffer = ByteBuffer.allocate(8192);
                int read;

                while ((read = inChannel.read(buffer)) != -1) {
                    buffer.rewind();
                    buffer.limit(read);
                    CharBuffer contentToAdd = StandardCharsets.UTF_8.decode(buffer);
                    content.append(contentToAdd);
                    buffer.clear();
                }
                data = content.toString();
            } catch (FileNotFoundException e) {
                data = "404";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inChannel != null) {
                        inChannel.close();
                    }
                } catch (IOException ioExObj) {
                    System.out.println("Problem Occured While Closing The Object= " + ioExObj.getMessage());
                }
            }
        }
        return data;
    }
}
