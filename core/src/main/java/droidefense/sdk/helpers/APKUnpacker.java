package droidefense.sdk.helpers;

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

    APKTOOL_UNPACKER {
        @Override
        public ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            //unpacks and decode on the same step
            APKToolHandler handler = new APKToolHandler(currentProject, apkFile);
            handler.doTheJob();
            //TODO enable file, folder counting
            return null;
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //todo implement axml, 9.png and resource decoder
            currentProject.setCorrectDecoded(true);
            return null;
        }

    }, ZIP_UNPACKER {
        @Override
        public ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            //only unpacks
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();
            //TODO enable file, folder counting
            return handler.getFiles();
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //todo implement axml, 9.png and resource decoder
            for (int i = 0; i < files.size(); i++) {
                AXMLDecoderHandler decoder = new AXMLDecoderHandler(files.get(i));
                decoder.doTheJob();
            }
            currentProject.setCorrectDecoded(files.size() > 0);
            return files;
        }
    };

    public abstract ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile);

    public abstract ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files);
}
