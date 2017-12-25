package droidefense.emulator.flow.stable;

import droidefense.emulator.flow.base.AbstractFlowWorker;
import droidefense.emulator.machine.base.AbstractDVMThread;
import droidefense.emulator.machine.base.DalvikVM;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseClass;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.util.ExecutionTimer;

public abstract class SimpleFlowWorker extends AbstractFlowWorker {

    public SimpleFlowWorker(DroidefenseProject currentProject) {
        super(currentProject.getDalvikMachine(), currentProject);
        this.name="SimpleFlowWorker";
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: "+getName());

        //TODO check if this can be removed with no side effects
        /*
        seems to be not needed

        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
        */
        this.timestamp.start();
    }

    @Override
    public void run() {
        try {
            execute(false);
        } catch (Throwable throwable) {
            Log.write(LoggerType.ERROR, throwable.getLocalizedMessage());
        }
    }

    @Override
    public void finish() {

    }

    @Override
    public int getInitialArgumentCount(IDroidefenseClass cls, IDroidefenseMethod m) {
        return DO_NOT_USE_ARGUMENTS_COUNT; //do not use arguments
    }

    @Override
    public Object getInitialArguments(IDroidefenseClass cls, IDroidefenseMethod m) {
        return DO_NOT_USE_ARGUMENTS; //do not use arguments
    }

    @Override
    public IDroidefenseClass[] getInitialDVMClass() {
        //only return developer class and skip known java jdk and android sdk classes
        return currentProject.getDeveloperClasses();
    }

    @Override
    public IDroidefenseMethod[] getInitialMethodToRun(IDroidefenseClass dexClass) {
        return dexClass.getAllMethods();
    }

    @Override
    public AbstractDVMThread cleanThreadContext() {
        //cleanThreadContext 'thread' status
        this.setStatus(STATUS_NOT_STARTED);
        this.removeFrames();
        this.timestamp = new ExecutionTimer();
        return this;
    }
}
