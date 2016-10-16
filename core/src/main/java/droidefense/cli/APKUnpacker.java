package droidefense.cli;

import droidefense.handler.APKToolHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.mod.vfs.model.base.IVirtualNode;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.LocalApkFile;

import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum APKUnpacker {

    APKTOOL_UNPACKER {

        @Override
        public ArrayList<IVirtualNode> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            //unpacks and decode on the same step
            APKToolHandler handler = new APKToolHandler(currentProject, apkFile);
            handler.doTheJob();

            //TODO enable file, folder counting
            /*
            ArrayList<IVirtualNode> files = currentProject.getVFS().getRecursiveFileList();

            Log.write(LoggerType.TRACE, "Files found: " + files.size());

            //save metadata
            currentProject.setFolderCount(currentProject.getVFS().getNfolder());
            currentProject.setFilesCount(currentProject.getVFS().getNfiles());
            */

            return null;
        }

        @Override
        public ArrayList<IVirtualNode> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<IVirtualNode> files) {
            //todo implement axml, 9.png and resource decoder
            return null;
        }

    }, ZIP_UNPACKER {
        @Override
        public ArrayList<IVirtualNode> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            //only unpacks
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();

            //TODO enable file, folder counting
            /*
            ArrayList<IVirtualNode> files = currentProject.getVFS().getRecursiveFileList();

            Log.write(LoggerType.TRACE, "Files found: " + files.size());

            //save metadata
            currentProject.setFolderCount(currentProject.getVFS().getNfolder());
            currentProject.setFilesCount(currentProject.getVFS().getNfiles());
            */

            return null;
        }

        @Override
        public ArrayList<IVirtualNode> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<IVirtualNode> files) {
            //todo implement axml, 9.png and resource decoder
            return null;
        }
    };

    public abstract ArrayList<IVirtualNode> unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile);

    public abstract ArrayList<IVirtualNode> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<IVirtualNode> files);
}
