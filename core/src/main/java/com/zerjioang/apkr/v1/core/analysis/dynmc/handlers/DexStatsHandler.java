package com.zerjioang.apkr.v1.core.analysis.dynmc.handlers;

import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.core.cfg.DexFileStatistics;

import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class DexStatsHandler extends AbstractHandler {

    private final ApkrProject currentProject;
    private final ArrayList<ResourceFile> list;

    public DexStatsHandler(ApkrProject currentProject, ArrayList<ResourceFile> list) {
        this.currentProject = currentProject;
        this.list = list;
    }

    @Override
    public boolean doTheJob() {
        //before running workers
        //calculate some statistics
        DexFileStatistics statistics = new DexFileStatistics(currentProject, list);
        currentProject.addDexFileStatistics(statistics);
        return true;
    }
}
