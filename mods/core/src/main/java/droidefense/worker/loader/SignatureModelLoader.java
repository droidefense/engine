package droidefense.worker.loader;

import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.exception.ConfigFileNotFoundException;
import droidefense.handler.FileIOHandler;
import droidefense.sdk.helpers.DroidDefenseEnvironmentConfig;
import droidefense.sdk.util.InternalConstant;
import droidefense.sdk.model.signature.Signature;
import droidefense.sdk.model.signature.SignatureMap;

import java.io.*;

/**
 * Created by sergio on 16/2/16.
 */
public class SignatureModelLoader implements Serializable {

    private SignatureMap model;

    public void load() {
        model = new SignatureMap();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = null;
        try {
            DroidDefenseEnvironmentConfig environmentConfig = DroidDefenseEnvironmentConfig.getInstance();
            cvsSplitBy = environmentConfig.CVS_SPLIT;

            try {
                InputStream csvFile = FileIOHandler.getFileInputStream(
                        environmentConfig.RESOURCE_FOLDER
                                + File.separator
                                + environmentConfig.SIGNATURE_FILE
                );
                br = new BufferedReader(new InputStreamReader(csvFile));
                while ((line = br.readLine()) != null) {
                    // use comma as separator
                    String[] data = line.split(cvsSplitBy);

                    int[] signatureBytes;
                    String[] bbytes = data[1].trim().split(InternalConstant.SPACE);
                    signatureBytes = new int[bbytes.length];
                    for (int i = 0; i < bbytes.length; i++) {
                        signatureBytes[i] = Integer.parseInt(bbytes[i], 16);
                    }
                    String extension = data[0];
                    Signature newSignature = new Signature(extension, signatureBytes, data[2]);
                    model.put(extension, newSignature);
                }

            } catch (FileNotFoundException e) {
                Log.write(LoggerType.ERROR, "Signature model file not found", e.getLocalizedMessage());
            } catch (Exception e) {
                Log.write(LoggerType.ERROR, e.getLocalizedMessage());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.write(LoggerType.ERROR, e.getLocalizedMessage());
                    }
                }
            }
        } catch (ConfigFileNotFoundException e) {
            Log.write(LoggerType.FATAL, "Could not retrieve CVS_SPLIT from external config file", e.getLocalizedMessage());
        }

        System.out.println("Done");
    }

    public final SignatureMap getModel() {
        return model;
    }

    public final void setModel(SignatureMap model) {
        this.model = model;
    }

}