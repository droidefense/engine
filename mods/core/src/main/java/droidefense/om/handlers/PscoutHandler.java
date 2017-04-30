package droidefense.om.handlers;

import apkr.external.module.pscout.PSCoutModel;
import com.droidefense.base.AbstractAtomNode;
import com.droidefense.nodes.MethodNode;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.handler.FileIOHandler;
import droidefense.handler.base.AbstractHandler;
import droidefense.sdk.helpers.DroidDefenseParams;
import droidefense.sdk.model.base.DroidefenseProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class PscoutHandler extends AbstractHandler {

    private final DroidefenseProject currentProject;
    private ArrayList<AbstractAtomNode> nodelist;

    public PscoutHandler(DroidefenseProject currentProject, ArrayList<AbstractAtomNode> nodelist) {
        this.currentProject = currentProject;
        this.nodelist = nodelist;
    }


    @Override
    public boolean doTheJob() {
        Log.write(LoggerType.TRACE, "---- Mapping method calls with permissions -----");
        try {
            PSCoutModel model = (PSCoutModel) FileIOHandler.getResourceObjectStream(DroidDefenseParams.getInstance().RESOURCE_FOLDER + File.separator + DroidDefenseParams.getInstance().PSCOUT_MODEL).readObject();
            if (model == null) {
                return false;
            }
            if (nodelist != null) {
                for (AbstractAtomNode node : nodelist) {
                    if (node instanceof MethodNode) {
                        //get name and args
                        MethodNode mn = (MethodNode) node;
                        if(mn.isOnscope()){
                            String key = mn.getKey();
                            String permissionName = model.getCallPermissions(key);
                            if (permissionName != null) {
                                System.out.println(permissionName + "\t" + key);
                            }
                        }
                    }
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
