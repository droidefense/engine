package droidefense.analysis;

import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.emulator.handlers.DexMetadataHandler;
import droidefense.emulator.handlers.VMWorkersHandler;
import droidefense.handler.base.AbstractHandler;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;

/**
 * Created by sergio on 25/3/16.
 */
public final class AndroidDynamicAnalysis extends AbstractAndroidAnalysis {

    @Override
    protected boolean analyze() {

        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense dynamic analysis ---\n\n");

        //run dex file statistics handler
        AbstractHandler handler = new DexMetadataHandler(currentProject);
        executionSuccessful &= handler.doTheJob();

        //execute selected controlflow
        handler = new VMWorkersHandler(currentProject);
        executionSuccessful &= handler.doTheJob();
        this.currentProject.setDynamicAnalysisDone(true);
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "Android dynamic analysis";
    }
}
