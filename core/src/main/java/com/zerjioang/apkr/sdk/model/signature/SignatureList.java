package com.zerjioang.apkr.sdk.model.signature;

import com.zerjioang.apkr.sdk.helpers.Util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public class SignatureList extends ArrayList<Signature> implements Serializable {

    private static final long serialVersionUID = 1L;

    public Signature checkSignature(byte[] buffer) {
        Signature result = null;
        boolean match;
        for (Signature s : this) {
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
}