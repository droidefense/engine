package droidefense.emulator.handlers;

import droidefense.emulator.flow.experimental.FollowCallsControlFlowGraphWorker;
import droidefense.emulator.flow.stable.OpCodeCheckerWorker;
import droidefense.emulator.machine.base.AbstractDVMThread;
import droidefense.emulator.machine.base.DalvikVM;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.handler.base.AbstractHandler;
import droidefense.emulator.flow.experimental.BasicControlFlowGraphWorker;
import droidefense.emulator.flow.stable.ReferencesResolverWorker;
import droidefense.sdk.model.base.DroidefenseProject;

import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class VMWorkersHandler extends AbstractHandler {

    private transient final DroidefenseProject currentProject;

    public VMWorkersHandler(DroidefenseProject currentProject) {
        super();
        this.currentProject = currentProject;
    }

    @Override
    public boolean doTheJob() {
        //create VM
        Log.write(LoggerType.TRACE, "Preparing observation machine to multiple analysis...");
        DalvikVM vm = currentProject.getDalvikMachine();
        boolean successReading = currentProject.isDexFileReaded();
        if (successReading) {
            try {
                ArrayList<AbstractDVMThread> worklist = new ArrayList<>();
                //add worker to run on dynamic analysis phase

                //opcode analysis
                Log.write(LoggerType.TRACE, "Adding Opcode analysis routine...");
                worklist.add(new OpCodeCheckerWorker(currentProject));

                //R references resolver analysis
                Log.write(LoggerType.TRACE, "Adding R references resolver analysis routine...");
                worklist.add(new ReferencesResolverWorker(currentProject));

                //normal model reporting
                Log.write(LoggerType.TRACE, "Adding Basic Control Flow Graph analysis routine...");
                worklist.add(new BasicControlFlowGraphWorker(currentProject));

                //follow model reporting
                worklist.add(new FollowCallsControlFlowGraphWorker(currentProject));

                //reflection solver model reporting
                //worklist.add(new ReflectionControlFlowGraphWorker(currentProject));

                //multiflow machine state
                //worklist.add(new MultiFlowWorker(currentProject));

                //TODO replace all flow workers by MULTIFLOW + REFLECTION type worker

                //run all selected workers
                for (AbstractDVMThread worker : worklist) {
                    Log.write(LoggerType.TRACE, "Running worker: "+worker.getName());
                    vm.setWorker(worker);
                    vm.run();
                }
                return true;
            } catch (Throwable throwable) {
                Log.write(LoggerType.FATAL, throwable.getMessage(), getClass().getName());
            }
        }
        return false;
    }
}
