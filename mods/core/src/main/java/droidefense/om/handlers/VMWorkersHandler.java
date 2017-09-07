package droidefense.om.handlers;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.handler.base.AbstractHandler;
import droidefense.om.flow.OpCodeCheckerWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.reader.DexHeaderReader;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.IOException;
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

        boolean readError = currentProject.isDexFileReaded();

        if (!readError) {
            try {
                ArrayList<AbstractDVMThread> worklist = new ArrayList<>();

                //add workes to run
                //opcode analysis
                worklist.add(new OpCodeCheckerWorker(vm, currentProject));
                //reflection solver model generator
                //worklist.add(new ReflectionControlFlowGraphWorker(vm, currentProject));
                //normal model generator
                //worklist.add(new BasicControlFlowGraphWorker(vm, currentProject));
                //worklist.add(new FollowCallsControlFlowGraphWorker(vm, currentProject));
                //multiflow machine state
                //worklist.add(new MultiFlowWorker(vm, currentProject));

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
