package com.zerjioang.apkr.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.MagicFileHandler;
import com.zerjioang.apkr.handler.SignatureHandler;
import com.zerjioang.apkr.parser.base.AbstractFileParser;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidResourcesParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\nParsing Android resource files...\n");

        ArrayList<ApkrFile> list = ApkrProject.getProject(apk).getAppFiles();

        SignatureHandler signatureHandler = new SignatureHandler();
        signatureHandler.setApk(apk);

        MagicFileHandler magicFileHandler = new MagicFileHandler();
        magicFileHandler.setApk(apk);
        for (ApkrFile resource : list) {
            if (resource.getThisFile().isFile()) {
                //run signature match
                String extension = "";
                if (resource.hasExtension()) {
                    extension = resource.extractExtensionFromName();
                }

                signatureHandler.setApkrFile(resource);
                signatureHandler.setNameExtension(extension);
                signatureHandler.doTheJob();
                //resource = signatureHandler.getUpdatedResource();
                signatureHandler.updateDescription();
                //run magic file command
                magicFileHandler.setResource(resource);
                boolean success = magicFileHandler.doTheJob();
                if (success)
                    resource.setMagicDescription(magicFileHandler.getAnswer());
            }
        }
    }
}