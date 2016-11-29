package droidefense.worker.handler;


import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.signature.Signature;
import droidefense.sdk.model.signature.SignatureList;
import droidefense.worker.handler.base.AbstractHandler;
import droidefense.worker.loader.SignatureModelLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sergio on 16/2/16.
 */
public class SignatureHandler extends AbstractHandler {

    //Class vars
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_SIGNATURE_SIZE = 40;
    private static boolean loaded;
    private static SignatureList model;

    //instance vars
    private String description;
    private String expectedFiletype;
    private AbstractHashedFile apkFile;
    private boolean valid;
    private String nameExtension;

    public SignatureHandler() {
        if (!loaded) {
            SignatureModelLoader loader = new SignatureModelLoader();
            loader.load();
            model = loader.getModel();
            loaded = true;
        }
        valid = false;
    }

    @Override
    public boolean doTheJob() {
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream in;
        int n;
        try {
            in = apkFile.getStream();
            n = in.read(buffer, 0, BUFFER_SIZE);
            int m = n;
            while ((m < MAX_SIGNATURE_SIZE) && (n > 0)) {
                n = in.read(buffer, m, BUFFER_SIZE - m);
                m += n;
            }
            in.close();
            Signature s = model.checkSignature(buffer);
            if (s != null) {
                expectedFiletype = s.getExtension();
                description = s.getFiletypeInfo();
                valid = expectedFiletype.equalsIgnoreCase(nameExtension);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.write(LoggerType.ERROR, "Droidefense could not 'do the job'", e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public AbstractHashedFile getUpdatedResource() {
        return apkFile;
    }

    public AbstractHashedFile getApkFile() {
        return apkFile;
    }

    public void setApkFile(AbstractHashedFile AbstractHashedFile) {
        this.apkFile = AbstractHashedFile;
    }

    public void updateDescription() {
        if (valid) {
            getApkFile().setExtension(nameExtension);
            getApkFile().setDescription(description);
            getApkFile().setHeaderBasedExtension(expectedFiletype);
            getApkFile().setSignatureMatches();
        } else {
            Log.write(LoggerType.DEBUG, "File NOT identified: " + getApkFile().getName());
            getApkFile().setExtension(nameExtension);
            getApkFile().setDescription("unknown");
            getApkFile().setHeaderBasedExtension("unknown");
        }
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }
}
