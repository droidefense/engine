package droidefense.worker.parser;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.handler.FileIOHandler;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.io.DexHashedFile;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.worker.base.AbstractFileParser;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 15/3/16.
 */
public class ApkDecompilerParser extends AbstractFileParser {

    public ApkDecompilerParser(LocalApkFile apk, DroidefenseProject currentProject) {
        super(apk, currentProject);
    }

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nDecompiling sample...\n");
        ArrayList<DexHashedFile> dexFiles = currentProject.getDexList();
        for (AbstractHashedFile dex : dexFiles) {
            Log.write(LoggerType.TRACE, "DECOMPILING... " + dex.getAbsolutePath());

            File decompiledPath = FileIOHandler.getDecompiledPath(currentProject);
            //create folder if not exist
            if (!decompiledPath.exists())
                decompiledPath.mkdirs();
            else {
                //decompiled output folder already exists. which means that app is already decompiled. Skip
                currentProject.setSuccessfullDecompilation(true);
                Log.write(LoggerType.TRACE, "App is already decompiled on ", decompiledPath.getAbsolutePath(), dex);
                return;
            }

            //decompile .jar
            //use jadx decompiler
            //JADX usgae: https://github.com/skylot/jadx
            if (dex.exists() && dex.canRead() && dex.canWrite()) {
                //TODO Decompile .jar
                //options..., input_file
                String[] args = {"-r", "--escape-unicode", "--show-bad-code", "-d", decompiledPath.getAbsolutePath(), dex.getAbsolutePath()};
                /*try {
                    new JadxCLI(args);
                    currentProject.setSuccessfullDecompilation(true);
                } catch (JadxException e) {
                    Log.write(LoggerType.FATAL, "Could not decompile ", dex, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                    currentProject.setSuccessfullDecompilation(false);
                } catch (Exception e) {
                    Log.write(LoggerType.FATAL, "Fatal excepcion during decompilation ", dex, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                    currentProject.setSuccessfullDecompilation(false);
                }*/
            } else {
                Log.write(LoggerType.FATAL, "Could not find, read or write output decompilation file", dex);
            }
        }
    }
}
