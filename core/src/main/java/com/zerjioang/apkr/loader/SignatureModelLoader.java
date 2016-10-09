package com.zerjioang.apkr.loader;

import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.signature.Signature;
import com.zerjioang.apkr.sdk.model.signature.SignatureList;

import java.io.*;

/**
 * Created by sergio on 16/2/16.
 */
public class SignatureModelLoader implements Serializable {

    private SignatureList model;

    public void load() {
        model = new SignatureList();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ApkrConstants.CVS_SPLIT;

        try {
            InputStream csvFile = FileIOHandler.getApkrFileInputStream(ApkrConstants.INTERNAL_DATA_FOLDER + File.separator + ApkrConstants.SIGNATURE_FILE);
            br = new BufferedReader(new InputStreamReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                int[] signatureBytes;
                String[] bbytes = data[1].trim().split(" ");
                signatureBytes = new int[bbytes.length];
                for (int i = 0; i < bbytes.length; i++) {
                    signatureBytes[i] = Integer.parseInt(bbytes[i], 16);
                }
                model.add(new Signature(data[0], signatureBytes, data[2]));
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

    public final SignatureList getModel() {
        return model;
    }

    public final void setModel(SignatureList model) {
        this.model = model;
    }

}