package droidefense.om.handlers;

import droidefense.om.flow.stable.OpCodeCheckerWorker;
import droidefense.om.flow.stable.ReferencesResolverWorker;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.handler.base.AbstractHandler;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
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
        DalvikVM vm = currentProject.getDalvikMachine();

        boolean successReading = currentProject.isDexFileReaded();

        if (successReading) {
            try {
                ArrayList<AbstractDVMThread> worklist = new ArrayList<>();

                //add workes to run
                //opcode analysis
                worklist.add(new OpCodeCheckerWorker(currentProject));

                //R references resolver analysis
                worklist.add(new ReferencesResolverWorker(currentProject));

                //normal model generator
                //worklist.add(new BasicControlFlowGraphWorker(currentProject));

                //follow model generator
                //worklist.add(new FollowCallsControlFlowGraphWorker(currentProject));

                //reflection solver model generator
                //worklist.add(new ReflectionControlFlowGraphWorker(currentProject));

                //multiflow machine state
                //worklist.add(new MultiFlowWorker(currentProject));

                //TODO replace all flow workers by MULTIFLOW + REFLECTION type worker

                //run all controlflow
                for (AbstractDVMThread worker : worklist) {
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
