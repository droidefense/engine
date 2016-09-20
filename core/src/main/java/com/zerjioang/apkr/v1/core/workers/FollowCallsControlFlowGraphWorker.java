package com.zerjioang.apkr.v1.core.workers;

import com.zerjioang.apkr.temp.ApkrIntelligence;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DalvikVM;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.fake.DVMTaintClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.fake.DVMTaintMethod;
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
import com.zerjioang.apkr.v1.core.cfg.nodes.MethodNode;
import com.zerjioang.apkr.v1.core.cfg.nodes.NormalNode;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public final strictfp class FollowCallsControlFlowGraphWorker extends AbstractDVMThread {

    private static AbstractFlowMap flowMap = new BasicCFGFlowMap();
    private int[] lowerCodes;
    private int[] upperCodes;
    private int[] codes;
    private AbstractAtomNode fromNode;
    private AbstractAtomNode toNode;

    public FollowCallsControlFlowGraphWorker(ApkrProject project) {
        super(new DalvikVM(project), project);
        fromNode = null;
    }

    public FollowCallsControlFlowGraphWorker(final DalvikVM vm, ApkrProject project) {
        super(vm, project);
        fromNode = null;
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: FollowCallsControlFlowGraphWorker");
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
        this.removeFrames();
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
        currentProject.setFollowCallsMap(flowMap);
        Log.write(LoggerType.DEBUG, "WORKER: FollowCallsControlFlowGraphWorker FINISHED!");
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
        toNode = MethodNode.builder(flowMap, method, 0);
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
                if (inst >= 0x44 && inst <= 0x6D) {
                    //GETTER SETTER
                    //do not execute that instruction. just act like if it was executed incrementing pc value properly
                    InstructionReturn ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, Instruction.CFG_EXECUTION);
                    //create node
                    toNode = NormalNode.builder(flowMap, currentInstruction, currentInstruction.code(), currentInstruction.description());
                } else if ((inst >= 0x6E && inst <= 0x78) || (inst == 0xF0) || (inst >= 0xF8 && inst <= 0xFB)) {
                    //CALLS
                    InstructionReturn fakeCallReturn = fakeMethodCall(frame.getMethod());
                    toNode = MethodNode.builder(flowMap, fakeCallReturn.getMethod(), frame.getPc());
                } else if (inst == 0x00) {
                    //NOP
                    //nop of increases pc by one
                    frame.increasePc(1);
                    toNode = NormalNode.builder(flowMap, currentInstruction, "op", "NOP");
                } else if (inst >= 0xE && inst <= 0x11) {
                    //return-void
                    //nop of increases pc by one
                    frame.increasePc(1);
                    toNode = NormalNode.builder(flowMap, currentInstruction, "return", "void");
                } else {
                    //OTHER INST
                    //do not execute that instruction. just act like if it was executed incrementing pc value properly
                    InstructionReturn ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, Instruction.CFG_EXECUTION);
                    AbstractAtomNode node = ret.getNode();
                    if (node == null) {
                        node = NormalNode.builder(flowMap, currentInstruction, currentInstruction.code(), currentInstruction.description());
                    }
                    toNode = node;
                }
                //create the connection
                createNewConnection(fromNode, toNode, currentInstruction);
                fromNode = toNode;
                toNode = null;
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


    private void createNewConnection(AbstractAtomNode from, AbstractAtomNode to, Instruction currentInstruction) {
        from = flowMap.addNode(from);
        to = flowMap.addNode(to);
        //avoid connections with itself
        if (true || !from.getConnectionLabel().equals(to.getConnectionLabel())) {
            NodeConnection conn = new NodeConnection(from, to, currentInstruction);
            flowMap.addConnection(conn);
        }
    }
}