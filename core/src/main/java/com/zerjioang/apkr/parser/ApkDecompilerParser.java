package com.zerjioang.apkr.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.parser.base.AbstractFileParser;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import jadx.cli.JadxCLI;
import jadx.core.utils.exceptions.JadxException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 15/3/16.
 */
public class ApkDecompilerParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        ArrayList<ApkrFile> dexFiles = currentProject.getDexList();
        for (ApkrFile dex : dexFiles) {
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
            File source = dex.getThisFile();
            if (source.exists() && source.canRead() && source.canWrite()) {
                //TODO Decompile .jar
                //options..., input_file
                String[] args = {"-r", "--escape-unicode", "--show-bad-code", "-d", decompiledPath.getAbsolutePath(), source.getAbsolutePath()};
                try {
                    new JadxCLI(args);
                    currentProject.setSuccessfullDecompilation(true);
                } catch (JadxException e) {
                    Log.write(LoggerType.FATAL, "Could not decompile ", source, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                    currentProject.setSuccessfullDecompilation(false);
                } catch (Exception e) {
                    Log.write(LoggerType.FATAL, "Fatal excepcion during decompilation ", source, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                    currentProject.setSuccessfullDecompilation(false);
                }
            } else {
                Log.write(LoggerType.FATAL, "Could not find, read or write output decompilation file", dex);
            }
        }
    }
}
