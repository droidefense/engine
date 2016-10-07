package com.zerjioang.apkr.analysis.dynamicscan.handlers;

import com.zerjioang.apkr.analysis.handlers.base.AbstractHandler;
import com.zerjioang.apkr.sdk.helpers.DexFileStatistics;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class DexStatsHandler extends AbstractHandler {

    private final ApkrProject currentProject;
    private final ArrayList<ApkrFile> list;

    public DexStatsHandler(ApkrProject currentProject, ArrayList<ApkrFile> list) {
        this.currentProject = currentProject;
        this.list = list;
    }

    @Override
    public boolean doTheJob() {
        //before running controlflow
        //calculate some statistics
        DexFileStatistics statistics = new DexFileStatistics(currentProject, list);
        currentProject.addDexFileStatistics(statistics);
        return true;
    }
}
