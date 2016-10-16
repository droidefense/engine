package droidefense.cli;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.APKToolHandler;
import droidefense.handler.DirScannerHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.handler.base.DirScannerFilter;
import droidefense.sdk.model.base.AbstractHashedFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.LocalApkFile;
import droidefense.util.UnpackAction;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum APKUnpacker {

    APKTOOL_UNPACKER {
        @Override
        public ArrayList<AbstractHashedFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile, File outputDir) {
            //unpacks and decode on the same step
            APKToolHandler handler = new APKToolHandler(apkFile, outputDir);
            handler.doTheJob();
            Log.write(LoggerType.TRACE, "Listing unpacked files...");

            //enumerate unpacked files and get information
            DirScannerHandler dirHandler = new DirScannerHandler(outputDir, true, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return true;
                }
            });
            dirHandler.doTheJob();
            ArrayList<AbstractHashedFile> files = dirHandler.getFiles();
            Log.write(LoggerType.TRACE, "Files found: " + files.size());

            //save metadata
            currentProject.setFolderCount(dirHandler.getNfolder());
            currentProject.setFilesCount(dirHandler.getNfiles());

            return files;
        }
    }, ZIP_UNPACKER {
        @Override
        public ArrayList<AbstractHashedFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile, File outputDir) {
            //only unpacks
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile, new UnpackAction[]{UnpackAction.DECODE, UnpackAction.GENERATE_HASH});
            handler.doTheJob();
            return handler.getFiles();
        }
    };

    public abstract ArrayList<AbstractHashedFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile, File outputDir);
}
