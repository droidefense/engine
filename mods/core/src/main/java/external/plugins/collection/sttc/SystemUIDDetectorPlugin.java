package external.plugins.collection.sttc;

import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.manifest.Manifest;


/**
 * Created by sergio on 1/2/16.
 */
public class SystemUIDDetectorPlugin extends AbstractStaticPlugin {

    public static final String APP_NOT_SYSTEM_UI = "This application is NOT configured as System UI application.";
    public static final String APP_SYSTEM_UI = "This application is configured as System UI application. This means that the key used for signing this app is the same key used for signing your Android system apps.\nIf this is not a trusted app such us Chrome, Gmail,..., please, report us and we will help you.";
    private static final String SYSTEMAPP = "android:sharedUserId=\"android.uid.system\"";
    private String sharedId;

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute() {
        Manifest manif = currentProject.getManifestInfo();
        if (manif != null) {
            sharedId = currentProject.getManifestInfo().getSharedUserId();
            positiveMatch = sharedId != null && sharedId.length() > 0;
        }
        log("SystemUIDDetectorPlugin plugin finished", 1);
    }

    @Override
    protected void postExecute() {
    }

    @Override
    protected String getPluginName() {
        return "System UID assignment detector";
    }
}
