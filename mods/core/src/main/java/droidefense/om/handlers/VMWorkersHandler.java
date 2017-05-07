package droidefense.om.handlers;

import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.handler.base.AbstractHandler;
import droidefense.om.flow.BasicControlFlowGraphWorker;
import droidefense.om.flow.OpCodeCheckerWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.exceptions.NoMainClassFoundException;
import droidefense.om.machine.reader.DexHeaderReader;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class VMWorkersHandler extends AbstractHandler {

    private final DroidefenseProject currentProject;
    private final ArrayList<AbstractHashedFile> list;

    public VMWorkersHandler(DroidefenseProject currentProject, ArrayList<AbstractHashedFile> list) {
        super();
        this.currentProject = currentProject;
        this.list = list;
    }

    @Override
    public boolean doTheJob() {
        //create VM
        DalvikVM vm = new DalvikVM(currentProject);

        //create loader
        //for each dex file found, add its content
        boolean readError = false;
        for (AbstractHashedFile dex : list) {
            byte[] data = new byte[0];
            try {
                data = currentProject.getDexData(dex);
                //first use my class loader and check for file integrity,...
                DexHeaderReader loader = new DexHeaderReader(data, currentProject);
                loader.loadClasses(dex);
                ///then use default class loader
                vm.load(dex, data, DalvikVM.MULTIDEX);
            } catch (IOException e) {
                e.printStackTrace();
                readError = true;
            }
        }

        if (!readError) {
            try {
                ArrayList<AbstractDVMThread> worklist = new ArrayList<>();

                //add workes to run
                //opcode analysis
                worklist.add(new OpCodeCheckerWorker(vm, currentProject));
                //reflection solver model generator
                //worklist.add(new ReflectionControlFlowGraphWorker(vm, currentProject));
                //normal model generator
                worklist.add(new BasicControlFlowGraphWorker(vm, currentProject));
                //worklist.add(new FollowCallsControlFlowGraphWorker(vm, currentProject));
                //multiflow machine state
                //worklist.add(new MultiFlowWorker(vm, currentProject));

                //run all controlflow
                for (AbstractDVMThread worker : worklist) {
                    vm.setWorker(worker);
                    vm.run();
                }
                return true;
            } catch (NoMainClassFoundException e) {
                Log.write(LoggerType.FATAL, e.getMessage(), getClass().getName());
            } catch (Exception ex) {
                Log.write(LoggerType.FATAL, ex.getMessage(), getClass().getName());
            } catch (Throwable throwable) {
                Log.write(LoggerType.FATAL, throwable.getMessage(), getClass().getName());
            }
        }
        return false;
    }
}
