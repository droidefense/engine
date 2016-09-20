package com.zerjioang.apkr.v1.core.analysis.sttc.handlers;


import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

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
    private ArrayList<ResourceFile> files;
    private int nfolder, nfiles;

    public DirScannerHandler(File outputDir, boolean generateInformation, DirScannerFilter filter) {
        this.outputDir = outputDir;
        this.generateInformation = generateInformation;
        this.filter = filter;
    }

    @Override
    public boolean doTheJob() {
        ArrayList<ResourceFile> files = enumFiles();
        return files != null && !files.isEmpty();
    }

    private ArrayList<ResourceFile> enumFiles() {
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
                            ResourceFile resfile = new ResourceFile(file.toFile(), generateInformation);
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

    public ArrayList<ResourceFile> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<ResourceFile> files) {
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
