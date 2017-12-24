package droidefense.sdk.enums;

import java.io.Serializable;

/**
 * Created by .local on 14/05/2016.
 */
public enum PrivacyResultEnum implements Serializable {

    SAFE("SAFE"), SUSPICIOUS("SUSPICIOUS"), DATA_LEAK("DATA LEAK"), UNKNOWN("UNKNOWN");

    private String type;

    PrivacyResultEnum(String type) {
        this.type = type;
    }
}
