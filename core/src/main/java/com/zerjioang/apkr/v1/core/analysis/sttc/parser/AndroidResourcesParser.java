package com.zerjioang.apkr.v1.core.analysis.sttc.parser;

import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.MagicFileHandler;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.SignatureHandler;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidResourcesParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\nParsing Android resource files...\n");

        ArrayList<ResourceFile> list = ApkrProject.getProject(apk).getAppFiles();

        SignatureHandler signatureHandler = new SignatureHandler();
        signatureHandler.setApk(apk);

        MagicFileHandler magicFileHandler = new MagicFileHandler();
        magicFileHandler.setApk(apk);
        for (ResourceFile resource : list) {
            if (resource.getThisFile().isFile()) {
                //run signature match
                String extension = "";
                if (resource.hasExtension()) {
                    extension = resource.extractExtensionFromName();
                }

                signatureHandler.setResourceFile(resource);
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