package droidefense.cli;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.APKToolHandler;
import droidefense.handler.AXMLDecoderHandler;
import droidefense.handler.DirScannerHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.handler.base.DirScannerFilter;
import droidefense.sdk.model.base.APKFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.HashedFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum APKUnpacker {

    APKTOOL_UNPACKER {
        @Override
        public ArrayList<HashedFile> unpackWithTechnique(DroidefenseProject currentProject, APKFile apkFile, File outputDir) {
            //unpacks and decode on the same step
            APKToolHandler handler = new APKToolHandler(apkFile, outputDir);
            handler.doTheJob();
            Log.write(LoggerType.TRACE, "Listing unpacked files...");

            //enumerate unpacked files and get information
            DirScannerHandler dirHandler = new DirScannerHandler(outputDir, GENERATE_HASHES, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return true;
                }
            });
            dirHandler.doTheJob();
            ArrayList<HashedFile> files = dirHandler.getFiles();
            Log.write(LoggerType.TRACE, "Files found: " + files.size());

            //save metadata
            currentProject.setFolderCount(dirHandler.getNfolder());
            currentProject.setFilesCount(dirHandler.getNfiles());

            return files;
        }
    }, ZIP_UNPACKER {
        @Override
        public ArrayList<HashedFile> unpackWithTechnique(DroidefenseProject currentProject, APKFile apkFile, File outputDir) {
            //only unpacks
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();
            Log.write(LoggerType.TRACE, "Listing unpacked files...");

            //TODO generate hashes of the files
            DirScannerHandler dirHandler = new DirScannerHandler(outputDir, GENERATE_HASHES, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return true;
                }
            });
            handler.doTheJob();

            //get extracted files
            ArrayList<HashedFile> files = dirHandler.getFiles();
            Log.write(LoggerType.TRACE, "Files found: " + files.size());

            Log.write(LoggerType.TRACE, "Decoding XML resources");
            //decode unpacked files
            AXMLDecoderHandler decoder = new AXMLDecoderHandler(files);
            decoder.doTheJob();

            //save metadata
            currentProject.setFolderCount(dirHandler.getNfolder());
            currentProject.setFilesCount(dirHandler.getNfiles());

            return files;
        }
    };

    public static final boolean GENERATE_HASHES = true;

    public abstract ArrayList<HashedFile> unpackWithTechnique(DroidefenseProject currentProject, APKFile apkFile, File outputDir);
}
