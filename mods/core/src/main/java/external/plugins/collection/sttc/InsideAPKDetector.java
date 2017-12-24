package external.plugins.collection.sttc;

import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.helpers.Util;
import droidefense.vfs.model.impl.VirtualFile;

import java.util.ArrayList;

/**
 * Created by r00t on 07/12/2015.
 */
public class InsideAPKDetector extends AbstractStaticPlugin {

    private static final int[] APK_SIGNATURE_1 = new int[]{0x50, 0x4b, 0x03, 0x04, 0x14, 0x00, 0x08, 0x08, 0x08, 0x00};
    private static final int[] APK_SIGNATURE_2 = new int[]{0x50, 0x4b, 0x03, 0x04, 0x14, 0x00, 0x00, 0x00, 0x08, 0x00};
    private ArrayList<VirtualFile> apklist;

    public InsideAPKDetector() {
        positiveMatch = false;
    }

    @Override
    public void onPreExecute() {
        apklist = new ArrayList<>();
    }

    @Override
    public void onExecute() {
        ArrayList<VirtualFile> files = currentProject.getAppFiles();
        for (VirtualFile res : files) {
            byte[] content = res.getContent();
            if (
                    Util.checkHexSignature(content, APK_SIGNATURE_1, false)
                            || Util.checkHexSignature(content, APK_SIGNATURE_2, false)
                    ) {
                apklist.add(res);
            }
        }
    }

    @Override
    protected void postExecute() {
        log(getPluginName() + " plugin FINISHED", 1);
        log(getPluginName() + " DETECTED " + apklist.size() + " .apk files", 1);
        for (VirtualFile res : apklist) {
            log("\t\t > " + res.getName(), 2);
        }
        this.positiveMatch = apklist.size() > 0;
    }

    @Override
    protected String getPluginName() {
        return "Inside APK detector";
    }
}