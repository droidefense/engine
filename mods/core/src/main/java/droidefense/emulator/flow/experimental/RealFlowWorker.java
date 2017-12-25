package droidefense.emulator.flow.experimental;

import droidefense.emulator.flow.base.AbstractFlowWorker;
import droidefense.emulator.flow.stable.SimpleFlowWorker;
import droidefense.emulator.machine.base.AbstractDVMThread;
import droidefense.emulator.machine.base.DalvikVM;
import droidefense.emulator.machine.base.exceptions.NoMainClassFoundException;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseClass;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.emulator.machine.inst.InstructionReturn;
import droidefense.emulator.machine.reader.DexClassReader;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.rulengine.map.BasicCFGFlowMap;
import droidefense.rulengine.nodes.EntryPointNode;
import droidefense.emulator.machine.inst.DalvikInstruction;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.util.ExecutionTimer;

import java.util.ArrayList;
import java.util.Vector;

public final strictfp class RealFlowWorker extends SimpleFlowWorker {

    public RealFlowWorker(DroidefenseProject project) {
        super(project);
        flowMap = new BasicCFGFlowMap();
        this.name="RealFlowWorker";
    }

    @Override
    public void run() {
        //get main class and load
        if (currentProject.hasMainClass()) {
            DexClassReader.getInstance().load(currentProject.getMainClassName());
            try {
                execute(true);
            } catch (Throwable throwable) {
                Log.write(LoggerType.FATAL, throwable.getLocalizedMessage());
            }
        } else {
            throw new NoMainClassFoundException(currentProject.getProjectName() + " >> check main class manually");
        }
    }

    @Override
    public void finish() {
        Log.write(LoggerType.DEBUG, "WORKER: RealFlowWorker FINISHED!");
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
    public IDroidefenseClass[] getInitialDVMClass() {
        //get all
        if (currentProject.hasMainClass())
            return new IDroidefenseClass[]{currentProject.getInternalInfo().getDexClass(currentProject.getMainClassName())};
        else {
            return currentProject.getDeveloperClasses();
        }
    }

    @Override
    public strictfp void execute(boolean endless) throws Throwable {

        IDroidefenseFrame frame = getCurrentFrame();
        IDroidefenseMethod method = frame.getMethod();

        int[] lowerCodes = method.getOpcodes();
        int[] upperCodes = method.getRegisterOpcodes();
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
                            upperCodes = method.getRegisterOpcodes();
                            lowerCodes = method.getOpcodes();
                            codes = method.getIndex();
                            continue;
                        }
                    }
                    break;
                }
                int instVal = lowerCodes[frame.getPc()];
                Log.write(LoggerType.TRACE, "DalvikInstruction: 0x" + Integer.toHexString(instVal));
                DalvikInstruction currentInstruction = AbstractDVMThread.instructions[instVal];
                InstructionReturn returnValue = currentInstruction.execute(flowMap, frame, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                if (returnValue != null) {
                    //first check for errors in DalvikInstruction execution
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
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
            }
        }
    }
}