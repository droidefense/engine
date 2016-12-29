package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.base.AbstractHandler;
import droidefense.om.handlers.DexStatsHandler;
import droidefense.om.handlers.VMWorkersHandler;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.util.ArrayList;

/**
 * Created by sergio on 25/3/16.
 */
public final class AndroidDynamicAnalysis extends AbstractAndroidAnalysis {

    @Override
    protected boolean analyze() {

        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense dynamic analysis ---\n\n");

        //dex file list
        ArrayList<AbstractHashedFile> list = currentProject.getDexList();

        //run dex file statistics handler
        AbstractHandler handler = new DexStatsHandler(currentProject, list);
        executionSuccessful &= handler.doTheJob();

        //execute selected controlflow
        handler = new VMWorkersHandler(currentProject, list);
        executionSuccessful &= handler.doTheJob();
        this.currentProject.setDynamicAnalysisDone(true);
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "Android dynamic analysis";
    }
}
