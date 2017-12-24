package droidefense.om.flow.experimental;

import droidefense.entropy.EntropyCalculator;
import droidefense.handler.FileIOHandler;
import droidefense.om.machine.base.struct.generic.IDroidefenseClass;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.om.flow.base.AbstractFlowWorker;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.struct.fake.DVMTaintMethod;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.om.machine.inst.InstructionReturn;
import droidefense.rulengine.map.BasicCFGFlowMap;
import droidefense.rulengine.nodes.EntryPointNode;
import droidefense.rulengine.nodes.FieldNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public final strictfp class ReflectionControlFlowGraphWorker extends AbstractFlowWorker {

    private int[] lowerCodes;
    private int[] upperCodes;
    private int[] codes;
    private String lastStringReaded, lastReflectedMethodName;
    private IDroidefenseClass lastReflectedClass;
    private boolean reflected;

    public ReflectionControlFlowGraphWorker(DroidefenseProject project) {
        super(project.getDalvikMachine(), project);
        flowMap = new BasicCFGFlowMap();
        fromNode = null;
    }

    public ReflectionControlFlowGraphWorker(final DalvikVM vm, DroidefenseProject project) {
        super(vm, project);
        flowMap = new BasicCFGFlowMap();
        fromNode = null;
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: ReflectionControlFlowGraphWorker");
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
        currentProject.setReflectedFlowMap(flowMap);
        Log.write(LoggerType.DEBUG, "WORKER: ReflectionControlFlowGraphWorker FINISHED!");
        //generate image as svg
        //dot -Tsvg *.dot > flowMap.svg
        try {
            String currentUnpackDir = FileIOHandler.getUnpackOutputPath(currentProject.getSample());
            //FileIOHandler.callSystemExec("dot -Tps " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.ps");
            //FileIOHandler.callSystemExec("ps2pdf " + currentUnpackDir + File.separator + "flowMap.ps" + " " + currentUnpackDir + File.separator + "flowMap.pdf");
            //TODO fix map generation
            FileIOHandler.callSystemExec("dot -Tsvg " + currentUnpackDir + File.separator + "graphviz.dot" + " > " + currentUnpackDir + File.separator + "flowMap.svg");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        //only return developer class and skip known java jdk and android sdk classes
        return currentProject.getDeveloperClasses();
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

        fromNode = EntryPointNode.builder();
        toNode = MethodNode.builder(
                flowMap,
                DalvikInstruction.DALVIK_0x0.description(),
                method.getName(),
                method.getByteCode().length,
                method.getOwnerClass().getName(),
                method.getTopClass().getName(),
                !method.isFake(),
                DynamicUtils.getParamStringFromDescriptor(method.getDescriptor()),
                DynamicUtils.getReturnTypeFromDescriptor(method.getDescriptor()),
                EntropyCalculator.getInstance().getMethodEntropy(method.getOpcodes()),
                0,
                method.isFake()
        );
        createNewConnection(fromNode, toNode, DalvikInstruction.DALVIK_0x0);
        fromNode = toNode;
        toNode = null;

        while (keepScanning) {
            int currentPc = frame.getPc();
            int inst = 0;

            //1 ask if we have more inst to execute
            if (currentPc >= lowerCodes.length || getFrames() == null || getFrames().isEmpty())
                break;

            //skip sdk methods
            if (method.isFake()) {
                keepScanning = goBack(1);
                continue;
            }

            inst = lowerCodes[currentPc];
            DalvikInstruction currentInstruction = instructions[inst];
            Log.write(LoggerType.TRACE, currentInstruction.name() + " " + currentInstruction.description());

            /*if (inst != 0x1A) {
                addCurrentMethodAsNode(currentInstruction);
            }*/
            //invoke call
            if (inst >= 0x44 && inst <= 0x6D) {
                //fake iget, sget, iput, sput,...
                /**
                 * 44: aget
                 45: aget-wide
                 46: aget-object
                 47: aget-boolean
                 48: aget-byte
                 49: aget-char
                 4a: aget-short
                 4b: aput
                 4c: aput-wide
                 4d: aput-object
                 4e: aput-boolean
                 4f: aput-byte
                 50: aput-char
                 51: aput-short
                 52: iget
                 53: iget-wide
                 54: iget-object
                 55: iget-boolean
                 56: iget-byte
                 57: iget-char
                 58: iget-short
                 59: iput
                 5a: iput-wide
                 5b: iput-object
                 5c: iput-boolean
                 5d: iput-byte
                 5e: iput-char
                 5f: iput-short
                 60: sget
                 61: sget-wide
                 62: sget-object
                 63: sget-boolean
                 64: sget-byte
                 65: sget-char
                 66: sget-short
                 67: sput
                 68: sput-wide
                 69: sput-object
                 6a: sput-boolean
                 6b: sput-byte
                 6c: sput-char
                 6d: sput-short
                 **/

                //do not execute that DalvikInstruction. just act like if it was executed incrementing pc value properly
                InstructionReturn ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                //if ret is null, go back to the previous state/frame
                //some instructions, however can read and get fields, in that cases, represent that read
                //addGetSetMethodAsNode(currentInstruction);
                /*if (ret != null && ret.getField() != null) {
                    //field read DalvikInstruction
                    toNode = FieldNode.builder(currentInstruction, ret.getField(), frame.getPc());
                    //create the connection
                    createNewConnection(fromNode, toNode);
                } else if (ret != null) {
                    toNode = MethodNode.builder(currentInstruction, ret.getMethod(), frame.getPc());
                    //create the connection
                    createNewConnection(fromNode, toNode);
                } else {
                    toNode = NormalNode.builder(currentInstruction, getCurrentFrame(), frame.getMethod());
                    //create the connection
                    createNewConnection(fromNode, toNode);
                }
                fromNode = toNode;
                toNode = null;*/
                frame.increasePc(currentInstruction.fakePcIncrement());
            } else if ((inst >= 0x6E && inst <= 0x78) || (inst == 0xF0) || (inst >= 0xF8 && inst <= 0xFB)) {
                /**
                 6e: invoke-virtual
                 6f: invoke-super
                 70: invoke-direct
                 71: invoke-static
                 72: invoke-interface
                 74: invoke-virtual/range
                 75: invoke-super/range
                 76: invoke-direct/range
                 77: invoke-static/range
                 78: invoke-interface/range
                 */
                //get counter increment from real DalvikInstruction
                int fakePc = currentInstruction.fakePcIncrement();
                InstructionReturn fakeCallReturn = fakeMethodCall(frame.getMethod(), upperCodes[frame.increasePc()], codes[frame.increasePc()]);
                //updateNextNode(currentInstruction, fakeCallReturn, fakePc);
                //increment the frake with the fake pc
                frame.increasePc(fakePc - 2);
                if (!reflected) {
                    //create invokated method as node

                    IDroidefenseMethod fakeMethod = fakeCallReturn.getMethod();

                    toNode = buildMethodNode(currentInstruction, frame, fakeMethod);

                    //create the connection
                    createNewConnection(fromNode, toNode, currentInstruction);
                /*fromNode = toNode;
                toNode = null;*/
                }
            } else if (inst == 0x1A) {
                /**
                 1a: const-string
                 */
                int destination = upperCodes[frame.increasePc()];
                String str = frame.getMethod().getStrings()[codes[frame.increasePc()]];
                lastStringReaded = str;
                //toNode = ConstStrNode.builder(frame, currentInstruction, destination, str);
                //createNewConnection(fromNode, toNode);
                /*fromNode = toNode;
                toNode = null;*/
            } else if (inst == 0x00) {
                /**
                 * 00: nop
                 */
                //nop DalvikInstruction. only increase pc
                //nop of increases pc by one
                frame.increasePc(1);
                //check the end of the fake method DalvikInstruction
                /*if (frame.getPc() >= lowerCodes.length) {
                    //method instructions are all executed. this method is ended. stop loop
                    keepScanning = false;
                    keepScanning = goBack(1);
                }*/
                //addCallMethodAsNode(currentInstruction);
            } else {
                //do not execute that DalvikInstruction. just act like if it was executed incrementing pc value properly
                InstructionReturn ret = currentInstruction.execute(flowMap, this, lowerCodes, upperCodes, codes, DalvikInstruction.CFG_EXECUTION);
                frame.increasePc(currentInstruction.fakePcIncrement());
                if (frame.getPc() + 1 >= lowerCodes.length) {
                    //keepScanning = goBack(1);
                } else {
                    //updateNextNode(currentInstruction, ret, 0);
                }
                //addCallMethodAsNode(currentInstruction);
            }

            //check if there are more instructions to execute
            if (frame.getPc() >= lowerCodes.length) {
                //method instructions are all executed. this method is ended. stop loop
                keepScanning = false;
                //keepScanning = goBack(1);
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

    private void updateNextNode(DalvikInstruction inst, InstructionReturn returnValue, int fakePc) {
        if (returnValue != null) {
            //first check for errors in DalvikInstruction execution
            if (returnValue.getError() != null) {
                returnValue.getError().printStackTrace();
            }
            //two different behaviours: method execution or getter/setter execution
            if (returnValue.getField() != null) {
                //getter/setter
                IAtomFrame frame = returnValue.getFrame();
                //create a new connection between previousNode and this new method
                FieldNode newMethodNode = buildFieldNode(inst, returnValue.getField(), frame.getPc());
            } else {
                //normal method execution
                //if no errors, update values
                IAtomFrame frame = returnValue.getFrame();
                IDroidefenseMethod method = returnValue.getMethod();
                /*upperCodes = returnValue.getRegistercodes();
                lowerCodes = returnValue.getOpcodes();
                codes = returnValue.getIndex();

                //update getters && setters
                setgetCurrentFrame().getMethod()(method);
                setgetCurrentFrame()(frame);*/

                //create a new connection between previousNode and this new method

                MethodNode newMethodNode = buildMethodNode(inst, frame, method);

                //add this new node to flow map
                flowMap.addNode(newMethodNode);

                //create new connection
                //createNewConnection(inst, newMethodNode);

                //add this new method as new node to flowMap
                //addCallMethodAsNode(inst);
            }
        }
    }

    private InstructionReturn fakeMethodCall(IDroidefenseMethod method, int upperCode, int code) {
        int registers = upperCode << 16;
        int methodIndex = code;
        registers |= code;

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

        IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
        reflected = false;
        if (cls != null) {
            //bypass reflection
            if (clazzName.equals("java/lang/Class") || clazzName.equals("dalvik/system/DexClassLoader")) {
                reflected = true;
                if (methodName.equals("forName") || methodName.equals("loadClass")) {
                    //developer using reflection
                    if (lastStringReaded == null)
                        lastStringReaded = "Unknown-to-determine-class";
                    IDroidefenseClass reflectedClass = DexClassReader.getInstance().load(lastStringReaded);
                    cls = reflectedClass;
                    lastReflectedClass = cls;
                    return getInstructionReturn(clazzName, lastStringReaded, methodDescriptor, lastReflectedClass);
                } else if (methodName.equals("getDeclaredMethod")) {
                    lastReflectedMethodName = lastStringReaded;
                    return getInstructionReturn(lastReflectedClass.getName(), lastReflectedMethodName, "", lastReflectedClass);
                }
            } else if (clazzName.equals("java/lang/reflect/Method")) {
                //stop reflection to print node in model
                //avoid method params for now
                reflected = false;
                return getInstructionReturn(lastReflectedClass.getName(), lastReflectedMethodName, "()", lastReflectedClass);
            } else {
                return getInstructionReturn(clazzName, methodName, methodDescriptor, cls);
            }
        }
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