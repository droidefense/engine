package com.zerjioang.apkr.v1.common.datamodel.enums;

import java.io.Serializable;

/**
 * Created by .local on 14/05/2016.
 */
public enum PrivacyResultEnum implements Serializable {

    SAFE("SAFE"), SUSPICIOUS("SUSPICIOUS"), DATA_LEAK("DATA_LEAK"), UNKNOWN("UNKNOWN");

    private String type;

    PrivacyResultEnum(String type) {
        this.type = type;
    }
}
