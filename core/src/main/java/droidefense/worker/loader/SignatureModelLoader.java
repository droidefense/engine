package droidefense.worker.loader;

import droidefense.handler.FileIOHandler;
import droidefense.sdk.helpers.DroidDefenseParams;
import droidefense.sdk.helpers.InternalConstant;
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
        String cvsSplitBy = DroidDefenseParams.getInstance().CVS_SPLIT;

        try {
            InputStream csvFile = FileIOHandler.getApkrFileInputStream(
                    InternalConstant.INTERNAL_DATA_FOLDER
                            + File.separator
                            + DroidDefenseParams.getInstance().SIGNATURE_FILE
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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