package droidefense.handler;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.base.AbstractHandler;
import droidefense.handler.base.DirScannerFilter;
import droidefense.sdk.model.base.HashedFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by sergio on 16/2/16.
 */
public class DirScannerHandler extends AbstractHandler {

    private final DirScannerFilter filter;
    private File outputDir;
    private boolean generateInformation;
    private ArrayList<HashedFile> files;
    private int nfolder, nfiles;

    public DirScannerHandler(File outputDir, boolean generateInformation, DirScannerFilter filter) {
        this.outputDir = outputDir;
        this.generateInformation = generateInformation;
        this.filter = filter;
    }

    @Override
    public boolean doTheJob() {
        ArrayList<HashedFile> files = enumFiles();
        return files != null && !files.isEmpty();
    }

    private ArrayList<HashedFile> enumFiles() {
        files = new ArrayList<>();

        //walk file tree
        final Path sourceDir = Paths.get(outputDir.getAbsolutePath());
        try {
            Files.walkFileTree(sourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            nfolder++;
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            HashedFile resfile = new HashedFile(file.toFile(), generateInformation);
                            if (filter.addFile(resfile.getThisFile())) {
                                nfiles++;
                                files.add(resfile);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            Log.write(LoggerType.FATAL, "Coud not read directory content", e.getLocalizedMessage());
        }
        return files;
    }

    public ArrayList<HashedFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<HashedFile> files) {
        this.files = files;
    }

    public int getNfolder() {
        return nfolder;
    }

    public void setNfolder(int nfolder) {
        this.nfolder = nfolder;
    }

    public int getNfiles() {
        return nfiles;
    }

    public void setNfiles(int nfiles) {
        this.nfiles = nfiles;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }
}
