package droidefense.analysis;

import apkr.external.modules.helpers.enums.ProcessStatus;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.util.ArrayList;

/**
 * Created by sergio on 25/3/16.
 */
public final class AndroidDynamicAnalysis extends AbstractAndroidAnalysis {

    public AndroidDynamicAnalysis() {
        this.positiveMatch = false;
        this.status = ProcessStatus.STARTED;
    }

    @Override
    protected boolean analyze() {

        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense dynamic analysis ---\n\n");

        //dex file list
        ArrayList<AbstractHashedFile> list = currentProject.getDexList();

        //run dex file statistics handler
        //AbstractHandler handler = new DexStatsHandler(currentProject, list);
        //handler.doTheJob();

        //execute selected controlflow
        //handler = new VMWorkersHandler(currentProject, list);
        //handler.doTheJob();

        //run pscout apimodel
        //handler = new PscoutHandler(currentProject, currentProject.getNormalControlFlowMap().getNodeList());
        //handler.doTheJob();

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
