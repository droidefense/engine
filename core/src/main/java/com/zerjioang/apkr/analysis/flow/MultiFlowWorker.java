package com.zerjioang.apkr.analysis.flow;

import apkr.external.modules.controlflow.model.map.BasicCFGFlowMap;
import apkr.external.modules.controlflow.model.nodes.ConditionalNode;
import apkr.external.modules.controlflow.model.nodes.EntryPointNode;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.DalvikVM;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.exceptions.NoMainClassFoundException;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.analysis.dynamicscan.machine.inst.Instruction;
import com.zerjioang.apkr.analysis.dynamicscan.machine.inst.InstructionReturn;
import com.zerjioang.apkr.analysis.dynamicscan.machine.reader.DexClassReader;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;
import com.zerjioang.apkr.temp.ApkrIntelligence;

import java.util.ArrayList;
import java.util.Vector;

public final strictfp class MultiFlowWorker extends AbstractFlowWorker {

    public MultiFlowWorker(final DalvikVM vm, ApkrProject project) {
        super(vm, project);
        flowMap = new BasicCFGFlowMap();
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: MultiFlow");
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
        //save multiflow worker flowmap
        currentProject.setMultiFlowMap(flowMap);
        Log.write(LoggerType.DEBUG, "WORKER: MultiFlow FINISHED!");
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
                //multiflow worker IS AWARE of instruction return value
                if (returnValue != null) {
                    //first check for errors in instruction execution
                    if (returnValue.getError() != null) {
                        throw returnValue.getError();
                    }
                    //first thing after errors is to check if its a conditional node
                    if (returnValue.getNode() != null && returnValue.getNode() instanceof ConditionalNode) {
                        //Duplicate current state
                    }
                    //if no errors, update values
                    frame = returnValue.getFrame();
                    method = returnValue.getMethod();
                    upperCodes = returnValue.getUpperCodes();
                    lowerCodes = returnValue.getLowerCodes();
                    codes = returnValue.getCodes();
                    toNode = returnValue.getNode();
                    //save node connection if instruction returned a node
                    if (fromNode != null && toNode != null) {
                        createNewConnection(fromNode, toNode, currentInstruction);
                        //update current node if instruction is an invoke type
                        if (isInvokeInstruction(currentInstruction))
                            fromNode = toNode;
                    }
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

    private boolean isInvokeInstruction(Instruction currentInstruction) {
        //TODO replace by enumeration contained different instruction types
        switch (currentInstruction.code()) {
            case "0x28":
                //goto
                return true;
            case "0x29":
                //goto
                return true;
            case "0x2A":
                //goto
                return true;
            case "0x6E":
                //goto
                return true;
            case "0x6F":
                //goto
                return true;
            case "0x70":
                //goto
                return true;
            case "0x71":
                //goto
                return true;
            case "0x72":
                //goto
                return true;
            case "0x74":
                //goto
                return true;
            case "0x75":
                //goto
                return true;
            case "0x76":
                //goto
                return true;
            case "0x77":
                //goto
                return true;
            case "0x78":
                //goto
                return true;
            default:
                return false;
        }
    }
}