package com.zerjioang.apkr.analysis.dynamicscan.handlers;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.DalvikVM;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.exceptions.NoMainClassFoundException;
import com.zerjioang.apkr.analysis.dynamicscan.machine.reader.DexHeaderReader;
import com.zerjioang.apkr.analysis.flow.BasicControlFlowGraphWorker;
import com.zerjioang.apkr.analysis.flow.OpCodeCheckerWorker;
import com.zerjioang.apkr.analysis.handlers.base.AbstractHandler;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.util.ArrayList;

/**
 * Created by sergio on 12/6/16.
 */
public class VMWorkersHandler extends AbstractHandler {

    private final ApkrProject currentProject;
    private final ArrayList<ApkrFile> list;

    public VMWorkersHandler(ApkrProject currentProject, ArrayList<ApkrFile> list) {
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
        for (ApkrFile dex : list) {
            byte[] data = currentProject.getDexData(dex);
            //first use my class loader and check for file integrity,...
            DexHeaderReader loader = new DexHeaderReader(data, currentProject);
            loader.loadClasses(dex);
            ///then use default class loader
            vm.load(dex, data, DalvikVM.MULTIDEX);
        }

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
        return false;
    }
}
