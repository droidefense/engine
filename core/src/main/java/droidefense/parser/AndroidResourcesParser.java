package droidefense.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.MagicFileHandler;
import droidefense.handler.SignatureHandler;
import droidefense.parser.base.AbstractFileParser;
import droidefense.sdk.model.base.AbstractHashedFile;
import droidefense.sdk.model.base.DroidefenseProject;

import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidResourcesParser extends AbstractFileParser {

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\nParsing Android resource files...\n");

        ArrayList<AbstractHashedFile> list = DroidefenseProject.getProject(apk).getAppFiles();

        SignatureHandler signatureHandler = new SignatureHandler();
        signatureHandler.setApk(apk);

        MagicFileHandler magicFileHandler = new MagicFileHandler();
        magicFileHandler.setApk(apk);
        for (AbstractHashedFile resource : list) {
            if (resource.isFile()) {
                //run signature match
                String extension = "";
                if (resource.hasExtension()) {
                    extension = resource.extractExtensionFromName();
                }

                signatureHandler.setApkFile(resource);
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