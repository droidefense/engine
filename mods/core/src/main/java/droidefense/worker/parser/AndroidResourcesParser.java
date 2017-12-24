package droidefense.worker.parser;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import droidefense.handler.MagicFileHandler;
import droidefense.handler.SignatureHandler;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.sdk.model.io.VirtualHashedFile;
import droidefense.vfs.model.impl.VirtualFile;
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

        SignatureHandler signatureHandler = SignatureHandler.getInstance();
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
            signatureHandler.updateDescription();

            //run magic file
            runMagicFile(resource, vf);
        }
    }

    private void runMagicFile(VirtualHashedFile resource, VirtualFile file) {
        // create a magic utility using the internal magic file
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info = util.findMatch(file.getContent());
        // display content type information
        if (info != null) {
            // other information in ContentInfo type
            resource.setMagicDescription(info.getName());
            resource.setContentInfo(info);
        }
    }
}