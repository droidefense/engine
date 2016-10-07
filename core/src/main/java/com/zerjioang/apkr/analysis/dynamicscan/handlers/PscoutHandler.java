package com.zerjioang.apkr.analysis.dynamicscan.handlers;

import apkr.external.module.pscout.PSCoutModel;
import apkr.external.modules.controlflow.model.base.AbstractAtomNode;
import apkr.external.modules.controlflow.model.nodes.MethodNode;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.handlers.FileIOHandler;
import com.zerjioang.apkr.analysis.handlers.base.AbstractHandler;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class PscoutHandler extends AbstractHandler {

    private final ApkrProject currentProject;
    private ArrayList<AbstractAtomNode> nodelist;

    public PscoutHandler(ApkrProject currentProject, ArrayList<AbstractAtomNode> nodelist) {
        this.currentProject = currentProject;
        this.nodelist = nodelist;
    }


    @Override
    public boolean doTheJob() {
        Log.write(LoggerType.TRACE, "---- Mapping method calls with permissions -----");
        try {
            PSCoutModel model = (PSCoutModel) FileIOHandler.getResourceObjectStream(ApkrConstants.INTERNAL_DATA_FOLDER + File.separator + ApkrConstants.PSCOUT_MODEL).readObject();
            if (model == null) {
                return false;
            }
            if (nodelist != null) {
                for (AbstractAtomNode node : nodelist) {
                    if (node instanceof MethodNode) {
                        //get name and args
                        MethodNode mn = (MethodNode) node;
                        String key = mn.getKey();
                        String permissionName = model.getCallPermissions(key);
                        if (permissionName != null) {
                            System.out.println(permissionName + "\t" + key);
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
