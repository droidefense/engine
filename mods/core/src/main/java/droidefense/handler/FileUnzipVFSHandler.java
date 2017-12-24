package droidefense.handler;

import droidefense.handler.base.AbstractHandler;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.vfs.model.impl.VirtualFileSystem;
import droidefense.vfs.model.impl.VirtualFolder;
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
    private static final String ROOT_FOLDER = "";
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
        Log.write(LoggerType.DEBUG, "[START] Unpacking...");
        //read zip file
        ZipInputStream zipIn;
        try {
            zipIn = new ZipInputStream(source.getStream());
            ZipEntry entry = zipIn.getNextEntry();

            // iterates over entries in the zip file
            while (entry != null) {
                //cleanThreadContext parent node
                parentNode = root;
                String entryName = entry.getName();
                Log.write(LoggerType.DEBUG, "\t"+entryName);
                if (!entry.isDirectory()) {
                    //check if entry parent directory exists on vfs
                    String[] items = entryName.split("/");
                    if (items.length > 1) {
                        //subfolder found
                        for (int i = 0; i < items.length - 1; i++) {
                            parentNode = VirtualFolder.createFolder(parentNode, items[i]);
                        }
                        entryName = items[items.length - 1];
                    }
                    // if the entry is a file, extracts it
                    VirtualFile virtualFile = VirtualFile.createFile(parentNode, entryName);
                    files.add(virtualFile);
                    byte[] bytesIn = new byte[BUFFER_SIZE];
                    int read;
                    while ((read = zipIn.read(bytesIn)) != -1) {
                        virtualFile.addContent(bytesIn, 0, read);
                    }
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            //close zip file access
            zipIn.close();

            VirtualFileSystem vfs = new VirtualFileSystem();
            vfs.add(ROOT_FOLDER, root);
            project.setVFS(vfs);
            Log.write(LoggerType.DEBUG, "Virtual file system created");
            //vfs.info(); //only show if verbose is true
            return true;
        } catch (FileNotFoundException e) {
            error = e;
            Log.write(LoggerType.ERROR, "Could not find target file to unzip", e.getLocalizedMessage());
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, "An IO error ocurred while unpacking", e.getLocalizedMessage());
            error = e;
        } catch (Exception e) {
            Log.droidefenseGenericError(e);
            error = e;
        }
        return false;
    }

    public ArrayList<VirtualFile> getFiles() {
        return files;
    }
}
