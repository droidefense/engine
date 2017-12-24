package droidefense.sdk.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by .local on 14/10/2016.
 */
public enum JsonStyle {

    JSON_BEAUTY {
        @Override
        public Gson getJsonBuilder() {
            return jsonBuilderBeauty;
        }
    }, JSON_COMPRESSED {
        @Override
        public Gson getJsonBuilder() {
            return jsonBuilderCompressed;
        }
    };

    private static Gson jsonBuilderCompressed = new GsonBuilder().serializeSpecialFloatingPointValues().create();
    private static Gson jsonBuilderBeauty = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create();

    public abstract Gson getJsonBuilder();
}
