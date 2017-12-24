package droidefense.emulator.handlers;

import droidefense.emulator.machine.reader.DexHeaderReader;
import droidefense.handler.base.AbstractHandler;
import droidefense.emulator.helper.DexFileStatistics;
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
