package external.plugins.collection.sttc;

import droidefense.sdk.AbstractStaticPlugin;

/**
 * Created by r00t on 07/12/2015.
 */
public class ManifestCheckerPlugin extends AbstractStaticPlugin {

    public static final String VERSION_01 = "";

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute() {
    }

    @Override
    protected void postExecute() {
        if (positiveMatch) {
            this.html = "";
        } else {
            this.html = "";
        }
    }

    @Override
    protected String getPluginName() {
        return "Manifest.MF creator detector";
    }
}