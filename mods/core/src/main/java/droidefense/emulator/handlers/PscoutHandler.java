package droidefense.emulator.handlers;

import droidefense.handler.FileIOHandler;
import droidefense.handler.base.AbstractHandler;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.pscout.PSCoutModel;
import droidefense.rulengine.base.AbstractAtomNode;
import droidefense.rulengine.nodes.MethodNode;
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
            PSCoutModel model = (PSCoutModel) FileIOHandler.getResourceObjectStream(environment.RESOURCE_FOLDER + File.separator + environment.PSCOUT_MODEL).readObject();
            if (model == null) {
                return false;
            }
            if (nodelist != null) {
                for (AbstractAtomNode node : nodelist) {
                    if (node instanceof MethodNode) {
                        //get name and args
                        MethodNode mn = (MethodNode) node;
                        boolean filterByScopeEnabled = false;
                        if (
                                (mn.isOnscope() && filterByScopeEnabled) ||
                                        !filterByScopeEnabled) {
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
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
        }
        return false;
    }
}
