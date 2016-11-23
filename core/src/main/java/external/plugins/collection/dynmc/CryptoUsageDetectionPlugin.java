package external.plugins.collection.dynmc;

import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.sdk.AbstractDynamicPlugin;

import java.util.ArrayList;

/**
 * Created by sergio on 31/5/16.
 */
public class CryptoUsageDetectionPlugin extends AbstractDynamicPlugin {

    private float founded;

    @Override
    public void onPreExecute() {
    }

    //https://developer.android.com/reference/javax/crypto/package-summary.html?hl=es

    @Override
    public void onExecute() {
        String[] keys = {
                "javax/crypto/",
        };
        float total = keys.length;
        SharedPool pool = SharedPool.getInstance();

        log("Looking for java crypto API usage descriptors...", 1);

        //classes used in whole .dex file

        String[] mclasses = pool.getMethodClasses();
        ArrayList<String> matchingClassNames = new ArrayList<>();

        log("Searching descriptor: " + keys[0], 2);
        for (String name : mclasses) {
            founded += name.startsWith(keys[0]) ? 1 : 0;
            if (!matchingClassNames.contains(name))
                matchingClassNames.add(name);
        }
        log("Found: " + founded, 1);
        positiveMatch = founded >= 1;
    }

    @Override
    protected void postExecute() {
        if (positiveMatch) {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-red\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">Java Crypto API usage detected (" + founded + ")</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        } else {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-green\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">No Java Crypto API usage detected</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        }
    }

    @Override
    protected String getPluginName() {
        return "Crypto API Usage Detection Plugin";
    }
}
