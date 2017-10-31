package external.plugins.collection.sttc;

import droidefense.vfs.model.impl.VirtualFile;
import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.helpers.Util;

import java.util.ArrayList;

/**
 * Created by r00t on 07/12/2015.
 */
public class ELFDetectorPlugin extends AbstractStaticPlugin {

    public static final int[] ELF_SIGNATURE = new int[]{0x7f, 0x45, 0x4c, 0x46};
    private ArrayList<VirtualFile> hidden;

    public ELFDetectorPlugin() {
        positiveMatch = false;
    }

    @Override
    public void onPreExecute() {
        hidden = new ArrayList<>();
    }

    @Override
    public void onExecute() {
        ArrayList<VirtualFile> files = currentProject.getAppFiles();
        for (VirtualFile res : files) {
            byte[] content = res.getContent();
            if (Util.checkHexSignature(content, ELF_SIGNATURE, false)) {
                hidden.add(res);
            }
        }
        log("ELFDetectorPlugin plugin FINISHED", 1);
        log("ELFDetectorPlugin DETECTED " + hidden.size() + " hidden (plain-text) ELF files", 1);
        for (VirtualFile res : hidden) {
            log("\t\t > " + res.getName(), 2);
        }
        this.positiveMatch = hidden.size() > 0;
    }

    @Override
    protected void postExecute() {
    }

    @Override
    protected String getPluginName() {
        return "Plain ELF file detector";
    }
}