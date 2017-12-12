package droidefense.sdk.helpers;

import droidefense.handler.FileIOHandler;
import droidefense.handler.apktool.MemAPKToolHandler;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.handler.apktool.APKToolHandler;
import droidefense.handler.AXMLDecoderHandler;
import droidefense.handler.FileUnzipVFSHandler;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.io.LocalHashedFile;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by .local on 14/10/2016.
 */
public enum APKUnpacker {

    APKTOOL {
        @Override
        public boolean unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            APKToolHandler handler = new APKToolHandler(currentProject, apkFile);
            handler.doTheJob();
            //TODO enable file, folder counting
            currentProject.setCorrectDecoded(true);
            //TODO currentProject.getAppFiles(); returns 0 files

            //count extracted files
            ArrayList<AbstractHashedFile> filelist = new ArrayList<>();
            File outDir = FileIOHandler.getApkUnpackDir(currentProject);
            try {
                Files.walk(Paths.get(outDir.getAbsolutePath()))
                        .filter(Files::isRegularFile)
                        .forEach(path -> filelist.add(
                                new LocalHashedFile(path.toFile(), LocalHashedFile.ENABLE_HASHING)
                        ));
                currentProject.setLocalAppFiles(filelist);
            } catch (IOException e) {
                Log.write(LoggerType.ERROR, "Could not read unpacked apk files", e.getLocalizedMessage());
            }
            return filelist.size() > 0;
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //files are already decoded when unpacking
            return currentProject.getAppFiles();
        }

    },
    MEMAPKTOOL {
        @Override
        public boolean unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            MemAPKToolHandler handler = new MemAPKToolHandler(currentProject, apkFile);
            handler.doTheJob();
            //todo implement memapktool decoding
            //TODO enable file, folder counting
            currentProject.setCorrectDecoded(true);
            ArrayList<VirtualFile> appfiles = currentProject.getAppFiles();
            return appfiles!= null && appfiles.size() > 0;
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            //files are already decoded when unpacking
            return currentProject.getAppFiles();
        }

    }, ZIP {
        @Override
        public boolean unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile) {
            FileUnzipVFSHandler handler = new FileUnzipVFSHandler(currentProject, apkFile);
            handler.doTheJob();
            ArrayList<VirtualFile> appfiles = handler.getFiles();
            return appfiles!= null && appfiles.size() > 0;
        }

        @Override
        public ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files) {
            int folderCount = 0;
            int filesCount = 0;
            AXMLDecoderHandler decoder = new AXMLDecoderHandler();

            ArrayList<VirtualFile> xmlFileList = new ArrayList<>();
            ArrayList<VirtualFile> ninePatchImageList = new ArrayList<>();
            //todo implement axml, 9.png and resource decoder
            for (VirtualFile file : files) {
                folderCount += file.isFolder() ? 1 : 0;
                filesCount += file.isFile() ? 1 : 0;
                if (isXml(file)) {
                    Log.write(LoggerType.DEBUG, "Decoding XML file " + file.getPath());
                    decoder.setFile(file);
                    decoder.doTheJob();
                    xmlFileList.add(file);
                }
                else if(isManifestMF(file)){
                    //skipping. no need to decode. it is raw file.
                    //parsing might be useful
                    Log.write(LoggerType.DEBUG, "Decoding *.MF file " + file.getPath());
                }
                else if(isManifestSF(file)){
                    //skipping. no need to decode. it is raw file.
                    //parsing might be useful
                    Log.write(LoggerType.DEBUG, "Decoding *.SF file " + file.getPath());
                }
                else if(isRSA(file)){
                    Log.write(LoggerType.DEBUG, "Decoding *.RSA file " + file.getPath());
                }
                else if(is9patch(file)){
                    //skipping. no need to decode. it is raw file
                    Log.write(LoggerType.DEBUG, "Decoding *.9.png image file " + file.getPath());
                    ninePatchImageList.add(file);
                }
                else if(isResourcesARSC(file)){
                    Log.write(LoggerType.DEBUG, "Decoding resources.arsc file " + file.getPath());
                }
                else {
                    Log.write(LoggerType.TRACE, "Skipping " + file.getPath());
                }
            }
            currentProject.setFolderCount(folderCount);
            currentProject.setFilesCount(filesCount);
            currentProject.setCorrectDecoded(files.size() > 0);
            currentProject.setXmlFiles(xmlFileList);
            currentProject.ninePatchImageFiles(ninePatchImageList);
            return files;
        }
    };

    private static boolean isResourcesARSC(VirtualFile file) {
        return (file != null) &&
                (
                        file.getPath().toLowerCase().equals("resources.arsc")
                        ||
                        file.getPath().toLowerCase().endsWith(".arsc")
                );
    }

    private static boolean is9patch(VirtualFile file) {
        return (file != null) && file.getPath().toLowerCase().endsWith(".9.png");
    }

    private static boolean isManifestMF(VirtualFile file) {
        return (file != null) && file.getPath().toUpperCase().matches("META-INF/\\w+\\.MF");
    }

    private static boolean isRSA(VirtualFile file) {
        return (file != null) && file.getPath().toUpperCase().matches("META-INF/\\w+\\.RSA");
    }

    private static boolean isManifestSF(VirtualFile file) {
        return (file != null) && file.getPath().toUpperCase().matches("META-INF/\\w+\\.SF");
    }

    private static boolean isXml(VirtualFile file) {
        return file.getName().endsWith(".xml");
    }

    /**
     *
     * @param apkFile input sample loaded
     * @return a list of internal sample files unpacked
     */
    public abstract boolean unpackWithTechnique(DroidefenseProject currentProject, LocalApkFile apkFile);

    /**
     *
     * @param currentProject input project loaded
     * @param files internal unpacked file list, ready to be decoded
     * @return a list of internal sample files decoded
     */
    public abstract ArrayList<VirtualFile> decodeWithTechnique(DroidefenseProject currentProject, ArrayList<VirtualFile> files);

    public static APKUnpacker getUnpackerFromStringName(String name) {
        if(name != null){
            if (name.equalsIgnoreCase(APKUnpacker.APKTOOL.name())) {
                return APKUnpacker.APKTOOL;
            } else if (name.equalsIgnoreCase(APKUnpacker.ZIP.name())) {
                return APKUnpacker.ZIP;
            }
        }
        //return as default unpacker if none selected
        return APKUnpacker.ZIP;
    }
}
