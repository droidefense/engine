package com.zerjioang.apkr.v1.core.workers;

import com.zerjioang.apkr.temp.ApkrIntelligence;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DalvikVM;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.exceptions.NoMainClassFoundException;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.InstructionReturn;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.reader.DexClassReader;
import com.zerjioang.apkr.v1.core.cfg.NodeConnection;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.BasicCFGFlowMap;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;
import com.zerjioang.apkr.v1.core.cfg.nodes.EntryPointNode;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.util.ArrayList;
import java.util.Vector;

public final strictfp class RealFlowWorker extends AbstractDVMThread {

    private static AbstractFlowMap flowMap;
    private AbstractAtomNode fromNode;
    private AbstractAtomNode toNode;

    public RealFlowWorker(ApkrProject project) {
        super(new DalvikVM(project), project);
        flowMap = new BasicCFGFlowMap();
    }

    public RealFlowWorker(final DalvikVM vm, ApkrProject project) {
        super(vm, project);
        flowMap = new BasicCFGFlowMap();
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: RealFlowWorker");
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
    }

    @Override
    public void run() throws Throwable {
        //get main class and load
        if (currentProject.hasMainClass()) {
            DexClassReader.getInstance().load(currentProject.getMainClassName());
            execute(true);
        } else {
            throw new NoMainClassFoundException(currentProject.getProjectName() + " >> check main class manually");
        }
    }

    @Override
    public void finish() {
        Log.write(LoggerType.DEBUG, "WORKER: RealFlowWorker FINISHED!");
    }

    @Override
    public IAtomMethod[] getInitialMethodToRun(IAtomClass clazz) {
        ArrayList<IAtomMethod> list = new ArrayList<>();
        /*IAtomMethod[] l0 = clazz.getMethod("<init>");
        for (IAtomMethod m : l0) {
            list.add(m);
        }*/
        list.add(clazz.getMethod("onCreate", "(Landroid/os/Bundle;)V", true));
        return list.toArray(new IAtomMethod[list.size()]);
    }

    @Override
    public int getInitialArgumentCount(IAtomClass cls, IAtomMethod m) {
        return 0;
    }

    @Override
    public Object getInitialArguments(IAtomClass cls, IAtomMethod m) {
        return null;
    }

    @Override
    public IAtomClass[] getInitialDVMClass() {
        //get all
        if (currentProject.hasMainClass())
            return new IAtomClass[]{DexClassReader.getInstance().load(currentProject.getMainClassName())};
        else {
            //else, return all reveivers, services,...
            IAtomClass[] alllist = DexClassReader.getInstance().getAllClasses();
            ArrayList<IAtomClass> developerClasses = new ArrayList<>();
            for (IAtomClass cls : alllist) {
                if (ApkrIntelligence.getInstance().isDeveloperClass(cls.getName()))
                    developerClasses.add(cls);
            }
            return developerClasses.toArray(new IAtomClass[developerClasses.size()]);
        }
    }

    @Override
    public AbstractDVMThread reset() {
        //reset 'thread' status
        this.setStatus(STATUS_NOT_STARTED);
        this.removeFrames();
        this.timestamp = new AtomTimeStamp();
        return this;
    }

    @Override
    public strictfp void execute(boolean endless) throws Throwable {

        IAtomFrame frame = getCurrentFrame();
        IAtomMethod method = frame.getMethod();

        int[] lowerCodes = method.getOpcodes();
        int[] upperCodes = method.getRegistercodes();
        int[] codes = method.getIndex();

        fromNode = EntryPointNode.builder();

        while (endless) {
            try {
                //1 ask if we have more inst to execute
                if (frame.getPc() >= lowerCodes.length || getFrames() == null || getFrames().isEmpty())
                    break;
                //skip sdk classes for faster execution
                if (method.isFake()) {
                    popFrame();
                    frame = getCurrentFrame();
                    if (frame != null) {
                        method = frame.getMethod();
                        if (method != null) {
                            upperCodes = method.getRegistercodes();
                            lowerCodes = method.getOpcodes();
                            codes = method.getIndex();
                            continue;
                        }
                    }
                    break;
                }
                int instVal = lowerCodes[frame.getPc()];
                System.out.println("Instruction: 0x" + Integer.toHexString(instVal));
                Instruction currentInstruction = instructions[instVal];
                InstructionReturn returnValue = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, Instruction.CFG_EXECUTION);
                if (returnValue != null) {
                    //first check for errors in instruction execution
                    if (returnValue.getError() != null) {
                        throw returnValue.getError();
                    }
                    //if no errors, update values
                    frame = returnValue.getFrame();
                    method = returnValue.getMethod();
                    upperCodes = returnValue.getUpperCodes();
                    lowerCodes = returnValue.getLowerCodes();
                    codes = returnValue.getCodes();
                    toNode = returnValue.getNode();
                    //save node connection
                    createNewConnection(fromNode, toNode, currentInstruction);
                }
            } catch (Throwable e) {
                frame = handleThrowable(e, frame);
                method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegistercodes();
                codes = method.getIndex();
            }
        }
    }

    private void createNewConnection(AbstractAtomNode from, AbstractAtomNode to, Instruction currentInstruction) {
        from = flowMap.addNode(from);
        to = flowMap.addNode(to);
        //avoid connections with itself
        if (true || !from.getConnectionLabel().equals(to.getConnectionLabel())) {
            NodeConnection conn = new NodeConnection(from, to, currentInstruction);
            flowMap.addConnection(conn);
        }
        //add this new connection to flowmap
    }
}