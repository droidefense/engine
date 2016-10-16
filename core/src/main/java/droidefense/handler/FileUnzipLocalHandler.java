package droidefense.handler;

import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.model.base.LocalHashedFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by sergio on 16/2/16.
 */
public class FileUnzipLocalHandler extends AbstractHandler {

    private static final int BUFFER_SIZE = 4096;

    private LocalHashedFile source;
    private File outputDir;

    public FileUnzipLocalHandler(LocalHashedFile source, File outputDir) {
        super();
        this.source = source;
        this.outputDir = outputDir;
    }

    @Override
    public boolean doTheJob() {

        //create output dir if does not exists
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        //read zip file
        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(source.getThisFile()));
            ZipEntry entry = zipIn.getNextEntry();

            // iterates over entries in the zip file

            String destDirectory = outputDir.getAbsolutePath();
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    File parent = new File(filePath);
                    if (!parent.getParentFile().exists())
                        parent.getParentFile().mkdirs();
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                    byte[] bytesIn = new byte[BUFFER_SIZE];
                    int read = 0;
                    while ((read = zipIn.read(bytesIn)) != -1) {
                        bos.write(bytesIn, 0, read);
                    }
                    bos.close();
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    boolean ok = dir.mkdir();
                    if (!ok) {
                        error = new RuntimeException("We could not make a new directory on: " + filePath);
                        return false;
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            //close zip file access
            zipIn.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            error = e;
        } catch (IOException e) {
            e.printStackTrace();
            error = e;
        } catch (Exception e) {
            e.printStackTrace();
            error = e;
        }
        return false;
    }
}
