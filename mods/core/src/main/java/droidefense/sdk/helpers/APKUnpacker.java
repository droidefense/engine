package droidefense.sdk.helpers;

import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.handler.APKToolHandler;
import droidefense.handler.AXMLDecoderHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.vfs.model.impl.VirtualFile;
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
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();
            return handler.getFiles();
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            int folderCount = 0;
            int filesCount = 0;
            AXMLDecoderHandler decoder = new AXMLDecoderHandler();
            //todo implement axml, 9.png and resource decoder
            for (VirtualFile file : files) {
                folderCount += file.isFolder() ? 1 : 0;
                filesCount += file.isFile() ? 1 : 0;
                if (isXml(file)) {
                    Log.write(LoggerType.DEBUG, "Decoding file " + file.getPath());
                    decoder.setFile(file);
                    decoder.doTheJob();
                }
                else {
                    Log.write(LoggerType.DEBUG, "File" + file.getPath() + " no need to decode");
                }
            }
            currentProject.setFolderCount(folderCount);
            currentProject.setFilesCount(filesCount);
            currentProject.setCorrectDecoded(files.size() > 0);
            return files;
        }
    };

    private static boolean isXml(VirtualFile file) {
        return file.getName().endsWith(DroidDefenseParams.getInstance().XML_EXTENSION);
    }

    /**
     *
     * @param apkFile input sample loaded
     * @return a list of internal sample files unpacked
     */
    public abstract ArrayList<VirtualFile> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile);

    /**
     *
     * @param currentProject input project loaded
     * @param files internal unpacked file list, ready to be decoded
     * @return a list of internal sample files decoded
     */
    public abstract ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files);
}
