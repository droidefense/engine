package droidefense.sdk.model.signature;

import droidefense.sdk.util.Util;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sergio on 16/2/16.
 */
public final class SignatureMap extends HashMap<String, Signature> implements Serializable {

    private static final long serialVersionUID = 1L;

    public Signature checkSignature(String extension, byte[] buffer) {
        //first attemp to match given header buffer with default extension buffer
        Signature matchedSignature = isDefaultHeaderMatch(extension, buffer);
        if (matchedSignature != null) {
            return matchedSignature;
        } else {
            //find file real signature on database
            return findFileSignature(buffer);
        }
    }

    private Signature findFileSignature(byte[] buffer) {
        Signature result = null;
        boolean match;
        for (Signature s : this.values()) {
            match = Util.checkHexSignature(buffer, s.getSignatureBytes(), true);
            if (match) {
                if (result == null) {
                    //first match detected
                    result = s;
                } else if (s.getSignatureLength() >= result.getSignatureLength()) {
                    //detected matched signature is larger than the previous one. Update definition
                    result = s;
                }
            }
        }
        return result;
    }

    private Signature isDefaultHeaderMatch(String extension, byte[] fileBuffer) {
        Signature defaultSignature = this.get(extension);
        if (defaultSignature != null) {
            boolean match = Util.checkHexSignature(fileBuffer, defaultSignature.getSignatureBytes(), true);
            if (match) {
                return defaultSignature;
            }
        }
        return null;
    }
}