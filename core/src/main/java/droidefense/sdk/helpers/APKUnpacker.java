package droidefense.sdk.helpers;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.APKToolHandler;
import droidefense.handler.AXMLDecoderHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.mod.vfs.model.impl.VirtualFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;

import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum APKUnpacker {

    APKTOOL {
        @Override
        public ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            APKToolHandler handler = new APKToolHandler(currentProject, apkFile);
            handler.doTheJob();
            //todo implement memapktool decoding
            //TODO enable file, folder counting
            currentProject.setCorrectDecoded(true);
            return currentProject.getAppFiles();
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //files are already decoded when unpacking
            return currentProject.getAppFiles();
        }

    }, ZIP {
        @Override
        public ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            //only unpacks
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();
            return handler.getFiles();
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //todo implement axml, 9.png and resource decoder
            for (VirtualFile file : files) {
                if (file.getName().endsWith(DroidDefenseParams.getInstance().XML_EXTENSION)) {
                    Log.write(LoggerType.INFO, "Decoding file " + file.getPath());
                    AXMLDecoderHandler decoder = new AXMLDecoderHandler(file);
                    decoder.doTheJob();
                } else {
                    Log.write(LoggerType.ERROR, "File" + file.getPath() + " was not decoded");
                }
            }
            currentProject.setCorrectDecoded(files.size() > 0);
            return files;
        }
    };

    public abstract ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile);

    public abstract ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files);
}
