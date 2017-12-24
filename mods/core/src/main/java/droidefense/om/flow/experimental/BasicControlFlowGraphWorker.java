package droidefense.om.flow.experimental;


import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.rulengine.map.BasicCFGFlowMap;
import droidefense.rulengine.nodes.EntryPointNode;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.handler.FileIOHandler;
import droidefense.om.flow.base.AbstractFlowWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.struct.fake.DVMTaintClass;
import droidefense.om.machine.base.struct.fake.DVMTaintMethod;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.om.machine.inst.InstructionReturn;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public final strictfp class BasicControlFlowGraphWorker extends AbstractFlowWorker {

    private int[] lowerCodes;
    private int[] upperCodes;
    private int[] codes;

    public BasicControlFlowGraphWorker(DroidefenseProject project) {
        super(project.getDalvikMachine(), project);
        flowMap = new BasicCFGFlowMap();
        fromNode = null;
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: BasicControlFlowGraphWorker");
        //todo check if this is needed cause most of the times points to same memory addresses
        /*
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
         */
    }

    @Override
    public void run() {
        try {
            execute(false);
        } catch (Throwable throwable) {
            Log.write(LoggerType.ERROR, throwable.getLocalizedMessage());
        }
    }

    @Override
    public void finish() {
        currentProject.setNormalControlFlowMap(flowMap);

        Log.write(LoggerType.DEBUG, "WORKER: BasicControlFlowGraphWorker FINISHED!");
        //generate image as svg
        //dot -Tsvg *.dot > flowMap.svg
        try {
            String currentUnpackDir = FileIOHandler.getUnpackOutputPath(currentProject.getSample());
            //FileIOHandler.callSystemExec("dot -Tps " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.ps");
            //FileIOHandler.callSystemExec("ps2pdf " + currentUnpackDir + File.separator + "flowMap.ps" + " " + currentUnpackDir + File.separator + "flowMap.pdf");
            //TODO fix map generation
            FileIOHandler.callSystemExec("dot -Tsvg " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.svg");
        } catch (IOException e) {
            Log.write(LoggerType.ERROR, e.getLocalizedMessage());
        }
    }

    @Override
    public int getInitialArgumentCount(IDroidefenseClass cls, IDroidefenseMethod m) {
        return DO_NOT_USE_ARGUMENTS_COUNT;
    }

    @Override
    public Object getInitialArguments(IDroidefenseClass cls, IDroidefenseMethod m) {
        return DO_NOT_USE_ARGUMENTS;
    }

    @Override
    public IDroidefenseClass[] getInitialDVMClass() {
        //only return developer class and skip known java jdk and android sdk classes

        IDroidefenseClass[] list = currentProject.getDeveloperClasses();
        Log.write(LoggerType.TRACE, "Estimated node count: ");
        int nodes = 0;
        for (IDroidefenseClass cls : list) {
            nodes += cls.getAllMethods().length;
        }
        Log.write(LoggerType.TRACE, nodes + " developer nodes");
        flowMap.setMaxNodes(nodes);
        return list;
    }

    @Override
    public IDroidefenseMethod[] getInitialMethodToRun(IDroidefenseClass dexClass) {
        return dexClass.getAllMethods();
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
    public strictfp void execute(boolean keepScanning) throws Throwable {

        IAtomFrame frame = getCurrentFrame();
        IDroidefenseMethod method = frame.getMethod();

        lowerCodes = method.getOpcodes();
        upperCodes = method.getRegistercodes();
        codes = method.getIndex();

        keepScanning = true;
        boolean methodEnded = false;

        fromNode = EntryPointNode.builder();
        toNode = buildMethodNode(DalvikInstruction.DALVIK_0x0, frame, method);
        createNewConnection(fromNode, toNode, DalvikInstruction.DALVIK_0x0);
        fromNode = toNode;
        toNode = null;

        while (keepScanning) {

            int currentPc = frame.getPc();
            int currentInstructionOpcode;

            //1 ask if we have more currentInstructionOpcode to execute
            if (currentPc >= lowerCodes.length || getFrames() == null || getFrames().isEmpty())
                break;

            //skip sdk methods
            if (getFrames().size() > 1) {
                keepScanning = goBack(0);
                continue;
            }

            currentInstructionOpcode = lowerCodes[currentPc];
            DalvikInstruction currentInstruction = instructions[currentInstructionOpcode];
            Log.write(LoggerType.TRACE, currentInstruction.name() + " " + currentInstruction.description());

            try {
                InstructionReturn ret;
                if (isGetterOrSetterInstruction(currentInstructionOpcode)) {
                    //GETTER SETTER
                    //do not execute that DalvikInstruction. just act like if it was executed incrementing pc value properly
                    ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                } else if (isCallMethodInstruction(currentInstructionOpcode)) {
                    //CALLS
                    InstructionReturn fakeCallReturn = fakeMethodCall(frame.getMethod());
                    //create invokated method as node
                    toNode = buildMethodNode(currentInstruction, frame, fakeCallReturn.getMethod());
                    //create the connection
                    createNewConnection(fromNode, toNode, currentInstruction);
                } else if (isNOPInstruction(currentInstructionOpcode)) {
                    //NOP
                    //nop of increases pc by one
                    frame.increasePc(1);
                } else if (isVoidInstruction(currentInstructionOpcode)) {
                    //return-void
                    methodEnded = true;
                } else {
                    //OTHER INST
                    //do not execute that DalvikInstruction. just act like if it was executed incrementing pc value properly
                    ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                }

                //check if there are more instructions to execute
                if (methodEnded) {
                    //method instructions are all executed. this method is ended. stop loop
                    keepScanning = false;
                    methodEnded = false;
                    //keepScanning = goBack(1);
                }
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Excepcion during observation", e, e.getLocalizedMessage());
            }
        }
    }

    private boolean goBack(int fakePc) {

        //remove last frame and set the new one the last one
        IAtomFrame supposedPreviousFrame = null;
        Vector list = getCurrentFrame().getThread().getFrames();
        if (list != null && !list.isEmpty()) {
            list.remove(list.size() - 1);
            if (!list.isEmpty()) {
                //set current frame list lastone
                supposedPreviousFrame = (IAtomFrame) list.get(list.size() - 1);
            } else {
                //no las frame, set null;
                supposedPreviousFrame = null;
            }
        }

        if (supposedPreviousFrame != null) {
            //set as current frame
            replaceCurrentFrame(supposedPreviousFrame);
            //reload method
            getCurrentFrame().setMethod(supposedPreviousFrame.getMethod());

            //restore codes
            lowerCodes = getCurrentFrame().getMethod().getOpcodes();
            upperCodes = getCurrentFrame().getMethod().getRegistercodes();
            codes = getCurrentFrame().getMethod().getIndex();
            getCurrentFrame().increasePc(fakePc);
            return true;
        }

        return false;
    }

    private InstructionReturn fakeMethodCall(IDroidefenseMethod method) {

        IAtomFrame frame = getCurrentFrame();

        // invoke-virtual {vD, vE, vF, vG, vA}, meth@CCCC
        int registers = upperCodes[frame.increasePc()] << 16;
        int methodIndex = codes[frame.increasePc()];
        registers |= codes[frame.increasePc()];

        //Todo fix null pointer when calling this method using tainted classes

        String clazzName;
        String methodName;
        String methodDescriptor;
        if (method.isFake()) {
            clazzName = method.getOwnerClass().getName();
            methodName = method.getName();
            methodDescriptor = method.getDescriptor();
        } else {
            clazzName = method.getMethodClasses()[methodIndex];
            methodName = method.getMethodNames()[methodIndex];
            methodDescriptor = method.getMethodTypes()[methodIndex];
        }

        IDroidefenseClass cls = new DVMTaintClass(clazzName);
        return getInstructionReturn(clazzName, methodName, methodDescriptor, cls);
    }

    private InstructionReturn getInstructionReturn(String clazzName, String methodName, String methodDescriptor, IDroidefenseClass cls) {
        IDroidefenseMethod methodToCall = cls.getMethod(methodName, methodDescriptor, false);
        //if class is an interface, It will not have the method to be called
        if (methodToCall == null) {
            methodToCall = new DVMTaintMethod(methodName, clazzName);
            methodToCall.setDescriptor(methodDescriptor);
            methodToCall.setOwnerClass(cls);
        }
        IAtomFrame frame = callMethod(false, methodToCall, getCurrentFrame());
        int[] lowerCodes = methodToCall.getOpcodes();
        int[] upperCodes = methodToCall.getRegistercodes();
        int[] codes = methodToCall.getIndex();
        return new InstructionReturn(frame, methodToCall, lowerCodes, upperCodes, codes, null);
    }
}