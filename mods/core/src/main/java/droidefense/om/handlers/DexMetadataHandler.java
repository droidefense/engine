package droidefense.om.handlers;

import droidefense.handler.base.AbstractHandler;
import droidefense.om.helper.DexFileStatistics;
import droidefense.om.machine.reader.DexHeaderReader;
import droidefense.sdk.model.base.DroidefenseProject;

/**
 * Created by sergio on 12/6/16.
 */
public class DexMetadataHandler extends AbstractHandler {

    private transient final DroidefenseProject currentProject;

    public DexMetadataHandler(DroidefenseProject currentProject) {
        this.currentProject = currentProject;
    }

    @Override
    public boolean doTheJob() {
        //for each dex file, read header
        DexHeaderReader loader = new DexHeaderReader(currentProject);
        loader.readAllDexAvailable();

        //for each dex file, calculate some statistics
        DexFileStatistics statistics = new DexFileStatistics(currentProject);
        currentProject.addDexFileStatistics(statistics);

        return true;
    }
}
