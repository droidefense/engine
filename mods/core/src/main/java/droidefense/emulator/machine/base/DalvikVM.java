package droidefense.emulator.machine.base;

import droidefense.emulator.featured.AndroidLogEmulator;
import droidefense.emulator.featured.ReflectionEmulator;
import droidefense.emulator.machine.base.constants.TypeDescriptorSemantics;
import droidefense.emulator.machine.base.exceptions.ChangeThreadRuntimeException;
import droidefense.emulator.machine.base.exceptions.MachineStateEndedException;
import droidefense.emulator.machine.base.exceptions.VirtualMachineRuntimeException;
import droidefense.emulator.machine.base.struct.fake.DVMTaintClass;
import droidefense.emulator.machine.base.struct.fake.EncapsulatedClass;
import droidefense.emulator.machine.base.struct.generic.*;
import droidefense.emulator.machine.reader.DexClassReader;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.io.AbstractHashedFile;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

public class DalvikVM extends AbstractVirtualMachine {

    public static final boolean MULTIDEX = true;
    private static final int TIMEOUT_THRESHOLD = 500000;
    private static final int TIME_ZERO = 0;
    private static final int TIME_MAX = 999999;
    private final DexClassReader systemClassLoader;
    private Object[] lastMethodArgs;

    /**
     * Given dex file has no main class
     *
     * @param project
     */
    public DalvikVM(DroidefenseProject project) {
        this(null, project);
    }

    /**
     * Run with main class & arguments
     *
     * @param mainClassName
     * @param argument
     * @param project
     */
    public DalvikVM(String mainClassName, String[] argument, DroidefenseProject project) {
        super(mainClassName, argument, project);
        DexClassReader.init(this, project);
        systemClassLoader = DexClassReader.getInstance();
    }

    /**
     * Run with main class and NO arguments
     *
     * @param mainClassName
     * @param project
     */
    public DalvikVM(String mainClassName, DroidefenseProject project) {
        super(mainClassName, null, project);
        DexClassReader.init(this, project);
        DexClassReader.init(this, project);
        systemClassLoader = DexClassReader.getInstance();
    }

    public void load(AbstractHashedFile dex, byte[] bytes, boolean multidex) {
        Log.write(LoggerType.INFO, "Loading .dex contents...");
        boolean successRead = systemClassLoader.loadClasses(bytes, multidex);
        this.currentProject.setDexFileReaded(successRead);
    }

    @Override
    public void run() throws Throwable {
        Log.write(LoggerType.TRACE, "\n--- SIMULATION START ---\n");

        isEnd = false;

        AbstractDVMThread main = getThread(0);
        if (main != null) {
            IDroidefenseClass baseCls[] = main.getInitialDVMClass();
            main.preload();
            if (baseCls != null && baseCls.length > 0) {
                //there are classes to simulate. simulate them all
                for (IDroidefenseClass currentClass : baseCls) {
                    try {
                        IDroidefenseMethod[] methodList = main.getInitialMethodToRun(currentClass);
                        if (methodList != null && methodList.length > 0) {
                            for (IDroidefenseMethod currentMethod : methodList) {
                                //clean thread context to have a fresh start
                                main = startNewObservationThread(currentClass, currentMethod);
                                Log.write(LoggerType.DEBUG, "\n\nSimulating behaviour of " + currentClass.getName() + "/" + currentMethod.getName() + "()\n");

                                //set thread as started
                                main.start();
                                try {
                                    switch (main.getStatus()) {
                                        case AbstractDVMThread.STATUS_RUNNING:
                                            main.execute(true);
                                            main.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
                                            break;
                                        case AbstractDVMThread.STATUS_JOIN:
                                            break;
                                        case AbstractDVMThread.STATUS_SLEEP:
                                            if (main.wakeUpTime != 0 && main.wakeUpTime <= System.currentTimeMillis()) {
                                                main.setWakeUpTime(0);
                                                main.setStatus(AbstractDVMThread.STATUS_RUNNING);
                                            }
                                            break;
                                        case AbstractDVMThread.STATUS_INTERRUPTED:
                                            main.handleInterrupted();
                                            // execute here for good response after calling #interrupt
                                            if (main.getStatus() == AbstractDVMThread.STATUS_RUNNING) {
                                                main.execute(false);
                                            }
                                            break;
                                        case AbstractDVMThread.STATUS_WAIT_FOR_MONITOR:
                                            break;
                                        case AbstractDVMThread.STATUS_WAIT_FOR_NOTIFICATION:
                                            if (main.wakeUpTime != 0 && main.wakeUpTime <= System.currentTimeMillis()) {
                                                main.setWakeUpTime(0);
                                                main.acquireLock(main.monitorToResume, false);
                                                main.setStatus(AbstractDVMThread.STATUS_RUNNING);
                                            }
                                            break;
                                        default:
                                            throw new VirtualMachineRuntimeException(main.name + " thread status is illegal (=" + main.status + ").");
                                    }
                                } catch (ChangeThreadRuntimeException e) {
                                    Throwable throwable = e.getCause();
                                    if (throwable != null) {
                                        error(throwable);
                                    } else {
                                        Log.write(LoggerType.ERROR, "There are not valid executable methods in current class");
                                    }
                                } catch (MachineStateEndedException e) {
                                    //end with this method, and go to the next
                                    continue;
                                }
                                synchronized (getStopWait()) {
                                    if (stopRequested) {
                                        stopRequested = false;
                                        getStopWait().notify();
                                        isEnd = true;
                                        return;
                                    }
                                }
                            }
                        } else {
                            Log.write(LoggerType.ERROR, "There are not valid executable methods in current class");
                        }
                    } catch (Throwable e) {
                        if (e instanceof VirtualMachineRuntimeException) {
                            throw e;
                        }
                        error(e);
                    }
                }
            } else {
                //there are no classses to simulate
                Log.write(LoggerType.ERROR, "There are no classes to sumulate");
            }
            main.finish();
        } else {
            Log.write(LoggerType.ERROR, "DVM Thread is null.");
        }
        Log.write(LoggerType.TRACE, "\n--- SIMULATION FINISHED ---\n");
    }

    private AbstractDVMThread startNewObservationThread(IDroidefenseClass currentClass, IDroidefenseMethod currentMethod) {
        AbstractDVMThread main;
        main = getThread(0).cleanThreadContext();
        IDroidefenseFrame frame = main.pushFrame();
        frame.init(currentMethod);
        frame.intArgument(main.getInitialArgumentCount(currentClass, currentMethod), main.getInitialArguments(currentClass, currentMethod));
        // TODO check if it is really needed
        // main.start();
        return main;
    }

    public void notifyToThreads(final IDroidefenseFrame frame, final boolean toAllThreads) {
        Object instance = frame.getObjectArguments()[0];
        if (!frame.getThread().hasLock(instance)) {
            throw new IllegalMonitorStateException();
        }
        VMWaitSet waitSet = getWaitSet(instance);
        if (waitSet != null) {
            if (toAllThreads) {
                AbstractDVMThread current = waitSet.getFirstThreadAndRemove();
                while (current != null) {
                    current.acquireLock(instance, false);
                    current = waitSet.getFirstThreadAndRemove();
                }
            } else {
                waitSet.getFirstThreadAndRemove().acquireLock(instance, false);
            }
        }
    }

    public void waitForNotification(final IDroidefenseFrame frame, long timeout, int nanos) {
        Object instance = frame.getObjectArguments()[0];

        AbstractDVMThread thread = frame.getThread();
        if (!thread.hasLock(instance)) {
            throw new IllegalMonitorStateException();
        }
        if (timeout < TIME_ZERO || nanos < TIME_ZERO || TIME_MAX < nanos) {
            throw new IllegalArgumentException();
        }

        thread.releaseLock(instance);
        thread.setStatus(AbstractDVMThread.STATUS_WAIT_FOR_NOTIFICATION);
        if (timeout != TIME_ZERO && nanos != TIME_ZERO) {
            if (TIMEOUT_THRESHOLD <= nanos) {
                timeout += 1;
            }
            thread.setWakeUpTime(System.currentTimeMillis() + timeout);
        } else {
            thread.setWakeUpTime(0);
        }
        thread.getVirtualMachine().addToWaitSet(instance, thread);
        throw new ChangeThreadRuntimeException();
    }

    public DexClassReader getSystemClassLoader() {
        return systemClassLoader;
    }

    protected AbstractVirtualMachine getDalvikVM() {
        return this;
    }

    public void setWorker(AbstractDVMThread worker) {
        setSingleWorker(worker);
    }

    private void setSingleWorker(AbstractDVMThread worker) {
        resetThreads();
        addThread(worker);
    }

    //handler

    public IDroidefenseField getField(final boolean isStatic, final IDroidefenseFrame frame, final String dexClassName, final String fieldName, final int instance, String fieldType) {
        if (isStatic) {
            IDroidefenseClass dexClass = DexClassReader.getInstance().load(dexClassName);
            if (dexClass != null) {
                //get field from .apk cls data
                return dexClass.getStaticField(fieldName);
            }
        } else {
            Object object = frame.getObjectRegisters()[instance];
            if (object == null) {
                IDroidefenseField field = DexClassReader.getInstance().load(dexClassName).getField(fieldName, fieldType);
                return field;
            }
        }
        return null;
    }

    public Object[] setArguments(final boolean isVirtual, final IDroidefenseFrame frame, final String descriptor, int firstRegister, int range) {
        ArrayList<Object> retList = new ArrayList<>();
        int argPos = 0;
        if (isVirtual) {
            Object objreg = frame.getObjectRegisters()[firstRegister];
            frame.setArgument(0, objreg);
            retList.add(objreg);
            argPos++;
        }
        for (int i = 1, length = descriptor.indexOf(')'); i < length; i++) {
            int register = firstRegister + argPos;
            switch (descriptor.charAt(i)) {
                case TypeDescriptorSemantics.DESC_C:
                case TypeDescriptorSemantics.DESC_B:
                case TypeDescriptorSemantics.DESC_S:
                case TypeDescriptorSemantics.DESC_I:
                case TypeDescriptorSemantics.DESC_Z:
                    frame.setArgument(argPos, frame.getIntRegisters()[register]);
                    argPos++;
                    break;
                case TypeDescriptorSemantics.DESC_J:
                    frame.setArgument(argPos, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argPos += 2;
                    break;
                case TypeDescriptorSemantics.DESC_F:
                    // Copy as int because bits data is important
                    frame.setArgument(argPos, frame.getIntRegisters()[register]);
                    argPos++;
                    break;
                case TypeDescriptorSemantics.DESC_D:
                    // Copy as long because bits data is important
                    frame.setArgument(argPos, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argPos += 2;
                    break;
                case TypeDescriptorSemantics.DESC_CLASSNAME: {
                    frame.setArgument(argPos, frame.getObjectRegisters()[register]);
                    argPos++;
                    i = descriptor.indexOf(';', i);
                    break;
                }
                case TypeDescriptorSemantics.DESC_DESCRIPTOR: {
                    int startIndex = i;
                    while (i + 1 < length && descriptor.charAt(i + 1) == TypeDescriptorSemantics.DESC_DESCRIPTOR) {
                        i++;
                    }
                    i++;
                    switch (descriptor.charAt(i)) {
                        case TypeDescriptorSemantics.DESC_C:
                        case TypeDescriptorSemantics.DESC_B:
                        case TypeDescriptorSemantics.DESC_S:
                        case TypeDescriptorSemantics.DESC_I:
                        case TypeDescriptorSemantics.DESC_Z:
                        case TypeDescriptorSemantics.DESC_J:
                        case TypeDescriptorSemantics.DESC_F:
                        case TypeDescriptorSemantics.DESC_D:
                            break;
                        case TypeDescriptorSemantics.DESC_CLASSNAME:
                            i = descriptor.indexOf(';', i);
                            break;
                        default:
                            //throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.substring(startIndex, i + 1));
                    }
                    frame.setArgument(argPos, frame.getObjectRegisters()[register]);
                    argPos++;
                    break;
                }
                default:
                    //throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.charAt(i));
            }
        }
        return retList.toArray(new Object[retList.size()]);
    }

    public Object[] setArguments(boolean isVirtual, IDroidefenseFrame frame, String descriptor, final int registers) {
        ArrayList<Object> retList = new ArrayList<>();
        //TODO frame.clearObjectArguments();
        int argPos = 0;
        if (isVirtual) {
            Object obj = frame.getObjectRegisters()[registers & 0xF];
            if (obj != null) {
                //obj = DexClassReader.getInstance().load(DynamicUtils.descriptorToClassName(descriptor));
                frame.setArgument(argPos, obj);
                retList.add(argPos, obj);
            }
            argPos++;
        }
        int length = descriptor.indexOf(')');
        for (int i = 1; i < length; i++) {
            int register = (registers >> (argPos * 4)) & 0xF;
            switch (descriptor.charAt(i)) {
                case TypeDescriptorSemantics.DESC_C:
                case TypeDescriptorSemantics.DESC_B:
                case TypeDescriptorSemantics.DESC_S:
                case TypeDescriptorSemantics.DESC_I:
                case TypeDescriptorSemantics.DESC_Z:
                    int b = frame.getIntRegisters()[register];
                    retList.add(b);
                    argumentValue(frame, argPos, b);
                    argPos++;
                    break;
                case TypeDescriptorSemantics.DESC_J:
                    long val = DynamicUtils.getLong(frame.getIntRegisters(), register);
                    retList.add(val);
                    argumentValue(frame, argPos, val);
                    argPos += 2;
                    break;
                case 'F':
                    // Copy as int because bits data is important
                    int intval = frame.getIntRegisters()[register];
                    retList.add(intval);
                    argumentValue(frame, argPos, intval);
                    argPos++;
                    break;
                case 'D':
                    // Copy as long because bits data is important
                    long longval = DynamicUtils.getLong(frame.getIntRegisters(), register);
                    retList.add(longval);
                    argumentValue(frame, argPos, longval);
                    argPos += 2;
                    break;
                case TypeDescriptorSemantics.DESC_CLASSNAME: {
                    Object clo = frame.getObjectRegisters()[register];
                    if (clo == null) {
                        //create a fake argument as tainted cls
                        //TODO clo = DexClassReader.getInstance().loadClassFromDescriptor(descriptor);
                    }
                    retList.add(clo);
                    argumentValue(frame, argPos, clo);
                    argPos++;
                    i = descriptor.indexOf(';', i);
                    break;
                }
                case TypeDescriptorSemantics.DESC_DESCRIPTOR: {
                    int startIndex = i;
                    while (i + 1 < length && descriptor.charAt(i + 1) == TypeDescriptorSemantics.DESC_DESCRIPTOR) {
                        i++;
                    }
                    i++;
                    switch (descriptor.charAt(i)) {
                        case TypeDescriptorSemantics.DESC_C:
                        case TypeDescriptorSemantics.DESC_B:
                        case TypeDescriptorSemantics.DESC_S:
                        case TypeDescriptorSemantics.DESC_I:
                        case TypeDescriptorSemantics.DESC_Z:
                        case TypeDescriptorSemantics.DESC_J:
                        case 'F':
                        case 'D':
                            break;
                        case TypeDescriptorSemantics.DESC_CLASSNAME:
                            i = descriptor.indexOf(';', i);
                            break;
                        default:
                            //throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.substring(startIndex, i + 1));
                    }
                    Object o = frame.getObjectRegisters()[register];
                    retList.add(o);
                    argumentValue(frame, argPos, o);
                    argPos++;
                    break;
                }
                default:
                    //throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.charAt(i));
            }
        }
        return retList.toArray(new Object[retList.size()]);
    }

    private void argumentValue(IDroidefenseFrame frame, int argPos, Object clo) {
        if (frame.getObjectArguments()[0] == null) {
            frame.setArgument(0, clo);
        } else {
            frame.setArgument(argPos, clo);
        }
    }

    public IDroidefenseFrame callMethod(final boolean isVirtual, IDroidefenseMethod method, final IDroidefenseFrame frame) {
        IDroidefenseFrame newFrame = getFirstWorker().pushFrame();
        IDroidefenseMethod original = method;
        Object instance = null;
        if (method.isInstance()) {
            instance = frame.getObjectRegisters()[0];
            if (isVirtual && instance != null && instance instanceof IDroidefenseInstance) {
                // Handle override method
                //TODO method = ((IDroidefenseInstance) instance).getDexClass().getVirtualMethod(method.getMethodName(), method.getDescriptor());
            }
            if (method == null) {
                //check super cls
                method = original.getOwnerClass().getVirtualMethod(original.getName(), original.getDescriptor(), true);
            }
        }
        if (method == null) {
            //return null;
        }
        newFrame.init(method);

        int argumentCount = method.getIncomingArgumentCount();
        int destPos = newFrame.getRegisterCount() - argumentCount;
        System.arraycopy(frame.getIntArguments(), 0, newFrame.getIntRegisters(), destPos, argumentCount);
        System.arraycopy(frame.getObjectArguments(), 0, newFrame.getObjectRegisters(), destPos, argumentCount);

        if (method.isSynchronized()) {
            if (method.isInstance()) {
                newFrame.setMonitor(instance);
            } else {
                newFrame.setMonitor(method.getOwnerClass());
            }
            getFirstWorker().acquireLock(newFrame.getMonitor(), true);
        }

        return newFrame;
    }

    public void addInstructionToStack(String dexClassName, String methodName, String methodDescriptor) {
        addInstructionToStack(dexClassName, methodName, methodDescriptor, null);
    }

    public void addInstructionToStack(String dexClassName, String methodName, String methodDescriptor, Object[] args) {
        this.lastMethodArgs = args;
        String arguments = "";
        if (args != null)
            for (Object o : args)
                if (o != null)
                    if (o instanceof String)
                        arguments += " " + o.toString();
                    else
                        arguments += " " + o.toString();
                    //" " + ReflectionToStringBuilder.toString(o, new StandardToStringStyle());
                else
                    arguments += " NULL";
        arguments = arguments.trim();
        arguments = arguments.replace(" ", ", ");
        Log.write(LoggerType.INFO, DynamicUtils.beautifyClassName(dexClassName) + "." + methodName + "(" + arguments + ")");
    }

    public String printArgs(Object[] objectArguments) {
        String arg = "";
        for (int i = 0; i < objectArguments.length; i++) {
            Object o = objectArguments[i];
            if (o != null) {
                if (o instanceof String) {
                    arg += ", " + o;
                } else if (o instanceof Character) {
                    arg += ", " + o;
                } else if (o instanceof Integer) {
                    arg += ", " + o;
                } else if (o instanceof Double) {
                    arg += ", " + o;
                } else if (o instanceof Float) {
                    arg += ", " + o;
                }
            }
        }
        return arg;
    }

    public void getField(final boolean isStatic, final IDroidefenseFrame frame, final int source, final int fieldIndex, final int destination) {
        IDroidefenseMethod method = frame.getMethod();
        String dexClassName = method.getFieldClasses()[fieldIndex];
        String fieldName = method.getFieldNames()[fieldIndex];
        String fieldType = method.getFieldTypes()[fieldIndex];
        addInstructionToStack(dexClassName, fieldName, fieldType);
        IDroidefenseField field = getField(isStatic, frame, dexClassName, fieldName, source, fieldType);
        if (field != null) {
            switch (fieldType.charAt(0)) {
                case TypeDescriptorSemantics.DESC_C:
                case TypeDescriptorSemantics.DESC_B:
                case TypeDescriptorSemantics.DESC_S:
                case TypeDescriptorSemantics.DESC_I:
                    frame.getIntRegisters()[destination] = field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case TypeDescriptorSemantics.DESC_Z:
                    frame.getIntRegisters()[destination] = field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case TypeDescriptorSemantics.DESC_J:
                    DynamicUtils.setLong(frame.getIntRegisters(), destination, field.getLongValue());
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case TypeDescriptorSemantics.DESC_CLASSNAME:
                case TypeDescriptorSemantics.DESC_DESCRIPTOR:
                    frame.getObjectRegisters()[destination] = field.getObjectValue();
                    frame.getIsObjectRegister()[destination] = true;
                    break;
                default:
                    throw new VirtualMachineRuntimeException("not supported field type");
            }
        } else {
            if (isStatic) {
                if (!handleClassFieldGetter(frame, dexClassName, fieldName, fieldType, destination)) {
                    //throw new VirtualMachineRuntimeException("not implemented cls field = " + dexClassName + " - " + fieldName + " - " + fieldType);
                }
            } else {
                if (!handleInstanceFieldGetter(frame, dexClassName, fieldName, fieldType, destination)) {
                    //throw new VirtualMachineRuntimeException("not implemented instance field = " + dexClassName + " - " + fieldName + " - " + fieldType);
                }
            }
        }
    }

    public Object handleNewArray(final String classDescriptor, final int lengthNumber, final int length1, final int length2, final int length3) {
        int dimension = 0;
        for (int i = 0; i < classDescriptor.length() && classDescriptor.charAt(i) == TypeDescriptorSemantics.DESC_DESCRIPTOR; i++) {
            dimension++;
        }
        switch (classDescriptor.charAt(dimension)) {
            case TypeDescriptorSemantics.DESC_CLASSNAME:
                return handleNewObjectArray(classDescriptor.substring(dimension + 1, classDescriptor.length() - 1), dimension, lengthNumber, length1, length2, length3);
            case TypeDescriptorSemantics.DESC_B:
                switch (dimension) {
                    case 1:
                        return new byte[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new byte[length1][];
                            case 2:
                                return new byte[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new byte[length1][][];
                            case 2:
                                return new byte[length1][length2][];
                            case 3:
                                return new byte[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_C:
                switch (dimension) {
                    case 1:
                        return new char[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new char[length1][];
                            case 2:
                                return new char[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new char[length1][][];
                            case 2:
                                return new char[length1][length2][];
                            case 3:
                                return new char[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_I:
                switch (dimension) {
                    case 1:
                        return new int[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new int[length1][];
                            case 2:
                                return new int[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new int[length1][][];
                            case 2:
                                return new int[length1][length2][];
                            case 3:
                                return new int[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_J:
                switch (dimension) {
                    case 1:
                        return new long[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new long[length1][];
                            case 2:
                                return new long[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new long[length1][][];
                            case 2:
                                return new long[length1][length2][];
                            case 3:
                                return new long[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_F:
                switch (dimension) {
                    case 1:
                        return new float[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new float[length1][];
                            case 2:
                                return new float[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new float[length1][][];
                            case 2:
                                return new float[length1][length2][];
                            case 3:
                                return new float[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_D:
                switch (dimension) {
                    case 1:
                        return new double[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new double[length1][];
                            case 2:
                                return new double[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new double[length1][][];
                            case 2:
                                return new double[length1][length2][];
                            case 3:
                                return new double[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_S:
                switch (dimension) {
                    case 1:
                        return new short[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new short[length1][];
                            case 2:
                                return new short[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new short[length1][][];
                            case 2:
                                return new short[length1][length2][];
                            case 3:
                                return new short[length1][length2][length3];
                        }
                        break;
                }
                break;
            case TypeDescriptorSemantics.DESC_Z:
                switch (dimension) {
                    case 1:
                        return new boolean[length1];
                    case 2:
                        switch (lengthNumber) {
                            case 1:
                                return new boolean[length1][];
                            case 2:
                                return new boolean[length1][length2];
                        }
                        break;
                    case 3:
                        switch (lengthNumber) {
                            case 1:
                                return new boolean[length1][][];
                            case 2:
                                return new boolean[length1][length2][];
                            case 3:
                                return new boolean[length1][length2][length3];
                        }
                        break;
                }
                break;
        }
        throw new VirtualMachineRuntimeException("not supported array type = " + classDescriptor);
    }

    public IDroidefenseFrame handleThrowable(final Throwable e, IDroidefenseFrame frame) {
        if (e instanceof ChangeThreadRuntimeException) {
            throw (ChangeThreadRuntimeException) e;
        }
        // At the end, #popFrameByThrowable throws a ChangeThreadRuntimeException exception
        while (true) {
            IDroidefenseMethod method = frame.getMethod();
            if (method.getExceptionStartAddresses() != null) {
                int handlerIndex = -1;
                {
                    int[] exceptionStartAddresses = method.getExceptionStartAddresses();
                    int[] exceptionEndAddresses = method.getExceptionEndAdresses();
                    for (int i = 0, length = exceptionStartAddresses.length; i < length; i++) {
                        if (exceptionStartAddresses[i] < frame.getPc() && frame.getPc() <= exceptionEndAddresses[i]) {
                            handlerIndex = method.getExceptionHandlerIndexes()[i];
                            // Don't exit this loop to search the outer block
                        }
                    }
                }
                if (handlerIndex != -1) {
                    String[] exceptionHandlerTypes = method.getExceptionHandlerTypes()[handlerIndex];
                    int[] exceptionHandlerAddresses = method.getExceptionHandlerAddresses()[handlerIndex];
                    for (int i = 0, length = exceptionHandlerTypes.length; i < length; i++) {
                        if (getFirstWorker().getVirtualMachine().isSubClass(e, exceptionHandlerTypes[i])) {
                            frame.setThrowableReturn(e);
                            frame.resetPc(exceptionHandlerAddresses[i]);
                            return frame;
                        }
                    }
                }
            }
            frame = getFirstWorker().popFrameByThrowable(e);
        }
    }

    public boolean isInstance(final IDroidefenseClass checked, final String type) throws ClassNotFoundException {
        if (checked == null) {
            return false;
        }
        String className = type.startsWith(String.valueOf(TypeDescriptorSemantics.DESC_CLASSNAME)) ? type.substring(1, type.length() - 1) : type;
        IDroidefenseClass vmClass = DexClassReader.getInstance().load(className);
        //TODO
        /*if (vmClass != null) {
            IDroidefenseClass instanceClazz = checked.getDexClass();
            while (vmClass != null) {
                if (instanceClazz == vmClass) {
                    return true;
                }
                vmClass = DexClassReader.getInstance().load(vmClass.getSuperClass());
            }
            return false;
        } else {
            Class nativeClass = Class.forName(className.replace('/', '.'));
            if (checked instanceof DalvikInstance) {
                return nativeClass.isInstance(((DalvikInstance) checked).getParentInstance());
            } else {
                return nativeClass.isInstance(checked);
            }
        }*/
        return true;
    }

    public void setField(final boolean isStatic, final IDroidefenseFrame frame, final int source, final int destination, final int fieldIndex) {
        IDroidefenseMethod method = frame.getMethod();
        String dexClassName = method.getFieldClasses()[fieldIndex];
        String fieldName = method.getFieldNames()[fieldIndex];
        String fieldType = method.getFieldTypes()[fieldIndex];
        //IDroidefenseField field = getField(isStatic, frame, dexClassName, fieldName, destination);
        IDroidefenseField field = getField(isStatic, frame, dexClassName, fieldName, destination, fieldType);
        if (field != null) {
            switch (fieldType.charAt(0)) {
                case TypeDescriptorSemantics.DESC_C:
                case TypeDescriptorSemantics.DESC_B:
                case TypeDescriptorSemantics.DESC_S:
                case TypeDescriptorSemantics.DESC_I:
                case TypeDescriptorSemantics.DESC_Z:
                    field.setIntValue(frame.getIntRegisters()[source]);
                    break;
                case TypeDescriptorSemantics.DESC_J:
                    field.setLongValue(DynamicUtils.getLong(frame.getIntRegisters(), source));
                    break;
                case TypeDescriptorSemantics.DESC_CLASSNAME:
                case TypeDescriptorSemantics.DESC_DESCRIPTOR:
                    field.setObjectValue(frame.getObjectRegisters()[source]);
                    break;
                default:
                    throw new VirtualMachineRuntimeException("not supported field type for descriptor " + fieldType.charAt(0));
            }

        } else {
            if (isStatic) {
                if (!handleClassFieldSetter(frame, source, dexClassName, fieldName, fieldType)) {
                    //throw new VirtualMachineRuntimeException("not implemented cls field = " + dexClassName + " - " + fieldName + " - " + fieldType);
                }
            } else {
                //throw new VirtualMachineRuntimeException("not implemented instance field = " + dexClassName + " - " + fieldName + " - " + fieldType);
            }
        }
    }

    public boolean handleClassFieldGetter(final IDroidefenseFrame frame, final String absoluteClassName, final String fieldName, final String fieldDescriptor, final int destination) {
        // CLASS FIELD SECTION {
        String packageName = absoluteClassName.substring(0, absoluteClassName.lastIndexOf('/'));
        String className = absoluteClassName.substring(absoluteClassName.lastIndexOf('/') + 1);
        if ("java/lang".equals(packageName)) {
            if ("Boolean".equals(className)) {
                if ("TRUE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Boolean.TRUE;
                    frame.getIsObjectRegister()[destination] = true;
                    return true;
                } else if ("FALSE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Boolean.FALSE;
                    frame.getIsObjectRegister()[destination] = true;
                    return true;
                }
            } else if ("System".equals(className)) {
                if ("out".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = System.out;
                    frame.getIsObjectRegister()[destination] = true;
                    return true;
                } else if ("err".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = System.err;
                    frame.getIsObjectRegister()[destination] = true;
                    return true;
                }
            }
        }
        // }
        // not CLDC classes but used in dex file
        if ("java/lang".equals(packageName)) {
            if ("Boolean".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Boolean.class;
                    return true;
                }
            } else if ("Byte".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Byte.class;
                    return true;
                }
            } else if ("Short".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Short.class;
                    return true;
                }
            } else if ("Integer".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Integer.class;
                    return true;
                }
            } else if ("Long".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Long.class;
                    return true;
                }
            } else if ("Float".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Float.class;
                    return true;
                }
            } else if ("Double".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Double.class;
                    return true;
                }
            } else if ("Character".equals(className)) {
                if ("TYPE".equals(fieldName)) {
                    frame.getObjectRegisters()[destination] = Character.class;
                    return true;
                }
            }
        }
        return false;
    }


    public boolean handleClassFieldSetter(final IDroidefenseFrame frame, final int source, final String absoluteClassName, final String fieldName, final String fieldDescriptor) {
        //TODO
        return false;
    }

    // TODO Add types
    public Object handleClassGetter(String type) throws ClassNotFoundException {
        if (type.startsWith("L")) {
            type = type.substring(1, type.length() - 1).replace('/', '.');
            return DexClassReader.getInstance().load(type);
        } else if (type.startsWith("[L")) {
            type = type.substring(2, type.length() - 1);
            String packageName = type.substring(0, type.lastIndexOf('/'));
            String className = type.substring(type.lastIndexOf('/') + 1);
            if ("java/lang".equals(packageName)) {
                if ("Object".equals(className)) {
                    return Object[].class;
                } else if ("String".equals(className)) {
                    return String[].class;
                }
            } else if ("java/util".endsWith(packageName)) {
                if ("Vector".equals(className)) {
                    return Vector[].class;
                }
            }
            throw new ClassNotFoundException();
        } else {
            return Class.forName(type);
        }
    }

    public boolean handleClassMethod(final IDroidefenseFrame frame, IDroidefenseMethod method, final String absoluteClassName, final String methodName, final String methodDescriptor, Object[] args) throws Exception {

        //RETURN REFLECTED CLASS IF POSSIBLE
        IDroidefenseClass cl = DexClassReader.getInstance().load(absoluteClassName);
        if (cl instanceof EncapsulatedClass) {
            EncapsulatedClass tc = (EncapsulatedClass) cl;
            if (tc.isReflected() && tc.getJavaObject() != null) {
                //reflected cls
                //TODO add return types by reflection
                frame.setObjectReturn(tc.getJavaObject());
                return true;
            }
            //RETURN WRAPPER CLASSES IF POSSIBLE
            if (absoluteClassName.equals(ReflectionEmulator.JAVA_REFLECTION_CLASS)) {
                //cls reflection loader. Always return an object
                ReflectionEmulator emulator = new ReflectionEmulator(absoluteClassName);
                emulator.emulate();
                Object o = emulator.getReflectedObject();
                frame.setObjectReturn(o);
                return true;
            } else if (absoluteClassName.equals(AndroidLogEmulator.ANDROID_LOG_CLASS)) {
                //Simulate LOG
                AndroidLogEmulator emulator = new AndroidLogEmulator(methodName, args);
                emulator.emulate();
                return true;
            } else {
                //GENERIC RETURN FOR TAINTED CLASSES
                //TODO add return
                return true;
            }
        } else {
            return true;
        }
        //add to stackTrace
        // CLASS METHOD SECTION
        /*String packageName = absoluteClassName.substring(0, absoluteClassName.lastIndexOf('/'));
        String className = absoluteClassName.substring(absoluteClassName.lastIndexOf('/') + 1);
        if ("android/util".equals(packageName)) {
            if ("Log".equals(className)) {
                new AndroidLogEmulator().executeMethod(methodName, args);
                return true;
            }
        }
        if ("java/util".equals(packageName)) {
            if ("Calendar".equals(className)) {
                if ("getInstance".equals(methodName) && "()Ljava/util/Calendar;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Calendar.getInstance());
                    return true;
                } else if ("getInstance".equals(methodName) && "(Ljava/util/TimeZone;)Ljava/util/Calendar;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Calendar.getInstance((TimeZone) frame.getObjectArguments()[0]));
                    return true;
                } else {
                    return false;
                }
            } else if ("TimeZone".equals(className)) {
                if ("getDefault".equals(methodName) && "()Ljava/util/TimeZone;".equals(methodDescriptor)) {
                    frame.setObjectReturn(TimeZone.getDefault());
                    return true;
                } else if ("getTimeZone".equals(methodName) && "(Ljava/lang/String;)Ljava/util/TimeZone;".equals(methodDescriptor)) {
                    frame.setObjectReturn(TimeZone.getTimeZone((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("getAvailableIDs".equals(methodName) && "()[Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(TimeZone.getAvailableIDs());
                    return true;
                } else {
                    return false;
                }
            }
        } else if ("java/lang".equals(packageName)) {
            if ("Byte".equals(className)) {
                if ("parseByte".equals(methodName) && "(Ljava/lang/String;)B".equals(methodDescriptor)) {
                    frame.setSingleReturn(Byte.parseByte((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("parseByte".equals(methodName) && "(Ljava/lang/String;I)B".equals(methodDescriptor)) {
                    frame.setSingleReturn(Byte.parseByte((String) frame.getObjectArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else {
                    return false;
                }
            } else if ("Character".equals(className)) {
                if ("digit".equals(methodName) && "(CI)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.digit((char) frame.getIntArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("isDigit".equals(methodName) && "(C)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.isDigit((char) frame.getIntArguments()[0]) ? 1 : 0);
                    return true;
                } else if ("isLowerCase".equals(methodName) && "(C)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.isLowerCase((char) frame.getIntArguments()[0]) ? 1 : 0);
                    return true;
                } else if ("isUpperCase".equals(methodName) && "(C)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.isUpperCase((char) frame.getIntArguments()[0]) ? 1 : 0);
                    return true;
                } else if ("toLowerCase".equals(methodName) && "(C)C".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.toLowerCase((char) frame.getIntArguments()[0]));
                    return true;
                } else if ("toUpperCase".equals(methodName) && "(C)C".equals(methodDescriptor)) {
                    frame.setSingleReturn(Character.toUpperCase((char) frame.getIntArguments()[0]));
                    return true;
                } else {
                    return false;
                }
            } else if ("Class".equals(className) && "forName".equals(methodName)) {
                frame.setObjectReturn(Class.forName((String) frame.getObjectArguments()[0]));
                return true;
            } else if ("Double".equals(className)) {
                if ("isNaN".equals(methodName) && "(D)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Double.isNaN(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))) ? 1 : 0);
                    return true;
                } else if ("valueOf".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/Double;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Double.valueOf((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("toString".equals(methodName) && "(D)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Double.toString(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))));
                    return true;
                } else if ("isInfinite".equals(methodName) && "(D)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Double.isInfinite(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))) ? 1 : 0);
                    return true;
                } else if ("parseDouble".equals(methodName) && "(Ljava/lang/String;)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Double.parseDouble((String) frame.getObjectArguments()[0])));
                    return true;
                } else if ("doubleToLongBits".equals(methodName) && "(D)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))));
                    return true;
                } else if ("longBitsToDouble".equals(methodName) && "(J)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))));
                    return true;
                } else {
                    return false;
                }
            } else if ("Float".equals(className)) {
                if ("isNaN".equals(methodName) && "(F)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.isNaN(Float.intBitsToFloat(frame.getIntArguments()[0])) ? 1 : 0);
                    return true;
                } else if ("valueOf".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/Float;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Float.valueOf((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("toString".equals(methodName) && "(F)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Float.toString(Float.intBitsToFloat(frame.getIntArguments()[0])));
                    return true;
                } else if ("parseFloat".equals(methodName) && "(Ljava/lang/String;)F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Float.parseFloat((String) frame.getObjectArguments()[0])));
                    return true;
                } else if ("isInfinite".equals(methodName) && "(F)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.isInfinite(Float.intBitsToFloat(frame.getIntArguments()[0])) ? 1 : 0);
                    return true;
                } else if ("floatToIntBits".equals(methodName) && "(F)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Float.intBitsToFloat(frame.getIntArguments()[0])));
                    return true;
                } else if ("intBitsToFloat".equals(methodName) && "(I)F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Float.intBitsToFloat(frame.getIntArguments()[0])));
                    return true;
                } else {
                    return false;
                }
            } else if ("Integer".equals(className)) {
                if ("valueOf".equals(methodName) && "(Ljava/lang/String;I)Ljava/lang/Integer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.valueOf((String) frame.getObjectArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("valueOf".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/Integer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.valueOf((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("toString".equals(methodName) && "(II)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.toString(frame.getIntArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("toString".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.toString(frame.getIntArguments()[0]));
                    return true;
                } else if ("parseInt".equals(methodName) && "(Ljava/lang/String;I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Integer.parseInt((String) frame.getObjectArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("parseInt".equals(methodName) && "(Ljava/lang/String;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Integer.parseInt((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("toHexString".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.toHexString(frame.getIntArguments()[0]));
                    return true;
                } else if ("toOctalString".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.toOctalString(frame.getIntArguments()[0]));
                    return true;
                } else if ("toBinaryString".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Integer.toBinaryString(frame.getIntArguments()[0]));
                    return true;
                } else {
                    return false;
                }
            } else if ("Long".equals(className)) {
                if ("toString".equals(methodName) && "(JI)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Long.toString(DynamicUtils.getLong(frame.getIntArguments(), 0), frame.getIntArguments()[2]));
                    return true;
                } else if ("toString".equals(methodName) && "(J)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(Long.toString(DynamicUtils.getLong(frame.getIntArguments(), 0)));
                    return true;
                } else if ("parseLong".equals(methodName) && "(Ljava/lang/String;I)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Long.parseLong((String) frame.getObjectArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("parseLong".equals(methodName) && "(Ljava/lang/String;)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Long.parseLong((String) frame.getObjectArguments()[0]));
                    return true;
                } else {
                    return false;
                }
            } else if ("Math".equals(className)) {
                if ("sin".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.sin(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("cos".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.cos(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("tan".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.tan(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("abs".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Math.abs(frame.getIntArguments()[0]));
                    return true;
                } else if ("abs".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Math.abs(DynamicUtils.getLong(frame.getIntArguments(), 0)));
                    return true;
                } else if ("abs".equals(methodName) && "(F)F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Math.abs(Float.intBitsToFloat(frame.getIntArguments()[0]))));
                    return true;
                } else if ("abs".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.abs(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("max".equals(methodName) && "(II)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Math.max(frame.getIntArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("max".equals(methodName) && "(JJ)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Math.max(DynamicUtils.getLong(frame.getIntArguments(), 0), DynamicUtils.getLong(frame.getIntArguments(), 2)));
                    return true;
                } else if ("max".equals(methodName) && "(FF)F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Math.max(Float.intBitsToFloat(frame.getIntArguments()[0]), Float.intBitsToFloat(frame.getIntArguments()[1]))));
                    return true;
                } else if ("max".equals(methodName) && "(DD)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.max(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)), Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 2)))));
                    return true;
                } else if ("min".equals(methodName) && "(II)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(Math.min(frame.getIntArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else if ("min".equals(methodName) && "(JJ)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Math.min(DynamicUtils.getLong(frame.getIntArguments(), 0), DynamicUtils.getLong(frame.getIntArguments(), 2)));
                    return true;
                } else if ("min".equals(methodName) && "(FF)F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(Math.min(Float.intBitsToFloat(frame.getIntArguments()[0]), Float.intBitsToFloat(frame.getIntArguments()[1]))));
                    return true;
                } else if ("min".equals(methodName) && "(DD)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.min(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)), Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 2)))));
                    return true;
                } else if ("sqrt".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.sqrt(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("ceil".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.ceil(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("floor".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.floor(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("toRadians".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.toRadians(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else if ("toDegrees".equals(methodName) && "(D)D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(Math.toDegrees(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0)))));
                    return true;
                } else {
                    return false;
                }
            } else if ("Runtime".equals(className) && "getRuntime".equals(methodName)) {
                frame.setObjectReturn(Runtime.getRuntime());
                return true;
            } else if ("Short".equals(className)) {
                if ("parseShort".equals(methodName) && "(Ljava/lang/String;)S".equals(methodDescriptor)) {
                    frame.setSingleReturn(Short.parseShort((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("parseShort".equals(methodName) && "(Ljava/lang/String;I)S".equals(methodDescriptor)) {
                    frame.setSingleReturn(Short.parseShort((String) frame.getObjectArguments()[0], frame.getIntArguments()[1]));
                    return true;
                } else {
                    return false;
                }
            } else if ("String".equals(className)) {
                if ("valueOf".equals(methodName) && "(Ljava/lang/Object;)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(frame.getObjectArguments()[0]));
                    return true;
                } else if ("valueOf".equals(methodName) && "([C)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf((char[]) frame.getObjectArguments()[0]));
                    return true;
                } else if ("valueOf".equals(methodName) && "([CII)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf((char[]) frame.getObjectArguments()[0], frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("valueOf".equals(methodName) && "(Z)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(frame.getIntArguments()[0] != 0));
                    return true;
                } else if ("valueOf".equals(methodName) && "(C)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf((char) frame.getIntArguments()[0]));
                    return true;
                } else if ("valueOf".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(frame.getIntArguments()[0]));
                    return true;
                } else if ("valueOf".equals(methodName) && "(J)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(DynamicUtils.getLong(frame.getIntArguments(), 0)));
                    return true;
                } else if ("valueOf".equals(methodName) && "(F)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(Float.intBitsToFloat(frame.getIntArguments()[0])));
                    return true;
                } else if ("valueOf".equals(methodName) && "(D)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(String.valueOf(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 0))));
                    return true;
                } else {
                    return false;
                }
            } else if ("System".equals(className)) {
                if ("gc".equals(methodName) && "()V".equals(methodDescriptor)) {
                    System.gc();
                    return true;
                } else if ("exit".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    System.exit(frame.getIntArguments()[0]);
                    return true;
                } else if ("arraycopy".equals(methodName) && "(Ljava/lang/Object;ILjava/lang/Object;II)V".equals(methodDescriptor)) {
                    System.arraycopy(frame.getObjectArguments()[0], frame.getIntArguments()[1], frame.getObjectArguments()[2], frame.getIntArguments()[3], frame.getIntArguments()[4]);
                    return true;
                } else if ("getProperty".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(System.getProperty((String) frame.getObjectArguments()[0]));
                    return true;
                } else if ("identityHashCode".equals(methodName) && "(Ljava/lang/Object;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(System.identityHashCode(frame.getObjectArguments()[0]));
                    return true;
                } else if ("currentTimeMillis".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(System.currentTimeMillis());
                    return true;
                } else {
                    return false;
                }
            }
        } else if ("java/io".equals(packageName)) {
            if ("DataInputStream".equals(className) && "readUTF".equals(methodName)) {
                frame.setObjectReturn(DataInputStream.readUTF((DataInput) frame.getObjectArguments()[0]));
                return true;
            }
        }
        // }
        // replace existing classes to add special code
        if ("java/lang".equals(packageName)) {
            if ("VMThread".equals(className)) {
                if ("currentThread".equals(methodName) && "()Ljava/lang/VMThread;".equals(methodDescriptor)) {
                    frame.setObjectReturn(AbstractDVMThread.currentThread(frame));
                    return true;
                } else if ("yield".equals(methodName) && "()V".equals(methodDescriptor)) {
                    AbstractDVMThread.yield();
                    return true;
                } else if ("sleep".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    AbstractDVMThread.sleep(frame, DynamicUtils.getLong(frame.getIntArguments(), 0));
                    return true;
                } else if ("activeCount".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(AbstractDVMThread.activeCount(frame));
                    return true;
                }
            }
        } else if ("java/lang/reflect".equals(packageName)) {
            if ("Array".equals(className)) {
                if ("newInstance".equals(methodName) && "(Ljava/lang/Class;[I)Ljava/lang/Object;".equals(methodDescriptor)) {
                    Class componentType = (Class) frame.getObjectArguments()[0];
                    int[] dimensions = (int[]) frame.getObjectArguments()[1];
                    frame.setObjectReturn(multiNewArray(componentType, dimensions));
                    return true;
                }
            }
        }
        return false;
        */
    }

    public boolean handleConstructor(final IDroidefenseFrame frame, final String absoluteClassName, final String methodName, final String methodDescriptor) throws Exception {
        //HANDLE CONSTRUCTOR FOR TAINTED CLASS ONLY
        IDroidefenseClass dclass = DexClassReader.getInstance().load(absoluteClassName);
        if (dclass instanceof DVMTaintClass) {
            DVMTaintClass tainted = (DVMTaintClass) dclass;
            replaceObjects(frame, frame.getObjectArguments()[0], tainted);
            return true;
        }

        // CONSTRUCTOR SECTION {
        String packageName = absoluteClassName.substring(0, absoluteClassName.lastIndexOf('/'));
        String className = absoluteClassName.substring(absoluteClassName.lastIndexOf('/') + 1);
        replaceObjects(frame, null, new Object());
        return true;
        /*AutomaticClassWriter writer = new AutomaticClassWriter(className, packageName);
        writer.create();
		Object newClass = writer.instantiate();
		replaceObjects(frame, frame.getObjectArguments()[0], newClass);
		return true;

		//if matches, try to load java jdk cls by reflectivity.
		//load cls by reflectivity


		try {
			// Create a new JavaClassLoader
			AbstractDexClassLoader classLoader = this.getClass().getClassLoader();

			// Load the target cls using its binary name
			String cn = (packageName+"/"+className).replace("/", ".");
			Log.write(LoggerType.INFO, this.mainClassName+" --> NEW "+cn);
			Log.write(LoggerType.INFO, "Loading cls "+cn.toUpperCase()+" from JDK");
			Class loadedMyClass = classLoader.load(cn);

			// Create a new instance from the loaded cls
			Constructor constructor = loadedMyClass.getConstructor();
			if(cn.equalsIgnoreCase("java.lang.Object")){
				Object myClassObject = constructor.newInstance();
				replaceObjects(frame, null, myClassObject);
				return true;
			}
			else{
				Object myClassObject = constructor.newInstance(frame.getObjectArguments());
				replaceObjects(frame, frame.getObjectArguments()[0], myClassObject);
				return true;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
		//if current cls is not part of jdk, keep looking!
		if ("java/util".equals(packageName)) {
			if ("Date".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Date());
					return true;
				} else if ("(J)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Date(DynamicUtils.getLong(frame.getIntArguments(), 1)));
					return true;
				} else {
					return false;
				}
			} else if ("EmptyStackException".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new EmptyStackException());
				return true;
			} else if ("Hashtable".equals(className)) {
				if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Hashtable(frame.getIntArguments()[1]));
					return true;
				} else if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Hashtable());
					return true;
				} else {
					return false;
				}
			} else if ("NoSuchElementException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NoSuchElementException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NoSuchElementException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Random".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Random());
					return true;
				} else if ("(J)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Random(DynamicUtils.getLong(frame.getIntArguments(), 1)));
					return true;
				} else {
					return false;
				}
			} else if ("Stack".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Stack());
				return true;
			} else if ("Vector".equals(className)) {
				if ("(II)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Vector(frame.getIntArguments()[1], frame.getIntArguments()[2]));
					return true;
				} else if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Vector(frame.getIntArguments()[1]));
					return true;
				} else if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Vector());
					return true;
				} else {
					return false;
				}
			}
		} else if ("java/lang/ref".equals(packageName)) {
			if ("WeakReference".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new WeakReference(frame.getObjectArguments()[1]));
				return true;
			}
		} else if ("java/lang".equals(packageName)) {
			if("Object".equals(className)){
				replaceObjects(frame, frame.getObjectArguments()[0], new Object());
				return true;
			}
			else if ("ArithmeticException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArithmeticException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArithmeticException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("ArrayIndexOutOfBoundsException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArrayIndexOutOfBoundsException());
					return true;
				} else if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArrayIndexOutOfBoundsException(frame.getIntArguments()[1]));
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArrayIndexOutOfBoundsException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("ArrayStoreException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArrayStoreException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ArrayStoreException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Boolean".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Boolean(frame.getIntArguments()[1] != 0));
				return true;
			} else if ("Byte".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Byte((byte) frame.getIntArguments()[1]));
				return true;
			} else if ("Character".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Character((char) frame.getIntArguments()[1]));
				return true;
			} else if ("ClassCastException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ClassCastException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ClassCastException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("ClassNotFoundException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ClassNotFoundException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ClassNotFoundException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Double".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Double(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1))));
				return true;
			} else if ("Error".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Error());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Error((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Exception".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Exception());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Exception((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Float".equals(className)) {
				if ("(F)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Float(Float.intBitsToFloat(frame.getIntArguments()[1])));
					return true;
				} else if ("(D)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Float(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1))));
					return true;
				} else {
					return false;
				}
			} else if ("IllegalAccessException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalAccessException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalAccessException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("IllegalArgumentException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalArgumentException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalArgumentException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("IllegalMonitorStateException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalMonitorStateException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalMonitorStateException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("IllegalThreadStateException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalThreadStateException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IllegalThreadStateException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("IndexOutOfBoundsException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IndexOutOfBoundsException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IndexOutOfBoundsException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("InstantiationException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InstantiationException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InstantiationException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Integer".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Integer(frame.getIntArguments()[1]));
				return true;
			} else if ("InterruptedException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InterruptedException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InterruptedException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Long".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Long(DynamicUtils.getLong(frame.getIntArguments(), 1)));
				return true;
			} else if ("NegativeArraySizeException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NegativeArraySizeException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NegativeArraySizeException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("NoClassDefFoundError".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NoClassDefFoundError());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NoClassDefFoundError((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("NullPointerException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NullPointerException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NullPointerException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("NumberFormatException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NumberFormatException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new NumberFormatException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("OutOfMemoryError".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new OutOfMemoryError());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new OutOfMemoryError((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("RuntimeException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new RuntimeException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new RuntimeException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("SecurityException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new SecurityException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new SecurityException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Short".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new Short((short) frame.getIntArguments()[1]));
				return true;
			} else if ("String".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((String) frame.getObjectArguments()[1]));
					return true;
				} else if ("([C)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((char[]) frame.getObjectArguments()[1]));
					return true;
				} else if ("([CII)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
					return true;
				} else if ("([BIILjava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3], (String) frame.getObjectArguments()[4]));
					return true;
				} else if ("([BLjava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((byte[]) frame.getObjectArguments()[1], (String) frame.getObjectArguments()[2]));
					return true;
				} else if ("([BII)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
					return true;
				} else if ("([B)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((byte[]) frame.getObjectArguments()[1]));
					return true;
				} else if ("(Ljava/lang/StringBuffer;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new String((StringBuffer) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("StringBuffer".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringBuffer());
					return true;
				} else if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringBuffer(frame.getIntArguments()[1]));
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringBuffer((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("StringIndexOutOfBoundsException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringIndexOutOfBoundsException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringIndexOutOfBoundsException((String) frame.getObjectArguments()[1]));
					return true;
				} else if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new StringIndexOutOfBoundsException(frame.getIntArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("Throwable".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Throwable());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new Throwable((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			}
		} else if ("java/io".equals(packageName)) {
			if("File".equals(className)){
				if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[1], new File((String) frame.getObjectArguments()[1]));
					return true;
				}
			}
			else if ("ByteArrayInputStream".equals(className)) {
				if ("([B)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ByteArrayInputStream((byte[]) frame.getObjectArguments()[1]));
					return true;
				} else if ("([BII)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ByteArrayInputStream((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
					return true;
				} else {
					return false;
				}
			} else if ("ByteArrayOutputStream".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ByteArrayOutputStream());
					return true;
				} else if ("(I)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new ByteArrayOutputStream(frame.getIntArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("DataInputStream".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new DataInputStream((InputStream) frame.getObjectArguments()[1]));
				return true;
			} else if ("DataOutputStream".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new DataOutputStream((OutputStream) frame.getObjectArguments()[1]));
				return true;
			} else if ("EOFException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new EOFException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new EOFException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("InputStreamReader".equals(className)) {
				if ("(Ljava/io/InputStream;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InputStreamReader((InputStream) frame.getObjectArguments()[1]));
					return true;
				} else if ("(Ljava/io/InputStream;Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InputStreamReader((InputStream) frame.getObjectArguments()[1], (String) frame.getObjectArguments()[2]));
					return true;
				} else {
					return false;
				}
			} else if ("InterruptedIOException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InterruptedIOException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new InterruptedIOException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("IOException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IOException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new IOException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("OutputStreamWriter".equals(className)) {
				if ("(Ljava/io/OutputStream;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new OutputStreamWriter((OutputStream) frame.getObjectArguments()[1]));
					return true;
				} else if ("(Ljava/io/OutputStream;Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new OutputStreamWriter((OutputStream) frame.getObjectArguments()[1], (String) frame.getObjectArguments()[2]));
					return true;
				} else {
					return false;
				}
			} else if ("PrintStream".equals(className) && "<init>".equals(methodName)) {
				replaceObjects(frame, frame.getObjectArguments()[0], new PrintStream((OutputStream) frame.getObjectArguments()[1]));
				return true;
			} else if ("UnsupportedEncodingException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new UnsupportedEncodingException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new UnsupportedEncodingException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			} else if ("UTFDataFormatException".equals(className)) {
				if ("()V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new UTFDataFormatException());
					return true;
				} else if ("(Ljava/lang/String;)V".equals(methodDescriptor)) {
					replaceObjects(frame, frame.getObjectArguments()[0], new UTFDataFormatException((String) frame.getObjectArguments()[1]));
					return true;
				} else {
					return false;
				}
			}
		}
		// }
		Log.write(LoggerType.INFO, "CLASS REQUEST --> "+packageName+"/"+className);
		return false;
		/*
        if("android/app".equals(packageName)){
        	if("Activity".equals(className)){
        		System.out.println("Match found with activity.java");
        		//load cls by reflectivity
        		try {

        			// Create a new JavaClassLoader
        			AbstractDexClassLoader classLoader = this.getClass().getClassLoader();

        			// Load the target cls using its binary name
        			String cn = (packageName+"/"+className).replace("/", ".");
        			System.out.println(cn);
        	        Class loadedMyClass = classLoader.load(cn);

        	        System.out.println("Loaded cls name: " + loadedMyClass.getMethodName());

        	        // Create a new instance from the loaded cls
        	        Constructor constructor = loadedMyClass.getConstructor();
        	        Object myClassObject = constructor.newInstance();
        	        System.out.println("Replacing object");
        	        replaceObjects(frame, frame.getObjectArguments()[0], myClassObject);
        		} catch (ClassNotFoundException e) {
        			e.printStackTrace();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		return true;
        	}
        }
        // replace existing classes to add special code
        if ("java/lang".equals(packageName)) {
            if ("Object".equals(className)) {
                replaceObjects(frame, frame.getObjectArguments()[0], new Object());
                return true;
            } else if ("Thread".equals(className)) {
                if ("()V".equals(methodDescriptor)) {
                    replaceObjects(frame, frame.getObjectArguments()[0], new DalvikWorker(frame.getThread().getVirtualMachine(), "thread"));
                    return true;
                } else if ("(Ljava/lang/Runnable;)V".equals(methodDescriptor)) {
                    replaceObjects(frame, frame.getObjectArguments()[0], new DalvikWorker(frame.getThread().getVirtualMachine(), (DalvikInstance) frame.getObjectArguments()[1]));
                    return true;
                } else if ("(Ljava/lang/Runnable;Ljava/lang/String;)V".equals(methodDescriptor)) {
                    replaceObjects(frame, frame.getObjectArguments()[0], new DalvikWorker(frame.getThread().getVirtualMachine(), (DalvikInstance) frame.getObjectArguments()[1], (String) frame.getObjectArguments()[2]));
                    return true;
                }
            }
        }
        System.out.println("\nUsed package name in that DalvikInstruction was not implemented.\nPackage name: "+packageName+"\nClass name: "+className+"\nMethod name: "+methodName);
        replaceObjects(frame, frame.getObjectArguments()[0], new Object());
        throw new VirtualMachineRuntimeException("\nUsed package name in that DalvikInstruction was not implemented.\nPackage name: "+packageName+"\nClass name: "+className+"\nMethod name: "+methodName);
        //		return false;
		 */

    }

    public boolean handleInstanceFieldGetter(final IDroidefenseFrame frame, final String absoluteClassName, final String fieldName, final String fieldDescriptor, final int register) {
        // INSTANCE FIELD SECTION {
        // }
        return false;
    }

    public boolean handleInstanceMethod(final IDroidefenseFrame frame, final String absoluteClassName, final String methodName, final String methodDescriptor) throws Exception {
        IDroidefenseClass dclass = DexClassReader.getInstance().load(absoluteClassName);
        if (dclass instanceof EncapsulatedClass) {
            EncapsulatedClass tainted = (EncapsulatedClass) dclass;
            IDroidefenseMethod method = tainted.getMethod(methodName, methodDescriptor, true);
            String returnType = method.getReturnType();
            switch (returnType) {
                case TypeDescriptorSemantics.DESC_RESOLVED_V:
                    //void
                    //do nothing
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_Z:
                    if (tainted.isReflected()) {
                        tainted.getDirectMethod(methodName, methodDescriptor, true);
                        frame.setSingleReturn((int) tainted.getJavaObject());
                    } else {
                        frame.setSingleReturn(0);
                    }
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_B:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_S:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_C:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_I:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_J:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_F:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_D:
                    break;
                case TypeDescriptorSemantics.DESC_RESOLVED_CLASSNAME:
                    String returnClass = methodDescriptor.replace("()L", "").replace(";", "");
                    IDroidefenseClass cl = DexClassReader.getInstance().load(returnClass);
                    frame.setObjectReturn(toTargetInstance(cl));
                    break;
            }
        }
        return true;
        /*
        String packageName = absoluteClassName.substring(0, absoluteClassName.lastIndexOf('/'));
        String className = absoluteClassName.substring(absoluteClassName.lastIndexOf('/') + 1);
        if ("java/util".equals(packageName)) {
            if ("Calendar".equals(className)) {
                if ("get".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Calendar) toTargetInstance(frame.getObjectArguments()[0])).get(frame.getIntArguments()[1]));
                    return true;
                } else if ("set".equals(methodName) && "(II)V".equals(methodDescriptor)) {
                    ((Calendar) toTargetInstance(frame.getObjectArguments()[0])).set(frame.getIntArguments()[1], frame.getIntArguments()[2]);
                    return true;
                } else if ("after".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Calendar) toTargetInstance(frame.getObjectArguments()[0])).after(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("before".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Calendar) toTargetInstance(frame.getObjectArguments()[0])).before(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("getTime".equals(methodName) && "()Ljava/util/Date;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Calendar) toTargetInstance(frame.getObjectArguments()[0])).getTime());
                    return true;
                } else if ("setTime".equals(methodName) && "(Ljava/util/Date;)V".equals(methodDescriptor)) {
                    ((Calendar) toTargetInstance(frame.getObjectArguments()[0])).setTime((Date) frame.getObjectArguments()[1]);
                    return true;
                } else if ("setTimeZone".equals(methodName) && "(Ljava/util/TimeZone;)V".equals(methodDescriptor)) {
                    ((Calendar) toTargetInstance(frame.getObjectArguments()[0])).setTimeZone((TimeZone) frame.getObjectArguments()[1]);
                    return true;
                } else if ("getTimeZone".equals(methodName) && "()Ljava/util/TimeZone;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Calendar) toTargetInstance(frame.getObjectArguments()[0])).getTimeZone());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Date".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("getTime".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Date) toTargetInstance(frame.getObjectArguments()[0])).getTime());
                    return true;
                } else if ("setTime".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((Date) toTargetInstance(frame.getObjectArguments()[0])).setTime(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).hashCode());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("EmptyStackException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("Hashtable".equals(className)) {
                if ("get".equals(methodName) && "(Ljava/lang/Object;)Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).get(frame.getObjectArguments()[1]));
                    return true;
                } else if ("put".equals(methodName) && "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).put(frame.getObjectArguments()[1], frame.getObjectArguments()[2]));
                    return true;
                } else if ("size".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).size());
                    return true;
                } else if ("keys".equals(methodName) && "()Ljava/util/Enumeration;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).keys());
                    return true;
                } else if ("clear".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).clear();
                    return true;
                } else if ("remove".equals(methodName) && "(Ljava/lang/Object;)Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).remove(frame.getObjectArguments()[1]));
                    return true;
                } else if ("isEmpty".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).isEmpty() ? 1 : 0);
                    return true;
                } else if ("elements".equals(methodName) && "()Ljava/util/Enumeration;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).elements());
                    return true;
                } else if ("contains".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).contains(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                } else if ("containsKey".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Hashtable) toTargetInstance(frame.getObjectArguments()[0])).containsKey(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("NoSuchElementException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("Random".equals(className)) {
                if ("setSeed".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((Random) toTargetInstance(frame.getObjectArguments()[0])).setSeed(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("nextInt".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Random) toTargetInstance(frame.getObjectArguments()[0])).nextInt());
                    return true;
                } else if ("nextInt".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Random) toTargetInstance(frame.getObjectArguments()[0])).nextInt(frame.getIntArguments()[1]));
                    return true;
                } else if ("nextLong".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Random) toTargetInstance(frame.getObjectArguments()[0])).nextLong());
                    return true;
                } else if ("nextFloat".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((Random) toTargetInstance(frame.getObjectArguments()[0])).nextFloat()));
                    return true;
                } else if ("nextDouble".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((Random) toTargetInstance(frame.getObjectArguments()[0])).nextDouble()));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Stack".equals(className)) {
                if ("pop".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Stack) toTargetInstance(frame.getObjectArguments()[0])).pop());
                    return true;
                } else if ("push".equals(methodName) && "(Ljava/lang/Object;)Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Stack) toTargetInstance(frame.getObjectArguments()[0])).push(frame.getObjectArguments()[1]));
                    return true;
                } else if ("peek".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Stack) toTargetInstance(frame.getObjectArguments()[0])).peek());
                    return true;
                } else if ("empty".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Stack) toTargetInstance(frame.getObjectArguments()[0])).empty() ? 1 : 0);
                    return true;
                } else if ("search".equals(methodName) && "(Ljava/lang/Object;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Stack) toTargetInstance(frame.getObjectArguments()[0])).search(frame.getObjectArguments()[1]));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/util/Vector", methodName, methodDescriptor);
                }
            } else if ("TimeZone".equals(className)) {
                if ("getID".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((TimeZone) toTargetInstance(frame.getObjectArguments()[0])).getID());
                    return true;
                } else if ("getOffset".equals(methodName) && "(IIIIII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((TimeZone) toTargetInstance(frame.getObjectArguments()[0])).getOffset(frame.getIntArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3], frame.getIntArguments()[4], frame.getIntArguments()[5], frame.getIntArguments()[6]));
                    return true;
                } else if ("getRawOffset".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((TimeZone) toTargetInstance(frame.getObjectArguments()[0])).getRawOffset());
                    return true;
                } else if ("useDaylightTime".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((TimeZone) toTargetInstance(frame.getObjectArguments()[0])).useDaylightTime() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Vector".equals(className)) {
                if ("size".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).size());
                    return true;
                } else if ("setSize".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).setSize(frame.getIntArguments()[1]);
                    return true;
                } else if ("isEmpty".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).isEmpty() ? 1 : 0);
                    return true;
                } else if ("indexOf".equals(methodName) && "(Ljava/lang/Object;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).indexOf(frame.getObjectArguments()[1]));
                    return true;
                } else if ("indexOf".equals(methodName) && "(Ljava/lang/Object;I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).indexOf(frame.getObjectArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("copyInto".equals(methodName) && "([Ljava/lang/Object;)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).copyInto((Object[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("capacity".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).capacity());
                    return true;
                } else if ("elements".equals(methodName) && "()Ljava/util/Enumeration;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).elements());
                    return true;
                } else if ("contains".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).contains(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                } else if ("elementAt".equals(methodName) && "(I)Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).elementAt(frame.getIntArguments()[1]));
                    return true;
                } else if ("trimToSize".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).trimToSize();
                    return true;
                } else if ("addElement".equals(methodName) && "(Ljava/lang/Object;)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).addElement(frame.getObjectArguments()[1]);
                    return true;
                } else if ("lastIndexOf".equals(methodName) && "(Ljava/lang/Object;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).lastIndexOf(frame.getObjectArguments()[1]));
                    return true;
                } else if ("lastIndexOf".equals(methodName) && "(Ljava/lang/Object;I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).lastIndexOf(frame.getObjectArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("lastElement".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).lastElement());
                    return true;
                } else if ("firstElement".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).firstElement());
                    return true;
                } else if ("setElementAt".equals(methodName) && "(Ljava/lang/Object;I)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).setElementAt(frame.getObjectArguments()[1], frame.getIntArguments()[2]);
                    return true;
                } else if ("removeElement".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Vector) toTargetInstance(frame.getObjectArguments()[0])).removeElement(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("ensureCapacity".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).ensureCapacity(frame.getIntArguments()[1]);
                    return true;
                } else if ("removeElementAt".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).removeElementAt(frame.getIntArguments()[1]);
                    return true;
                } else if ("insertElementAt".equals(methodName) && "(Ljava/lang/Object;I)V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).insertElementAt(frame.getObjectArguments()[1], frame.getIntArguments()[2]);
                    return true;
                } else if ("removeAllElements".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Vector) toTargetInstance(frame.getObjectArguments()[0])).removeAllElements();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            }
        } else if ("java/lang/ref".equals(packageName)) {
            if ("Reference".equals(className)) {
                if ("get".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Reference) toTargetInstance(frame.getObjectArguments()[0])).get());
                    return true;
                } else if ("clear".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Reference) toTargetInstance(frame.getObjectArguments()[0])).clear();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("WeakReference".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/ref/Reference", methodName, methodDescriptor);
            }
        } else if ("java/lang".equals(packageName)) {
            if ("ArithmeticException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("ArrayIndexOutOfBoundsException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/IndexOutOfBoundsException", methodName, methodDescriptor);
            } else if ("ArrayStoreException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("Boolean".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).hashCode());
                    return true;
                } else if ("booleanValue".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Boolean) toTargetInstance(frame.getObjectArguments()[0])).booleanValue() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Byte".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).hashCode());
                    return true;
                } else if ("byteValue".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Byte) toTargetInstance(frame.getObjectArguments()[0])).byteValue());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Character".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Character) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Character) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Character) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("charValue".equals(methodName) && "()C".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Character) toTargetInstance(frame.getObjectArguments()[0])).charValue());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Class".equals(className)) {
                if ("isArray".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).isArray() ? 1 : 0);
                    return true;
                } else if ("getMethodName".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).getMethodName());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("isInstance".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).isInstance(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("newInstance".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).newInstance());
                    return true;
                } else if ("isInterface".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).isInterface() ? 1 : 0);
                    return true;
                } else if ("isAssignableFrom".equals(methodName) && "(Ljava/lang/Class;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).isAssignableFrom((Class) frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("getResourceAsStream".equals(methodName) && "(Ljava/lang/String;)Ljava/io/InputStream;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Class) toTargetInstance(frame.getObjectArguments()[0])).getResourceAsStream((String) frame.getObjectArguments()[1]));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("ClassCastException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("ClassNotFoundException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("Double".equals(className)) {
                if ("isNaN".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).isNaN() ? 1 : 0);
                    return true;
                } else if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("intValue".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).intValue());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("byteValue".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).byteValue());
                    return true;
                } else if ("longValue".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).longValue());
                    return true;
                } else if ("isInfinite".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).isInfinite() ? 1 : 0);
                    return true;
                } else if ("shortValue".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Double) toTargetInstance(frame.getObjectArguments()[0])).shortValue());
                    return true;
                } else if ("floatValue".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((Double) toTargetInstance(frame.getObjectArguments()[0])).floatValue()));
                    return true;
                } else if ("doubleValue".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((Double) toTargetInstance(frame.getObjectArguments()[0])).doubleValue()));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Error".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Throwable", methodName, methodDescriptor);
            } else if ("Exception".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Throwable", methodName, methodDescriptor);
            } else if ("Float".equals(className)) {
                if ("isNaN".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).isNaN() ? 1 : 0);
                    return true;
                } else if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("intValue".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).intValue());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("byteValue".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).byteValue());
                    return true;
                } else if ("longValue".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).longValue());
                    return true;
                } else if ("isInfinite".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).isInfinite() ? 1 : 0);
                    return true;
                } else if ("shortValue".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Float) toTargetInstance(frame.getObjectArguments()[0])).shortValue());
                    return true;
                } else if ("floatValue".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((Float) toTargetInstance(frame.getObjectArguments()[0])).floatValue()));
                    return true;
                } else if ("doubleValue".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((Float) toTargetInstance(frame.getObjectArguments()[0])).doubleValue()));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("IllegalAccessException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("IllegalArgumentException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("IllegalMonitorStateException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("IllegalThreadStateException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/IllegalArgumentException", methodName, methodDescriptor);
            } else if ("IndexOutOfBoundsException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("InstantiationException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("Integer".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("intValue".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).intValue());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("byteValue".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).byteValue());
                    return true;
                } else if ("longValue".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).longValue());
                    return true;
                } else if ("shortValue".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Integer) toTargetInstance(frame.getObjectArguments()[0])).shortValue());
                    return true;
                } else if ("floatValue".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((Integer) toTargetInstance(frame.getObjectArguments()[0])).floatValue()));
                    return true;
                } else if ("doubleValue".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((Integer) toTargetInstance(frame.getObjectArguments()[0])).doubleValue()));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("InterruptedException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("Long".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Long) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Long) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Long) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("longValue".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Long) toTargetInstance(frame.getObjectArguments()[0])).longValue());
                    return true;
                } else if ("floatValue".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((Long) toTargetInstance(frame.getObjectArguments()[0])).floatValue()));
                    return true;
                } else if ("doubleValue".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((Long) toTargetInstance(frame.getObjectArguments()[0])).doubleValue()));
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("Math".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
            } else if ("NegativeArraySizeException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("NoClassDefFoundError".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Error", methodName, methodDescriptor);
            } else if ("NullPointerException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("NumberFormatException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/IllegalArgumentException", methodName, methodDescriptor);
            } else if ("OutOfMemoryError".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/VirtualMachineError", methodName, methodDescriptor);
            } else if ("Runtime".equals(className)) {
                if ("gc".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Runtime) toTargetInstance(frame.getObjectArguments()[0])).gc();
                    return true;
                } else if ("exit".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Runtime) toTargetInstance(frame.getObjectArguments()[0])).exit(frame.getIntArguments()[1]);
                    return true;
                } else if ("freeMemory".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Runtime) toTargetInstance(frame.getObjectArguments()[0])).freeMemory());
                    return true;
                } else if ("totalMemory".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Runtime) toTargetInstance(frame.getObjectArguments()[0])).totalMemory());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("RuntimeException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("SecurityException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/RuntimeException", methodName, methodDescriptor);
            } else if ("Short".equals(className)) {
                if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Short) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Short) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Short) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("shortValue".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Short) toTargetInstance(frame.getObjectArguments()[0])).shortValue());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("String".equals(className)) {
                if ("trim".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).trim());
                    return true;
                } else if ("length".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).length());
                    return true;
                } else if ("charAt".equals(methodName) && "(I)C".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).charAt(frame.getIntArguments()[1]));
                    return true;
                } else if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).equals(frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("concat".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).concat((String) frame.getObjectArguments()[1]));
                    return true;
                } else if ("intern".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).intern());
                    return true;
                } else if ("indexOf".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).indexOf(frame.getIntArguments()[1]));
                    return true;
                } else if ("indexOf".equals(methodName) && "(II)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).indexOf(frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("indexOf".equals(methodName) && "(Ljava/lang/String;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).indexOf((String) frame.getObjectArguments()[1]));
                    return true;
                } else if ("indexOf".equals(methodName) && "(Ljava/lang/String;I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).indexOf((String) frame.getObjectArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("replace".equals(methodName) && "(CC)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).replace((char) frame.getIntArguments()[1], (char) frame.getIntArguments()[2]));
                    return true;
                } else if ("getChars".equals(methodName) && "(II[CI)V".equals(methodDescriptor)) {
                    ((String) toTargetInstance(frame.getObjectArguments()[0])).getChars(frame.getIntArguments()[1], frame.getIntArguments()[2], (char[]) frame.getObjectArguments()[3], frame.getIntArguments()[4]);
                    return true;
                } else if ("getBytes".equals(methodName) && "(Ljava/lang/String;)[B".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).getBytes((String) frame.getObjectArguments()[1]));
                    return true;
                } else if ("getBytes".equals(methodName) && "()[B".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).getBytes());
                    return true;
                } else if ("endsWith".equals(methodName) && "(Ljava/lang/String;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).endsWith((String) frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).hashCode());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])));
                    return true;
                } else if ("compareTo".equals(methodName) && "(Ljava/lang/String;)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).compareTo((String) frame.getObjectArguments()[1]));
                    return true;
                } else if ("substring".equals(methodName) && "(I)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).substring(frame.getIntArguments()[1]));
                    return true;
                } else if ("substring".equals(methodName) && "(II)Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).substring(frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("startsWith".equals(methodName) && "(Ljava/lang/String;I)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).startsWith((String) frame.getObjectArguments()[1], frame.getIntArguments()[2]) ? 1 : 0);
                    return true;
                } else if ("startsWith".equals(methodName) && "(Ljava/lang/String;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).startsWith((String) frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else if ("lastIndexOf".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).lastIndexOf(frame.getIntArguments()[1]));
                    return true;
                } else if ("lastIndexOf".equals(methodName) && "(II)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).lastIndexOf(frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("toLowerCase".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).toLowerCase());
                    return true;
                } else if ("toUpperCase".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).toUpperCase());
                    return true;
                } else if ("toCharArray".equals(methodName) && "()[C".equals(methodDescriptor)) {
                    frame.setObjectReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).toCharArray());
                    return true;
                } else if ("regionMatches".equals(methodName) && "(ZILjava/lang/String;II)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).regionMatches(frame.getIntArguments()[1] != 0, frame.getIntArguments()[2], (String) frame.getObjectArguments()[3], frame.getIntArguments()[4], frame.getIntArguments()[5]) ? 1 : 0);
                    return true;
                } else if ("equalsIgnoreCase".equals(methodName) && "(Ljava/lang/String;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((String) toTargetInstance(frame.getObjectArguments()[0])).equalsIgnoreCase((String) frame.getObjectArguments()[1]) ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("StringBuffer".equals(className)) {
                if ("length".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).length());
                    return true;
                } else if ("charAt".equals(methodName) && "(I)C".equals(methodDescriptor)) {
                    frame.setSingleReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).charAt(frame.getIntArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "(Ljava/lang/Object;)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(frame.getObjectArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "(Ljava/lang/String;)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append((String) frame.getObjectArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "([C)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append((char[]) frame.getObjectArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "([CII)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("append".equals(methodName) && "(Z)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(frame.getIntArguments()[1] != 0));
                    return true;
                } else if ("append".equals(methodName) && "(C)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append((char) frame.getIntArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "(I)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(frame.getIntArguments()[1]));
                    return true;
                } else if ("append".equals(methodName) && "(J)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("append".equals(methodName) && "(F)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(Float.intBitsToFloat(frame.getIntArguments()[1])));
                    return true;
                } else if ("append".equals(methodName) && "(D)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).append(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1))));
                    return true;
                } else if ("delete".equals(methodName) && "(II)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).delete(frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(ILjava/lang/Object;)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], frame.getObjectArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(ILjava/lang/String;)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], (String) frame.getObjectArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(I[C)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], (char[]) frame.getObjectArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(IZ)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], frame.getIntArguments()[2] != 0));
                    return true;
                } else if ("insert".equals(methodName) && "(IC)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], (char) frame.getIntArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(II)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], frame.getIntArguments()[2]));
                    return true;
                } else if ("insert".equals(methodName) && "(IJ)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], DynamicUtils.getLong(frame.getIntArguments(), 2)));
                    return true;
                } else if ("insert".equals(methodName) && "(IF)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], Float.intBitsToFloat(frame.getIntArguments()[2])));
                    return true;
                } else if ("insert".equals(methodName) && "(ID)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).insert(frame.getIntArguments()[1], Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 2))));
                    return true;
                } else if ("reverse".equals(methodName) && "()Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).reverse());
                    return true;
                } else if ("capacity".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).capacity());
                    return true;
                } else if ("getChars".equals(methodName) && "(II[CI)V".equals(methodDescriptor)) {
                    ((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).getChars(frame.getIntArguments()[1], frame.getIntArguments()[2], (char[]) frame.getObjectArguments()[3], frame.getIntArguments()[4]);
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("setSignatureLength".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).setLength(frame.getIntArguments()[1]);
                    return true;
                } else if ("setCharAt".equals(methodName) && "(IC)V".equals(methodDescriptor)) {
                    ((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).setCharAt(frame.getIntArguments()[1], (char) frame.getIntArguments()[2]);
                    return true;
                } else if ("deleteCharAt".equals(methodName) && "(I)Ljava/lang/StringBuffer;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).deleteCharAt(frame.getIntArguments()[1]));
                    return true;
                } else if ("ensureCapacity".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((StringBuffer) toTargetInstance(frame.getObjectArguments()[0])).ensureCapacity(frame.getIntArguments()[1]);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("StringIndexOutOfBoundsException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/IndexOutOfBoundsException", methodName, methodDescriptor);
            } else if ("System".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
            } else if ("Throwable".equals(className)) {
                if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Throwable) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("getMessage".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Throwable) toTargetInstance(frame.getObjectArguments()[0])).getMessage());
                    return true;
                } else if ("printStackTrace".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Throwable) toTargetInstance(frame.getObjectArguments()[0])).printStackTrace();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("VirtualMachineError".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Error", methodName, methodDescriptor);
            }
        } else if ("java/io".equals(packageName)) {
            if ("ByteArrayInputStream".equals(className)) {
                if ("read".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).read());
                    return true;
                } else if ("read".equals(methodName) && "([BII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).read((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("skip".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).skip(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("mark".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).mark(frame.getIntArguments()[1]);
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("available".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).available());
                    return true;
                } else if ("markSupported".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((ByteArrayInputStream) toTargetInstance(frame.getObjectArguments()[0])).markSupported() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/InputStream", methodName, methodDescriptor);
                }
            } else if ("ByteArrayOutputStream".equals(className)) {
                if ("size".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).size());
                    return true;
                } else if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).toString());
                    return true;
                } else if ("toByteArray".equals(methodName) && "()[B".equals(methodDescriptor)) {
                    frame.setObjectReturn(((ByteArrayOutputStream) toTargetInstance(frame.getObjectArguments()[0])).toByteArray());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/OutputStream", methodName, methodDescriptor);
                }
            } else if ("DataInputStream".equals(className)) {
                if ("read".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).read());
                    return true;
                } else if ("read".equals(methodName) && "([B)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).read((byte[]) frame.getObjectArguments()[1]));
                    return true;
                } else if ("read".equals(methodName) && "([BII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).read((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("skip".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).skip(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("mark".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).mark(frame.getIntArguments()[1]);
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("readInt".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readInt());
                    return true;
                } else if ("readUTF".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readUTF());
                    return true;
                } else if ("readByte".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readByte());
                    return true;
                } else if ("readChar".equals(methodName) && "()C".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readChar());
                    return true;
                } else if ("readLong".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readLong());
                    return true;
                } else if ("readFully".equals(methodName) && "([B)V".equals(methodDescriptor)) {
                    ((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readFully((byte[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("readFully".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readFully((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("skipBytes".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).skipBytes(frame.getIntArguments()[1]));
                    return true;
                } else if ("readShort".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readShort());
                    return true;
                } else if ("readFloat".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readFloat()));
                    return true;
                } else if ("available".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).available());
                    return true;
                } else if ("readDouble".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readDouble()));
                    return true;
                } else if ("readBoolean".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readBoolean() ? 1 : 0);
                    return true;
                } else if ("markSupported".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).markSupported() ? 1 : 0);
                    return true;
                } else if ("readUnsignedByte".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readUnsignedByte());
                    return true;
                } else if ("readUnsignedShort".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInputStream) toTargetInstance(frame.getObjectArguments()[0])).readUnsignedShort());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/InputStream", methodName, methodDescriptor);
                }
            } else if ("DataOutputStream".equals(className)) {
                if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("flush".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).flush();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("writeInt".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeInt(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeUTF".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeUTF((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("writeByte".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeByte(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeChar".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeChar(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeLong".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeLong(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("writeShort".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeShort(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeFloat".equals(methodName) && "(F)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeFloat(Float.intBitsToFloat(frame.getIntArguments()[1]));
                    return true;
                } else if ("writeChars".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeChars((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("writeDouble".equals(methodName) && "(D)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeDouble(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("writeBoolean".equals(methodName) && "(Z)V".equals(methodDescriptor)) {
                    ((DataOutputStream) toTargetInstance(frame.getObjectArguments()[0])).writeBoolean(frame.getIntArguments()[1] != 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/OutputStream", methodName, methodDescriptor);
                }
            } else if ("EOFException".equals(className)) {
                return handleInstanceMethod(frame, "java/io/IOException", methodName, methodDescriptor);
            } else if ("InputStream".equals(className)) {
                if ("read".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).read());
                    return true;
                } else if ("read".equals(methodName) && "([B)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).read((byte[]) frame.getObjectArguments()[1]));
                    return true;
                } else if ("read".equals(methodName) && "([BII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).read((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("skip".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).skip(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("mark".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((InputStream) toTargetInstance(frame.getObjectArguments()[0])).mark(frame.getIntArguments()[1]);
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((InputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((InputStream) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("available".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).available());
                    return true;
                } else if ("markSupported".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStream) toTargetInstance(frame.getObjectArguments()[0])).markSupported() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("InputStreamReader".equals(className)) {
                if ("read".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).read());
                    return true;
                } else if ("read".equals(methodName) && "([CII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).read((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("skip".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).skip(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("mark".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).mark(frame.getIntArguments()[1]);
                    return true;
                } else if ("ready".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).ready() ? 1 : 0);
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("markSupported".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((InputStreamReader) toTargetInstance(frame.getObjectArguments()[0])).markSupported() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/Reader", methodName, methodDescriptor);
                }
            } else if ("InterruptedIOException".equals(className)) {
                return handleInstanceMethod(frame, "java/io/IOException", methodName, methodDescriptor);
            } else if ("IOException".equals(className)) {
                return handleInstanceMethod(frame, "java/lang/Exception", methodName, methodDescriptor);
            } else if ("OutputStream".equals(className)) {
                if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((OutputStream) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([B)V".equals(methodDescriptor)) {
                    ((OutputStream) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((OutputStream) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("flush".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((OutputStream) toTargetInstance(frame.getObjectArguments()[0])).flush();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((OutputStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("OutputStreamWriter".equals(className)) {
                if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((OutputStreamWriter) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([CII)V".equals(methodDescriptor)) {
                    ((OutputStreamWriter) toTargetInstance(frame.getObjectArguments()[0])).write((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("write".equals(methodName) && "(Ljava/lang/String;II)V".equals(methodDescriptor)) {
                    ((OutputStreamWriter) toTargetInstance(frame.getObjectArguments()[0])).write((String) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("flush".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((OutputStreamWriter) toTargetInstance(frame.getObjectArguments()[0])).flush();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((OutputStreamWriter) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/Writer", methodName, methodDescriptor);
                }
            } else if ("PrintStream".equals(className)) {
                if ("flush".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).flush();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("print".equals(methodName) && "(Z)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(frame.getIntArguments()[1] != 0);
                    return true;
                } else if ("print".equals(methodName) && "(C)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print((char) frame.getIntArguments()[1]);
                    return true;
                } else if ("print".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(frame.getIntArguments()[1]);
                    return true;
                } else if ("print".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("print".equals(methodName) && "(F)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(Float.intBitsToFloat(frame.getIntArguments()[1]));
                    return true;
                } else if ("print".equals(methodName) && "(D)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("print".equals(methodName) && "([C)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print((char[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("print".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("print".equals(methodName) && "(Ljava/lang/Object;)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).print(frame.getObjectArguments()[1]);
                    return true;
                } else if ("println".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println();
                    return true;
                } else if ("println".equals(methodName) && "(Z)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(frame.getIntArguments()[1] != 0);
                    return true;
                } else if ("println".equals(methodName) && "(C)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println((char) frame.getIntArguments()[1]);
                    return true;
                } else if ("println".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(frame.getIntArguments()[1]);
                    return true;
                } else if ("println".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("println".equals(methodName) && "(F)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(Float.intBitsToFloat(frame.getIntArguments()[1]));
                    return true;
                } else if ("println".equals(methodName) && "(D)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("println".equals(methodName) && "([C)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println((char[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("println".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("println".equals(methodName) && "(Ljava/lang/Object;)V".equals(methodDescriptor)) {
                    ((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).println(frame.getObjectArguments()[1]);
                    return true;
                } else if ("checkError".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((PrintStream) toTargetInstance(frame.getObjectArguments()[0])).checkError() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/io/OutputStream", methodName, methodDescriptor);
                }
            } else if ("Reader".equals(className)) {
                if ("read".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).read());
                    return true;
                } else if ("read".equals(methodName) && "([C)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).read((char[]) frame.getObjectArguments()[1]));
                    return true;
                } else if ("read".equals(methodName) && "([CII)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).read((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]));
                    return true;
                } else if ("skip".equals(methodName) && "(J)J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).skip(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("mark".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Reader) toTargetInstance(frame.getObjectArguments()[0])).mark(frame.getIntArguments()[1]);
                    return true;
                } else if ("ready".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).ready() ? 1 : 0);
                    return true;
                } else if ("cleanThreadContext".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Reader) toTargetInstance(frame.getObjectArguments()[0])).cleanThreadContext();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Reader) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else if ("markSupported".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Reader) toTargetInstance(frame.getObjectArguments()[0])).markSupported() ? 1 : 0);
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            } else if ("UnsupportedEncodingException".equals(className)) {
                return handleInstanceMethod(frame, "java/io/IOException", methodName, methodDescriptor);
            } else if ("UTFDataFormatException".equals(className)) {
                return handleInstanceMethod(frame, "java/io/IOException", methodName, methodDescriptor);
            } else if ("Writer".equals(className)) {
                if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([C)V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).write((char[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([CII)V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).write((char[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("write".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).write((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "(Ljava/lang/String;II)V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).write((String) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("flush".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).flush();
                    return true;
                } else if ("close".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((Writer) toTargetInstance(frame.getObjectArguments()[0])).close();
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            }
        }
        // }
        // replace existing classes to add special code
        if ("android/widget".equals(packageName)) {
            if ("Toast".equals(className)) {
                //Todo
            }
        }
        if ("java/lang".equals(packageName)) {
            if ("Object".equals(className)) {
                if ("wait".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    long timeout = DynamicUtils.getLong(frame.getIntArguments(), 1);
                    getFirstWorker().getVirtualMachine().waitForNotification(frame, timeout, 0);
                    return true;
                } else if ("wait".equals(methodName) && "(JI)V".equals(methodDescriptor)) {
                    long timeout = DynamicUtils.getLong(frame.getIntArguments(), 1);
                    int nanos = frame.getIntArguments()[3];
                    getFirstWorker().getVirtualMachine().waitForNotification(frame, timeout, nanos);
                    return true;
                } else if ("wait".equals(methodName) && "()V".equals(methodDescriptor)) {
                    getFirstWorker().getVirtualMachine().waitForNotification(frame, 0, 0);
                    return true;
                } else if ("notify".equals(methodName) && "()V".equals(methodDescriptor)) {
                    getFirstWorker().getVirtualMachine().notifyToThreads(frame, false);
                    return true;
                } else if ("notifyAll".equals(methodName) && "()V".equals(methodDescriptor)) {
                    getFirstWorker().getVirtualMachine().notifyToThreads(frame, true);
                    return true;
                } else if ("equals".equals(methodName) && "(Ljava/lang/Object;)Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(DynamicUtils.toInt(toTargetInstance(frame.getObjectArguments()[0]).equals(frame.getObjectArguments()[1])));
                    return true;
                } else if ("getClass".equals(methodName) && "()Ljava/lang/Class;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).getClass());
                    return true;
                } else if ("hashCode".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(toTargetInstance(frame.getObjectArguments()[0]).hashCode());
                    return true;
                } else if ("toString".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(toTargetInstance(frame.getObjectArguments()[0]).toString());
                    return true;
                }
            } else if ("Thread".equals(className)) {
                if ("start".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DalvikWorker) frame.getObjectArguments()[0]).start();
                    return true;
                } else if ("interrupt".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DalvikWorker) frame.getObjectArguments()[0]).interrupt();
                    return true;
                } else if ("isAlive".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(DynamicUtils.toInt(((DalvikWorker) frame.getObjectArguments()[0]).isAlive()));
                    return true;
                } else if ("setPriority".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DalvikWorker) frame.getObjectArguments()[0]).setPriority(frame.getIntArguments()[1]);
                    return true;
                } else if ("getPriority".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DalvikWorker) frame.getObjectArguments()[0]).getPriority());
                    return true;
                } else if ("getMethodName".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((DalvikWorker) frame.getObjectArguments()[0]).getMethodName());
                    return true;
                } else if ("join".equals(methodName) && "()V".equals(methodDescriptor)) {
                    ((DalvikWorker) frame.getObjectArguments()[0]).join(frame.getThread());
                    return true;
                } else {
                    return handleInstanceMethod(frame, "java/lang/Object", methodName, methodDescriptor);
                }
            }
        }
        return false;
        */
    }

    public boolean handleInterfaceMethod(final IDroidefenseFrame frame, final String absoluteClassName, final String methodName, final String methodDescriptor) throws Exception {
        // INTERFACE METHOD SECTION {
        String packageName = absoluteClassName.substring(0, absoluteClassName.lastIndexOf('/'));
        String className = absoluteClassName.substring(absoluteClassName.lastIndexOf('/') + 1);
        if ("java/util".equals(packageName)) {
            if ("Enumeration".equals(className)) {
                if ("nextElement".equals(methodName) && "()Ljava/lang/Object;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((Enumeration) toTargetInstance(frame.getObjectArguments()[0])).nextElement());
                    return true;
                } else if ("hasMoreElements".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((Enumeration) toTargetInstance(frame.getObjectArguments()[0])).hasMoreElements() ? 1 : 0);
                    return true;
                } else {
                    return false;
                }
            }
        } else if ("java/lang".equals(packageName)) {
            if ("Runnable".equals(className) && "run".equals(methodName)) {
                ((Runnable) toTargetInstance(frame.getObjectArguments()[0])).run();
                return true;
            }
        } else if ("java/io".equals(packageName)) {
            if ("DataInput".equals(className)) {
                if ("readInt".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readInt());
                    return true;
                } else if ("readUTF".equals(methodName) && "()Ljava/lang/String;".equals(methodDescriptor)) {
                    frame.setObjectReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readUTF());
                    return true;
                } else if ("readByte".equals(methodName) && "()B".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readByte());
                    return true;
                } else if ("readChar".equals(methodName) && "()C".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readChar());
                    return true;
                } else if ("readLong".equals(methodName) && "()J".equals(methodDescriptor)) {
                    frame.setDoubleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readLong());
                    return true;
                } else if ("readFully".equals(methodName) && "([B)V".equals(methodDescriptor)) {
                    ((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readFully((byte[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("readFully".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readFully((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("skipBytes".equals(methodName) && "(I)I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).skipBytes(frame.getIntArguments()[1]));
                    return true;
                } else if ("readShort".equals(methodName) && "()S".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readShort());
                    return true;
                } else if ("readFloat".equals(methodName) && "()F".equals(methodDescriptor)) {
                    frame.setSingleReturn(Float.floatToIntBits(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readFloat()));
                    return true;
                } else if ("readDouble".equals(methodName) && "()D".equals(methodDescriptor)) {
                    frame.setDoubleReturn(Double.doubleToLongBits(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readDouble()));
                    return true;
                } else if ("readBoolean".equals(methodName) && "()Z".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readBoolean() ? 1 : 0);
                    return true;
                } else if ("readUnsignedByte".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readUnsignedByte());
                    return true;
                } else if ("readUnsignedShort".equals(methodName) && "()I".equals(methodDescriptor)) {
                    frame.setSingleReturn(((DataInput) toTargetInstance(frame.getObjectArguments()[0])).readUnsignedShort());
                    return true;
                } else {
                    return false;
                }
            } else if ("DataOutput".equals(className)) {
                if ("write".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).write(frame.getIntArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([B)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1]);
                    return true;
                } else if ("write".equals(methodName) && "([BII)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).write((byte[]) frame.getObjectArguments()[1], frame.getIntArguments()[2], frame.getIntArguments()[3]);
                    return true;
                } else if ("writeInt".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeInt(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeUTF".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeUTF((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("writeByte".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeByte(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeChar".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeChar(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeLong".equals(methodName) && "(J)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeLong(DynamicUtils.getLong(frame.getIntArguments(), 1));
                    return true;
                } else if ("writeShort".equals(methodName) && "(I)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeShort(frame.getIntArguments()[1]);
                    return true;
                } else if ("writeFloat".equals(methodName) && "(F)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeFloat(Float.intBitsToFloat(frame.getIntArguments()[1]));
                    return true;
                } else if ("writeChars".equals(methodName) && "(Ljava/lang/String;)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeChars((String) frame.getObjectArguments()[1]);
                    return true;
                } else if ("writeDouble".equals(methodName) && "(D)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeDouble(Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntArguments(), 1)));
                    return true;
                } else if ("writeBoolean".equals(methodName) && "(Z)V".equals(methodDescriptor)) {
                    ((DataOutput) toTargetInstance(frame.getObjectArguments()[0])).writeBoolean(frame.getIntArguments()[1] != 0);
                    return true;
                } else {
                    return false;
                }
            } else {
                frame.setSingleReturn(((Enumeration) toTargetInstance(null)).hasMoreElements() ? 1 : 0);
                return true;
            }
        }
        // }
        return false;
    }

    public Object handleNewObjectArray(final String absoluteClassName, final int dimension, final int lengthNumber, final int length1, final int length2, final int length3) {
        // NEW OBJECT ARRAY SECTION {
        // }
        switch (dimension) {
            case 1:
                return new Object[length1];
            case 2:
                switch (lengthNumber) {
                    case 1:
                        return new Object[length1][];
                    case 2:
                        return new Object[length1][length2];
                }
                break;
            case 3:
                switch (lengthNumber) {
                    case 1:
                        return new Object[length1][][];
                    case 2:
                        return new Object[length1][length2][];
                    case 3:
                        return new Object[length1][length2][length3];
                }
                break;
        }
        throw new VirtualMachineRuntimeException("not supported array type = " + absoluteClassName + getFirstWorker().getVirtualMachine().toDimesionString(dimension));
    }

    private AbstractDVMThread getFirstWorker() {
        return getThread(0);
    }

    public final Object toTargetInstance(final Object object) {
        if (object instanceof IDroidefenseInstance) {
            return ((IDroidefenseInstance) object).getParentInstance();
        } else {
            return object;
        }
    }

    public void replaceObjects(final IDroidefenseFrame frame, final Object previousObject, final Object newObject) {
        if (previousObject instanceof IDroidefenseInstance) {
            ((IDroidefenseInstance) previousObject).setParentInstance(newObject);
        } else {
            for (int i = 0, length = frame.getRegisterCount(); i < length; i++) {
                if (frame.getObjectRegisters()[i] == previousObject) {
                    frame.getObjectRegisters()[i] = newObject;
                }
            }
            for (int i = 0, length = frame.getArgumentCount(); i < length; i++) {
                if (frame.getObjectArguments()[i] == previousObject) {
                    frame.getObjectArguments()[i] = newObject;
                }
            }
        }
    }

    public Object multiNewArray(final Class componentType, final int[] dimensions) {
        try {
            if (componentType == Boolean.class) {
                switch (dimensions.length) {
                    case 1:
                        return new boolean[dimensions[0]];
                    case 2:
                        return new boolean[dimensions[0]][dimensions[1]];
                    case 3:
                        return new boolean[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[Z")) {
                switch (dimensions.length) {
                    case 1:
                        return new boolean[dimensions[0]][];
                    case 2:
                        return new boolean[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Byte.class) {
                switch (dimensions.length) {
                    case 1:
                        return new byte[dimensions[0]];
                    case 2:
                        return new byte[dimensions[0]][dimensions[1]];
                    case 3:
                        return new byte[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[B")) {
                switch (dimensions.length) {
                    case 1:
                        return new byte[dimensions[0]][];
                    case 2:
                        return new byte[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Short.class) {
                switch (dimensions.length) {
                    case 1:
                        return new short[dimensions[0]];
                    case 2:
                        return new short[dimensions[0]][dimensions[1]];
                    case 3:
                        return new short[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[S")) {
                switch (dimensions.length) {
                    case 1:
                        return new short[dimensions[0]][];
                    case 2:
                        return new short[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Integer.class) {
                switch (dimensions.length) {
                    case 1:
                        return new int[dimensions[0]];
                    case 2:
                        return new int[dimensions[0]][dimensions[1]];
                    case 3:
                        return new int[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[I")) {
                switch (dimensions.length) {
                    case 1:
                        return new int[dimensions[0]][];
                    case 2:
                        return new int[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Long.class) {
                switch (dimensions.length) {
                    case 1:
                        return new long[dimensions[0]];
                    case 2:
                        return new long[dimensions[0]][dimensions[1]];
                    case 3:
                        return new long[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[J")) {
                switch (dimensions.length) {
                    case 1:
                        return new long[dimensions[0]][];
                    case 2:
                        return new long[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Float.class) {
                switch (dimensions.length) {
                    case 1:
                        return new float[dimensions[0]];
                    case 2:
                        return new float[dimensions[0]][dimensions[1]];
                    case 3:
                        return new float[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[F")) {
                switch (dimensions.length) {
                    case 1:
                        return new float[dimensions[0]][];
                    case 2:
                        return new float[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Double.class) {
                switch (dimensions.length) {
                    case 1:
                        return new double[dimensions[0]];
                    case 2:
                        return new double[dimensions[0]][dimensions[1]];
                    case 3:
                        return new double[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[D")) {
                switch (dimensions.length) {
                    case 1:
                        return new double[dimensions[0]][];
                    case 2:
                        return new double[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Character.class) {
                switch (dimensions.length) {
                    case 1:
                        return new char[dimensions[0]];
                    case 2:
                        return new char[dimensions[0]][dimensions[1]];
                    case 3:
                        return new char[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Class.forName("[C")) {
                switch (dimensions.length) {
                    case 1:
                        return new char[dimensions[0]][];
                    case 2:
                        return new char[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Object.class) {
                switch (dimensions.length) {
                    case 1:
                        return new Object[dimensions[0]];
                    case 2:
                        return new Object[dimensions[0]][dimensions[1]];
                    case 3:
                        return new Object[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Object[].class) {
                switch (dimensions.length) {
                    case 1:
                        return new Object[dimensions[0]][];
                    case 2:
                        return new Object[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == String.class) {
                switch (dimensions.length) {
                    case 1:
                        return new String[dimensions[0]];
                    case 2:
                        return new String[dimensions[0]][dimensions[1]];
                    case 3:
                        return new String[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == String[].class) {
                switch (dimensions.length) {
                    case 1:
                        return new String[dimensions[0]][];
                    case 2:
                        return new String[dimensions[0]][dimensions[1]][];
                }
            } else if (componentType == Vector.class) {
                switch (dimensions.length) {
                    case 1:
                        return new Vector[dimensions[0]];
                    case 2:
                        return new Vector[dimensions[0]][dimensions[1]];
                    case 3:
                        return new Vector[dimensions[0]][dimensions[1]][dimensions[2]];
                }
            } else if (componentType == Vector[].class) {
                switch (dimensions.length) {
                    case 1:
                        return new Vector[dimensions[0]][];
                    case 2:
                        return new Vector[dimensions[0]][dimensions[1]][];
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // TODO Add types
        throw new IllegalArgumentException("not supported array type: " + componentType.getName());
    }
}