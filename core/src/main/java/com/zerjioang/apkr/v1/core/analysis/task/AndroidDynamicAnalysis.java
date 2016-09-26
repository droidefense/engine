package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.base.AbstractHandler;
import com.zerjioang.apkr.v1.core.analysis.dynmc.handlers.DexStatsHandler;
import com.zerjioang.apkr.v1.core.analysis.dynmc.handlers.PscoutHandler;
import com.zerjioang.apkr.v1.core.analysis.dynmc.handlers.VMWorkersHandler;
import com.zerjioang.apkr.v2.helpers.enums.ProcessStatus;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.util.ArrayList;

/**
 * Created by sergio on 25/3/16.
 */
public class AndroidDynamicAnalysis extends AbstractAndroidAnalysis {

    public AndroidDynamicAnalysis() {
        this.positiveMatch = false;
        this.status = ProcessStatus.STARTED;
    }

    @Override
    protected boolean analyze() {

        Log.write(LoggerType.TRACE, "\n\n --- Running apkr dynamic analysis ---\n\n");

        //dex file list
        ArrayList<ResourceFile> list = currentProject.getDexList();

        //run dex file statistics handler
        AbstractHandler handler = new DexStatsHandler(currentProject, list);
        handler.doTheJob();

        //execute selected workers
        handler = new VMWorkersHandler(currentProject, list);
        handler.doTheJob();

        //run pscout apimodel
        handler = new PscoutHandler(currentProject, currentProject.getNormalControlFlowMap().getNodeList());
        handler.doTheJob();

        //stop timer
        stop();
        positiveMatch = !hasErrors();
        return hasErrors();
    }

    @Override
    public String getName() {
        return "Android dynamic analysis";
    }
}
