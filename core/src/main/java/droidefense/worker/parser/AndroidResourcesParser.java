package droidefense.worker.parser;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.MagicFileHandler;
import droidefense.handler.SignatureHandler;
import droidefense.mod.vfs.model.impl.VirtualFile;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.sdk.model.io.VirtualHashedFile;
import droidefense.worker.base.AbstractFileParser;

import java.util.ArrayList;

/**
 * Created by r00t on 24/10/15.
 */
public class AndroidResourcesParser extends AbstractFileParser {

    public AndroidResourcesParser(LocalApkFile apk, DroidefenseProject currentProject) {
        super(apk, currentProject);
    }

    @Override
    public void parserCode() {
        Log.write(LoggerType.INFO, "\n\nParsing Android resource files...\n");

        ArrayList<VirtualFile> list = currentProject.getAppFiles();

        SignatureHandler signatureHandler = new SignatureHandler();
        signatureHandler.setApk(apk);

        MagicFileHandler magicFileHandler = new MagicFileHandler();
        magicFileHandler.setApk(apk);
        for (VirtualFile vf : list) {
            VirtualHashedFile resource = new VirtualHashedFile(vf, false);
            //run signature match
            String extension = "";
            if (resource.hasExtension()) {
                extension = resource.extractExtensionFromName();
            }

            signatureHandler.setFile(resource);
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