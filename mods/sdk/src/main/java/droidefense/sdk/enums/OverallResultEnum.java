package droidefense.sdk.enums;

/**
 * Created by .local on 23/10/2016.
 */
public enum OverallResultEnum {

    UNKNOWN("Unknown"),
    SUSPICIOUS("Suspicious"),
    SMS_SENDER("SMS sender"),
    FAKE_APP("Fake app"),
    BANKER("Banker"),
    TROJAN("Trojan"),
    TROJANIZED("Trojanized app");

    private String type;

    OverallResultEnum(String type) {
        this.type = type;
    }
}
