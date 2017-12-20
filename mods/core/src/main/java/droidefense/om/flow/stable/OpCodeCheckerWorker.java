package droidefense.om.flow.stable;

import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.om.flow.base.AbstractFlowWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.util.ArrayList;

public final strictfp class OpCodeCheckerWorker extends AbstractFlowWorker {

    private static final int[] codeCount = new int[instructions.length];
    private static int total = 0;

    public OpCodeCheckerWorker(DroidefenseProject project) {
        super(project.getDalvikMachine(), project);
    }

    public OpCodeCheckerWorker(final DalvikVM vm, DroidefenseProject project) {
        super(vm, project);
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: OpCodeCheckerWorker");
        /*
        seems to be not needed

        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
        */
        this.timestamp.start();
    }

    @Override
    public void run() throws Throwable {
    }

    @Override
    public void finish() {
        Log.write(LoggerType.DEBUG, "WORKER: OpCodeCheckerWorker FINISHED!");
        currentProject.setInstructionCount(total);
        currentProject.setOpCodesCount(codeCount);
        this.timestamp.stop();
        Log.write(LoggerType.TRACE, "OpCodeCheckerWorker execution time:\t" + this.timestamp.getFormattedDuration() + " ( " + this.timestamp.getDuration() + " ms )");
    }

    @Override
    public int getInitialArgumentCount(IDroidefenseClass cls, IDroidefenseMethod m) {
        return 0;
    }

    @Override
    public Object getInitialArguments(IDroidefenseClass cls, IDroidefenseMethod m) {
        return null;
    }

    @Override
    public IDroidefenseClass[] getInitialDVMClass() {
        //only return developer class and skip known java jdk and android sdk classes
        IDroidefenseClass[] alllist = currentProject.getInternalInfo().getAllClasses();
        ArrayList<IDroidefenseClass> developerClasses = new ArrayList<>();
        for (IDroidefenseClass cls : alllist) {
            if (environment.isDeveloperClass(cls) && cls.isDeveloperClass())
                developerClasses.add(cls);
        }
        return developerClasses.toArray(new IDroidefenseClass[developerClasses.size()]);
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

    @Override
    public strictfp void execute(boolean endless) throws Throwable {

        IAtomFrame frame = getCurrentFrame();
        if(frame!=null){
            IDroidefenseMethod method = frame.getMethod();

            if (method!=null){
                for (int idx : method.getOpcodes()) {
                    codeCount[idx]++;
                    total++;
                }
            }
            else{
                //its been some error while loading this frame methods
            }
        }
        else{
            //is been some error while loading and frame is null
        }
    }
}