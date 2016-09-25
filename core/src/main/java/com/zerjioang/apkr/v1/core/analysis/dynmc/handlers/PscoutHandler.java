package com.zerjioang.apkr.v1.core.analysis.dynmc.handlers;

import apkr.external.module.pscout.PSCoutModel;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.nodes.MethodNode;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

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
                        String key = getKey(mn.getMethod());
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

    public String getKey(IAtomMethod method) {
        return method.getOwnerClass().getName() + "." + method.getName() + method.getDescriptor();
    }
}
