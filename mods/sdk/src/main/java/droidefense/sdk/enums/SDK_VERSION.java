package droidefense.sdk.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by .local on 05/11/2016.
 */
public enum SDK_VERSION {

    @SerializedName("Android 7.1")
    ANDROID_7_1("Android 7.1", 24, "N"),

    @SerializedName("Android 7.0")
    ANDROID_7_0("Android 7.0", 24, "N"),

    @SerializedName("Android 6.0")
    ANDROID_6_0("Android 6.0", 23, "M"),

    @SerializedName("Android 5.1")
    ANDROID_5_1("Android 5.1", 22, "LOLLIPOP MR1"),

    @SerializedName("Android 5.0")
    ANDROID_5_0("Android 5.0", 21, "LOLLIPOP"),

    @SerializedName("Android 4.4W")
    ANDROID_4_4_W("Android 4.4W", 20, "KITKAT WATCH"),

    @SerializedName("Android 4.4")
    ANDROID_4_4("Android 4.4", 19, "KITKAT"),

    @SerializedName("Android 4.3")
    ANDROID_4_3("Android 4.3", 18, "JELLY BEAN MR2"),

    @SerializedName("Android 4.2.2")
    ANDROID_4_2_2("Android 4.2.2", 17, "JELLY BEAN MR1"),

    @SerializedName("Android 4.2")
    ANDROID_4_2("Android 4.2", 17, "JELLY BEAN MR1"),

    @SerializedName("Android 4.1 or Android 4.1.1")
    ANDROID_4_1("Android 4.1 - Android 4.1.1", 16, "JELLY BEAN"),

    @SerializedName("Android 4.0.3 or Android 4.0.4")
    ANDROID_4_0_3("Android 4.0.3 - Android 4.0.4", 15, "ICE CREAM SANDWICH MR1"),

    @SerializedName("Android 4.0 or Android 4.0.2")
    ANDROID_4_0("Android 4.0 - Android 4.0.2", 14, "ICE CREAM SANDWICH"),

    @SerializedName("Android 3.2")
    ANDROID_3_2("Android 3.2", 13, "HONEYCOMB MR2"),

    @SerializedName("Android 3.1.x")
    ANDROID_3_1("Android 3.1.x", 12, "HONEYCOMB MR1"),

    @SerializedName("Android 3.0.x")
    ANDROID_3_0("Android 3.0.x", 11, "HONEYCOMB"),

    @SerializedName("Android 2.3.3 or Android 2.3.4")
    ANDROID_2_3_3("Android 2.3.3 - Android 2.3.4", 10, "GINGERBREAD MR1"),

    @SerializedName("Android 2.3.0 or Android 2.3.2")
    ANDROID_2_3("Android 2.3.0 - Android 2.3.2", 9, "GINGERBREAD"),

    @SerializedName("Android 2.2.x")
    ANDROID_2_2_X("Android 2.2.x", 8, "FROYO"),

    @SerializedName("Android 2.1.x")
    ANDROID_2_1_X("Android 2.1.x", 7, "ECLAIR MR1"),

    @SerializedName("Android 2.0.1")
    ANDROID_2_0_1("Android 2.0.1", 6, "ECLAIR"),

    @SerializedName("Android 2.0")
    ANDROID_2_0("Android 2.0", 5, "ECLAIR"),

    @SerializedName("Android 1.6")
    ANDROID_1_6("Android 1.6", 4, "DONUT"),

    @SerializedName("Android 1.5")
    ANDROID_1_5("Android 1.5", 3, "CUPCAKE"),

    @SerializedName("Android 1.1")
    ANDROID_1_1("Android 1.1", 2, "no code name"),

    @SerializedName("Android 1.0")
    ANDROID_1_0("Android 1.0", 1, "no code name"),

    @SerializedName("Unknown")
    UNKNOWN("Unknown", -1, "unknown");

    private static final int MIN_API_LEVEL = 1;
    private static final int MAX_API_LEVEL = 25;
    private static final SDK_VERSION[] enumValues = SDK_VERSION.values();

    private String androidVersionName;
    private int apiLevel;
    private String codeName;

    SDK_VERSION(String androidVersionName, int apiLevel, String codeName) {
        this.androidVersionName = androidVersionName;
        this.apiLevel = apiLevel;
        this.codeName = codeName;
    }

    public static SDK_VERSION getSdkVersion(int apiLevel) {
        if (apiLevel >= MIN_API_LEVEL && apiLevel <= MAX_API_LEVEL) {
            return enumValues[MAX_API_LEVEL - apiLevel];
        }
        return SDK_VERSION.UNKNOWN;
    }

    //getters and setters

    public String getAndroidVersionName() {
        return androidVersionName;
    }

    public void setAndroidVersionName(String androidVersionName) {
        this.androidVersionName = androidVersionName;
    }

    public int getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(int apiLevel) {
        this.apiLevel = apiLevel;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }


    @Override
    public String toString() {
        return this.getCodeName();
    }
}