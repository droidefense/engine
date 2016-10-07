package com.zerjioang.apkr.analysis.flow;


import apkr.external.modules.controlflow.model.map.BasicCFGFlowMap;
import apkr.external.modules.controlflow.model.nodes.EntryPointNode;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.DalvikVM;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.fake.DVMTaintClass;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.fake.DVMTaintMethod;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.analysis.dynamicscan.machine.inst.Instruction;
import com.zerjioang.apkr.analysis.dynamicscan.machine.inst.InstructionReturn;
import com.zerjioang.apkr.analysis.dynamicscan.machine.reader.DexClassReader;
import com.zerjioang.apkr.analysis.handlers.FileIOHandler;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;
import com.zerjioang.apkr.temp.ApkrIntelligence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public final strictfp class BasicControlFlowGraphWorker extends AbstractFlowWorker {

    private int[] lowerCodes;
    private int[] upperCodes;
    private int[] codes;
    private String lastStringReaded;
    private IAtomClass lastReflectedClass;
    private boolean reflected;

    public BasicControlFlowGraphWorker(final DalvikVM vm, ApkrProject project) {
        super(vm, project);
        flowMap = new BasicCFGFlowMap();
        fromNode = null;
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: BasicControlFlowGraphWorker");
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
    }

    @Override
    public void run() {
        try {
            execute(false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void finish() {
        currentProject.setNormalControlFlowMap(flowMap);

        Log.write(LoggerType.DEBUG, "WORKER: BasicControlFlowGraphWorker FINISHED!");
        //generate image as svg
        //dot -Tsvg *.dot > flowMap.svg
        try {
            String currentUnpackDir = FileIOHandler.getUnpackOutputPath(currentProject.getSourceFile());
            //FileIOHandler.callSystemExec("dot -Tps " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.ps");
            //FileIOHandler.callSystemExec("ps2pdf " + currentUnpackDir + File.separator + "flowMap.ps" + " " + currentUnpackDir + File.separator + "flowMap.pdf");
            //TODO fix map generation
            FileIOHandler.callSystemExec("dot -Tsvg " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.svg");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        //only return developer class and skip known java jdk and android sdk classes
        IAtomClass[] alllist = DexClassReader.getInstance().getAllClasses();
        ArrayList<IAtomClass> developerClasses = new ArrayList<>();
        for (IAtomClass cls : alllist) {
            if (ApkrIntelligence.getInstance().isDeveloperClass(cls.getName())
                    && !ApkrIntelligence.getInstance().isAndroidRclass(cls.getName())
                    )
                developerClasses.add(cls);
        }
        IAtomClass[] list = developerClasses.toArray(new IAtomClass[developerClasses.size()]);
        Log.write(LoggerType.TRACE, "Estimated node count: ");
        int nodes = 0;
        for (IAtomClass cls : list) {
            nodes += cls.getAllMethods().length;
        }
        Log.write(LoggerType.TRACE, nodes + " developer nodes");
        flowMap.setMaxNodes(nodes);
        return list;
    }

    @Override
    public IAtomMethod[] getInitialMethodToRun(IAtomClass dexClass) {
        return dexClass.getAllMethods();
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
    public strictfp void execute(boolean keepScanning) throws Throwable {

        IAtomFrame frame = getCurrentFrame();
        IAtomMethod method = frame.getMethod();

        lowerCodes = method.getOpcodes();
        upperCodes = method.getRegistercodes();
        codes = method.getIndex();

        keepScanning = true;

        fromNode = EntryPointNode.builder();
        toNode = buildMethodNode(Instruction.DALVIK_0x0, frame, method);
        createNewConnection(fromNode, toNode, Instruction.DALVIK_0x0);
        fromNode = toNode;
        toNode = null;

        while (keepScanning) {
            int currentPc = frame.getPc();
            int inst;

            //1 ask if we have more inst to execute
            if (currentPc >= lowerCodes.length || getFrames() == null || getFrames().isEmpty())
                break;

            //skip sdk methods
            if (getFrames().size() > 1) {
                keepScanning = goBack(0);
                continue;
            }

            inst = lowerCodes[currentPc];
            Instruction currentInstruction = instructions[inst];
            Log.write(LoggerType.TRACE, currentInstruction.name() + " " + currentInstruction.description());

            try {
                InstructionReturn ret;
                if (inst >= 0x44 && inst <= 0x6D) {
                    //GETTER SETTER
                    //do not execute that instruction. just act like if it was executed incrementing pc value properly
                    ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, Instruction.CFG_EXECUTION);
                } else if ((inst >= 0x6E && inst <= 0x78) || (inst == 0xF0) || (inst >= 0xF8 && inst <= 0xFB)) {
                    //CALLS
                    InstructionReturn fakeCallReturn = fakeMethodCall(frame.getMethod());
                    //create invokated method as node
                    toNode = buildMethodNode(currentInstruction, frame, fakeCallReturn.getMethod());
                    //create the connection
                    createNewConnection(fromNode, toNode, currentInstruction);
                } else if (inst == 0x00) {
                    //NOP
                    //nop of increases pc by one
                    frame.increasePc(1);
                } else if (inst >= 0xE && inst <= 0x11) {
                    //return-void
                    //nop of increases pc by one
                    frame.increasePc(1);
                } else {
                    //OTHER INST
                    //do not execute that instruction. just act like if it was executed incrementing pc value properly
                    ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, Instruction.CFG_EXECUTION);
                }
                //check if there are more instructions to execute
                if (frame.getPc() >= lowerCodes.length) {
                    //method instructions are all executed. this method is ended. stop loop
                    keepScanning = false;
                    //keepScanning = goBack(1);
                }
            } catch (Exception e) {
                Log.write(LoggerType.ERROR, "Excepcion during observation", e, e.getLocalizedMessage());
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

    private InstructionReturn fakeMethodCall(IAtomMethod method) {

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

        IAtomClass cls = new DVMTaintClass(clazzName);
        return getInstructionReturn(clazzName, methodName, methodDescriptor, cls);
    }

    private InstructionReturn getInstructionReturn(String clazzName, String methodName, String methodDescriptor, IAtomClass cls) {
        IAtomMethod methodToCall = cls.getMethod(methodName, methodDescriptor, false);
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