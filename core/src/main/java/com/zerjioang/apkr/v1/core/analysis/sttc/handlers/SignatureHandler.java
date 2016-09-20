package com.zerjioang.apkr.v1.core.analysis.sttc.handlers;

import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.datamodel.signature.Signature;
import com.zerjioang.apkr.v1.common.datamodel.signature.SignatureList;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.core.analysis.sttc.loaders.SignatureModelLoader;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

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
    private ResourceFile resourceFile;
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
            in = new FileInputStream(resourceFile.getThisFile());
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

    public ResourceFile getUpdatedResource() {
        return resourceFile;
    }

    public ResourceFile getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(ResourceFile resourceFile) {
        this.resourceFile = resourceFile;
    }

    public void updateDescription() {
        if (valid) {
            getResourceFile().setExtension(nameExtension);
            getResourceFile().setDescription(description);
            getResourceFile().setHeaderBasedExtension(expectedFiletype);
            getResourceFile().setSignatureMatches();
        } else {
            Log.write(LoggerType.DEBUG, "File NOT identified: " + getResourceFile().getThisFile().getName());
            getResourceFile().setExtension(nameExtension);
            getResourceFile().setDescription("unknown");
            getResourceFile().setHeaderBasedExtension("unknown");
        }
    }

    public String getNameExtension() {
        return nameExtension;
    }

    public void setNameExtension(String nameExtension) {
        this.nameExtension = nameExtension;
    }
}
