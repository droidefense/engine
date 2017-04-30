package droidefense.om.handlers;

import droidefense.handler.base.AbstractHandler;
import droidefense.om.helper.DexFileStatistics;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class DexStatsHandler extends AbstractHandler {

    private final DroidefenseProject currentProject;
    private final ArrayList<AbstractHashedFile> list;

    public DexStatsHandler(DroidefenseProject currentProject, ArrayList<AbstractHashedFile> list) {
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
