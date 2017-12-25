package droidefense.emulator.flow.stable;

import droidefense.emulator.machine.base.AbstractDVMThread;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.emulator.machine.base.DalvikVM;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseClass;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.emulator.flow.base.AbstractFlowWorker;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.util.ExecutionTimer;

public final strictfp class OpCodeCheckerWorker extends SimpleFlowWorker {

    private static final int[] codeCount = new int[instructions.length];
    private static int total = 0;

    public OpCodeCheckerWorker(DroidefenseProject project) {
        super(project);
        this.name="OpCodeCheckerWorker";
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
    public strictfp void execute(boolean endless) throws Throwable {

        IDroidefenseFrame frame = getCurrentFrame();
        if (frame != null) {
            IDroidefenseMethod method = frame.getMethod();

            if (method != null) {
                for (int idx : method.getOpcodes()) {
                    codeCount[idx]++;
                    total++;
                }
            } else {
                //its been some error while loading this frame methods
            }
        } else {
            //is been some error while loading and frame is null
        }
    }
}