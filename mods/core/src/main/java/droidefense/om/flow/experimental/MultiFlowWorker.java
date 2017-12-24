package droidefense.om.flow.experimental;

import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import com.droidefense.rulengine.map.BasicCFGFlowMap;
import com.droidefense.rulengine.nodes.ConditionalNode;
import com.droidefense.rulengine.nodes.EntryPointNode;
import droidefense.om.flow.base.AbstractFlowWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.exceptions.NoMainClassFoundException;
import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.om.machine.inst.InstructionReturn;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.util.ArrayList;
import java.util.Vector;

public final strictfp class MultiFlowWorker extends AbstractFlowWorker {

    public MultiFlowWorker(DroidefenseProject project) {
        super(project.getDalvikMachine(), project);
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
    public IDroidefenseMethod[] getInitialMethodToRun(IDroidefenseClass clazz) {
        ArrayList<IDroidefenseMethod> list = new ArrayList<>();
        /*IDroidefenseMethod[] l0 = clazz.getMethod("<init>");
        for (IDroidefenseMethod m : l0) {
            list.add(m);
        }*/
        list.add(clazz.getMethod("onCreate", "(Landroid/os/Bundle;)V", true));
        return list.toArray(new IDroidefenseMethod[list.size()]);
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
        //get all
        if (currentProject.hasMainClass())
            return new IDroidefenseClass[]{currentProject.getInternalInfo().getDexClass(currentProject.getMainClassName())};
        else {
            //else, return all reveivers, services,...
            return currentProject.getDeveloperClasses();
        }
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

        IDroidefenseFrame frame = getCurrentFrame();
        IDroidefenseMethod method = frame.getMethod();

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
                System.out.println("DalvikInstruction: 0x" + Integer.toHexString(instVal));
                DalvikInstruction currentInstruction = instructions[instVal];
                InstructionReturn returnValue = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                //multiflow worker IS AWARE of DalvikInstruction return value
                if (returnValue != null) {
                    //first check for errors in DalvikInstruction execution
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
                    //save node connection if DalvikInstruction returned a node
                    if (fromNode != null && toNode != null) {
                        createNewConnection(fromNode, toNode, currentInstruction);
                        //update current node if DalvikInstruction is an invoke type
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

    private boolean isInvokeInstruction(DalvikInstruction currentInstruction) {
        //TODO replace by enumeration contained different DalvikInstruction types
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