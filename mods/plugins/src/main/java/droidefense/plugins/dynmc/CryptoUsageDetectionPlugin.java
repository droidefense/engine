package droidefense.plugins.dynmc;

import droidefense.emulator.machine.base.struct.model.SharedPool;
import droidefense.sdk.AbstractDynamicPlugin;

import java.util.ArrayList;

/**
 * Created by sergio on 31/5/16.
 */
public class CryptoUsageDetectionPlugin extends AbstractDynamicPlugin {

    private float found;

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
            found += name.startsWith(keys[0]) ? 1 : 0;
            if (!matchingClassNames.contains(name))
                matchingClassNames.add(name);
        }
        log("Found: " + found, 1);
        positiveMatch = found >= 1;
    }

    @Override
    protected void postExecute() {
    }

    @Override
    public String getPluginName() {
        return "Crypto API Usage Detection Plugin";
    }
}
