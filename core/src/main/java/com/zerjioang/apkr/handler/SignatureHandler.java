package com.zerjioang.apkr.handler;


import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.base.AbstractHandler;
import com.zerjioang.apkr.loader.SignatureModelLoader;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.signature.Signature;
import com.zerjioang.apkr.sdk.model.signature.SignatureList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private ApkrFile ApkrFile;
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
        try {
            in = new FileInputStream(ApkrFile.getThisFile());
            int n;
            try {
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
                Log.write(LoggerType.ERROR, "Atom Engine could not 'do the job'", e.getLocalizedMessage(), e);
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.write(LoggerType.ERROR, "Atom Engine could not 'do the job'", e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    public ApkrFile getUpdatedResource() {
        return ApkrFile;
    }

    public ApkrFile getApkrFile() {
        return ApkrFile;
    }

    public void setApkrFile(ApkrFile ApkrFile) {
        this.ApkrFile = ApkrFile;
    }

    public void updateDescription() {
        if (valid) {
            getApkrFile().setExtension(nameExtension);
            getApkrFile().setDescription(description);
            getApkrFile().setHeaderBasedExtension(expectedFiletype);
            getApkrFile().setSignatureMatches();
        } else {
            Log.write(LoggerType.DEBUG, "File NOT identified: " + getApkrFile().getThisFile().getName());
            getApkrFile().setExtension(nameExtension);
            getApkrFile().setDescription("unknown");
            getApkrFile().setHeaderBasedExtension("unknown");
        }
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }
}
