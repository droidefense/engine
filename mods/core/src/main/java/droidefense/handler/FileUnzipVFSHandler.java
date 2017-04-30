package droidefense.handler;

import droidefense.handler.base.AbstractHandler;
import droidefense.mod.vfs.model.impl.VirtualFile;
import droidefense.mod.vfs.model.impl.VirtualFolder;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by sergio on 16/2/16.
 */
public class FileUnzipVFSHandler extends AbstractHandler {

    private static final int BUFFER_SIZE = 4096;
    private static final String ROOT_FOLDER = "/";
    private final VirtualFolder root;
    private VirtualFolder parentNode;
    private AbstractHashedFile source;
    private ArrayList<VirtualFile> files;

    public FileUnzipVFSHandler(DroidefenseProject currentProject, AbstractHashedFile source) {
        this.source = source;
        this.root = VirtualFolder.createFolder(ROOT_FOLDER);
        this.parentNode = root;
        this.project = currentProject;
        this.files = new ArrayList<>();
    }

    @Override
    public boolean doTheJob() {
        //read zip file
        ZipInputStream zipIn;
        try {
            zipIn = new ZipInputStream(source.getStream());
            ZipEntry entry = zipIn.getNextEntry();

            // iterates over entries in the zip file
            while (entry != null) {
                //reset parent node
                parentNode = root;
                String entryName = entry.getName();
                if (!entry.isDirectory()) {
                    //check if entry parent directory exists on vfs
                    String[] items = entryName.split("/");
                    if (items.length > 1) {
                        //subfolder found
                        for (int i = 0; i < items.length - 1; i++) {
                            VirtualFolder vf = VirtualFolder.createFolder(parentNode, items[i]);
                            parentNode = vf;
                        }
                        entryName = items[items.length - 1];
                    }
                    // if the entry is a file, extracts it
                    VirtualFile virtualFile = VirtualFile.createFile(parentNode, entryName);
                    files.add(virtualFile);
                    byte[] bytesIn = new byte[BUFFER_SIZE];
                    int read;
                    int offset = 0;
                    while ((read = zipIn.read(bytesIn)) != -1) {
                        virtualFile.addContent(bytesIn, 0, read);
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            //close zip file access
            zipIn.close();
            project.setVFS(root);
            project.getVFS().print();
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

    public ArrayList<VirtualFile> getFiles() {
        return files;
    }
}
