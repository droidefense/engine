/*
 * Developed by Koji Hisano <koji.hisano@eflow.jp>
 *
 * Copyright (C) 2009 eflow Inc. <http://www.eflow.jp/en/>
 *
 * This file is a part of Android Dalvik VM on Java.
 * http://code.google.com/p/android-dalvik-vm-on-java/
 *
 * This project is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package droidefense.om.machine.base;

import droidefense.om.machine.base.exceptions.ChangeThreadRuntimeException;
import droidefense.om.machine.base.exceptions.MachineStateEndedException;
import droidefense.om.machine.base.exceptions.VirtualMachineRuntimeException;
import droidefense.om.machine.base.struct.generic.*;
import droidefense.om.machine.base.struct.model.DVMFrame;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.om.machine.inst.InstructionReturn;
import droidefense.om.machine.reader.DexClassReader;
import droidefense.sdk.helpers.DroidDefenseEnvironment;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public abstract strictfp class AbstractDVMThread implements Serializable {

    // The upper STATUS_RUNNING status constants need to mean 'running'.
    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_END = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_JOIN = 3;
    public static final int STATUS_SLEEP = 4;
    public static final int STATUS_INTERRUPTED = 5;
    public static final int STATUS_WAIT_FOR_MONITOR = 6;
    public static final int STATUS_WAIT_FOR_NOTIFICATION = 7;
    protected static final int INSTRUCTIONS_PER_PRIORITY = 20;
    protected static DalvikInstruction[] instructions = DalvikInstruction.values();
    protected final DalvikVM vm;
    protected final DroidefenseProject currentProject;
    protected final Vector<IAtomFrame> frames = new Vector<>();
    protected final Vector monitorList = new Vector();
    protected ExecutionTimer timestamp;
    protected String name;
    protected int status = STATUS_NOT_STARTED;
    protected long wakeUpTime = 0;
    protected Object monitorToResume;
    protected int currentFrame = -1;
    protected int priority = Thread.NORM_PRIORITY;
    protected Vector joinedThreads = new Vector();

    protected transient static final DroidDefenseEnvironment environment = DroidDefenseEnvironment.getInstance();

    public AbstractDVMThread(final DroidefenseProject currentProject) {
        this.vm = currentProject.getDalvikMachine();
        this.currentProject = currentProject;
        this.name = currentProject.getProjectName();
        this.timestamp = new ExecutionTimer();
    }

    //frame operators

    protected static IAtomField getField(final boolean isStatic, final IAtomFrame frame, final String clazzName, final String fieldName, final int instance) {
        if (isStatic) {
            IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
            //class loader always will return a class. real or fake
            //no null check, but just in case
            return cls.getStaticField(fieldName);
        } else {
            Object object = frame.getObjectRegisters()[instance];
            if (object != null) {
                if (object instanceof IAtomInstance) {
                    IAtomField field = ((IAtomInstance) object).getField(clazzName, fieldName);
                    if (field != null) {
                        return field;
                    }
                }
            }
        }
        return null;
    }

    public static Object toObject(final int value) {
        return value == 0 ? null : new Object();
    }

    public static void setArguments(boolean isVirtual, IAtomFrame frame, String descriptor, int firstRegister, int range) {
        int argumentPosition = 0;
        if (isVirtual) {
            frame.setArgument(0, frame.getObjectRegisters()[firstRegister]);
            argumentPosition++;
        }
        for (int i = 1, length = descriptor.indexOf(')'); i < length; i++) {
            //TODO sure?? int register = firstRegister + argumentPosition;
            int register = firstRegister + range;
            switch (descriptor.charAt(i)) {
                case 'C':
                    //TODO implement
                    throw new NullPointerException("Not implemented");
                case 'B':
                    //TODO implement
                    throw new NullPointerException("Not implemented");
                case 'S':
                    //TODO implement
                    throw new NullPointerException("Not implemented");
                case 'I':
                    //TODO implement
                    throw new NullPointerException("Not implemented");
                case 'Z':
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'J':
                    frame.setArgument(argumentPosition, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argumentPosition += 2;
                    break;
                case 'F':
                    // Copy as int because bits data is important
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'D':
                    // Copy as long because bits data is important
                    frame.setArgument(argumentPosition, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argumentPosition += 2;
                    break;
                case 'L': {
                    frame.setArgument(argumentPosition, frame.getObjectRegisters()[register]);
                    argumentPosition++;
                    i = descriptor.indexOf(';', i);
                    break;
                }
                case '[': {
                    int startIndex = i;
                    while (i + 1 < length && descriptor.charAt(i + 1) == '[') {
                        i++;
                    }
                    i++;
                    switch (descriptor.charAt(i)) {
                        case 'C':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'B':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'S':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'I':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'Z':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'J':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'F':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'D':
                            break;
                        case 'L':
                            i = descriptor.indexOf(';', i);
                            break;
                        default:
                            throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.substring(startIndex, i + 1));
                    }
                    frame.setArgument(argumentPosition, frame.getObjectRegisters()[register]);
                    argumentPosition++;
                    break;
                }
                default:
                    throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.charAt(i));
            }
        }
    }

    public static String setArguments(final boolean isVirtual, final IAtomFrame frame, final String descriptor, final int registers) {
        //save added arguments as String list
        ArrayList<String> argList = new ArrayList<>();
        int argumentPosition = 0;
        if (isVirtual) {
            frame.setArgument(0, frame.getObjectRegisters()[registers & 0xF]);
            argumentPosition++;
        }
        int iterations = 0;
        for (int i = 1, length = descriptor.indexOf(')'); i < length; i++) {
            int register = (registers >> (argumentPosition * 4)) & 0xF;
            switch (descriptor.charAt(i)) {
                case 'C':
                    //char
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("char: " + frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'B':
                    //byte
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("byte: " + frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'S':
                    //boolean
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("short: " + frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'I':
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("int: " + frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'Z':
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("boolean: " + (frame.getIntRegisters()[register] == 1 ? "true" : "false"));
                    argumentPosition++;
                    break;
                case 'J':
                    frame.setArgument(argumentPosition, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argList.add("long: " + DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argumentPosition += 2;
                    break;
                case 'F':
                    // Copy as int because bits data is important
                    frame.setArgument(argumentPosition, frame.getIntRegisters()[register]);
                    argList.add("float: " + frame.getIntRegisters()[register]);
                    argumentPosition++;
                    break;
                case 'D':
                    // Copy as long because bits data is important
                    frame.setArgument(argumentPosition, DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argList.add("double: " + DynamicUtils.getLong(frame.getIntRegisters(), register));
                    argumentPosition += 2;
                    break;
                case 'L': {
                    Object obj = frame.getObjectRegisters()[register];
                    if (obj == null) {
                        String[] descData;
                        descData = DynamicUtils.descriptorListToClassName(descriptor);
                        obj = DexClassReader.getInstance().load(descData[iterations]);
                    }
                    frame.setArgument(argumentPosition, obj);
                    argList.add("object: " + obj.toString());
                    argumentPosition++;
                    iterations++;
                    i = descriptor.indexOf(';', i);
                    break;
                }
                case '[': {
                    int startIndex = i;
                    while (i + 1 < length && descriptor.charAt(i + 1) == '[') {
                        i++;
                    }
                    i++;
                    switch (descriptor.charAt(i)) {
                        case 'C':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'B':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'S':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'I':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'Z':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'J':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'F':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'D':
                            //TODO implement
                            throw new NullPointerException("Not implemented");
                        case 'L':
                            i = descriptor.indexOf(';', i);
                            break;
                        default:
                            throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.substring(startIndex, i + 1));
                    }
                    frame.setArgument(argumentPosition, frame.getObjectRegisters()[register]);
                    argumentPosition++;
                    iterations++;
                    break;
                }
                default:
                    throw new VirtualMachineRuntimeException("not implemented type = " + descriptor.charAt(i));
            }
        }
        if (argList.size() == 0)
            return "";
        return argList.toString().replaceAll("\\[|\\]", "").replaceAll(", ", "\t");
    }

    //DalvikInstruction executor

    public static AbstractDVMThread currentThread(final IAtomFrame frame) {
        return frame.getThread();
    }

    //dvm setter

    public static void yield() {
        throw new ChangeThreadRuntimeException();
    }

    //dvm getter

    static void sleep(final IAtomFrame frame, final long millis) {
        AbstractDVMThread thread = frame.getThread();
        thread.status = STATUS_SLEEP;
        thread.wakeUpTime = System.currentTimeMillis() + millis;
        throw new ChangeThreadRuntimeException();
    }

    //dvm setter

    public static int activeCount(final IAtomFrame frame) {
        return frame.getThread().vm.getThreadCount();
    }

    public IAtomFrame pushFrame() {
        IAtomFrame newFrame = new DVMFrame(this);
        frames.addElement(newFrame);
        return newFrame;
        /*currentFrame++;
        if (frames.size() < currentFrame + 1) {
            frames.addElement(new DVMFrame(this));
        }
        if (currentFrame >= frames.size())
            return frames.elementAt(frames.size() - 1);
        return frames.elementAt(currentFrame);
        */
    }

    public IAtomFrame getCurrentFrame() {
        /*if (currentFrame < 0) {
            return null;
        }*/
        if (frames.isEmpty())
            return null;
        return frames.get(frames.size() - 1);
    }

    public IAtomFrame popFrame() {
        if (frames.isEmpty())
            return null;
        return frames.remove(frames.size() - 1);
        //return popFrameByThrowable(null);
    }

    protected void replaceCurrentFrame(IAtomFrame newFrame) {
        if (!frames.isEmpty()) {
            frames.remove(frames.size() - 1);
            if (frames.isEmpty())
                frames.add(0, newFrame);
            else
                frames.add(frames.size() - 1, newFrame);
        }
    }


    public IAtomFrame popFrameByThrowable(final Throwable e) {
        IAtomFrame previousFrame = frames.elementAt(currentFrame--);
        boolean isChangeThreadFrame = previousFrame.isChangeThreadFrame();
        previousFrame.destroy();
        if (isChangeThreadFrame) {
            throw new ChangeThreadRuntimeException(e);
        } else if (currentFrame < 0) {
            status = STATUS_END;
            vm.removeThread(this);
            for (int i = 0; i < joinedThreads.size(); i++) {
                AbstractDVMThread thread = (AbstractDVMThread) joinedThreads.elementAt(i);
                thread.status = STATUS_RUNNING;
            }
            joinedThreads.removeAllElements();
            throw new MachineStateEndedException("Current Machine state is ended!");
        } else {
            return frames.elementAt(currentFrame);
        }
    }

    public strictfp void execute(final boolean endless) throws Throwable {
        int count = INSTRUCTIONS_PER_PRIORITY * priority;

        IAtomFrame frame = getCurrentFrame();
        IAtomMethod method = frame.getMethod();

        int[] lowerCodes = method.getOpcodes();
        int[] upperCodes = method.getRegistercodes();
        int[] codes = method.getIndex();

        while (endless || 0 < count--) {
            try {
                int instVal = lowerCodes[frame.getPc()];
                System.out.println("DalvikInstruction: 0x" + Integer.toHexString(instVal).toUpperCase());
                InstructionReturn returnValue = instructions[instVal].execute(null, this, lowerCodes, upperCodes, codes, DalvikInstruction.REAL_EXECUTION);
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
                }
            } catch (Throwable e) {
                if (e instanceof MachineStateEndedException) {
                    //there is no more code to simulate. Stop the loop
                    break;
                } else {
                    frame = handleThrowable(e, frame);
                    method = frame.getMethod();
                    lowerCodes = method.getOpcodes();
                    upperCodes = method.getRegistercodes();
                    codes = method.getIndex();
                }
            }
        }
    }

    public void setField(final IAtomFrame frame, final int source, final int destination, final int fieldIndex) {
        IAtomMethod method = frame.getMethod();
        String clazzName = method.getFieldClasses()[fieldIndex];
        String fieldName = method.getFieldNames()[fieldIndex];
        String fieldType = method.getFieldTypes()[fieldIndex];

        IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
        IAtomField[] instanceFields = cls.getInstanceFields();
        boolean isInstanceField = false;
        for (IAtomField f : instanceFields) {
            if (f.getName().equals(fieldName)) {
                isInstanceField = true;
                break;
            }
        }

        IAtomField field = getField(!isInstanceField, frame, clazzName, fieldName, destination);
        if (field != null) {
            switch (fieldType.charAt(0)) {
                case 'C':
                    //TODO
                    break;
                case 'B':
                    //TODO
                    break;
                case 'S':
                    //TODO
                    break;
                case 'I':
                    //TODO
                    break;
                case 'Z':
                    field.setIntValue(frame.getIntRegisters()[source]);
                    break;
                case 'J':
                    field.setLongValue(DynamicUtils.getLong(frame.getIntRegisters(), source));
                    break;
                case 'L':
                    field.setObjectValue(frame.getObjectRegisters()[source]);
                    break;
                case '[':
                    //TODO check
                    field.setObjectValue(frame.getObjectRegisters()[source]);
                    break;
                default:
                    throw new VirtualMachineRuntimeException("not supported field type");
            }
        } else {
            if (!isInstanceField) {
                if (!vm.handleClassFieldSetter(frame, source, clazzName, fieldName, fieldType)) {
                    throw new VirtualMachineRuntimeException("not implemented class field = " + clazzName + " - " + fieldName + " - " + fieldType);
                }
            } else {
                throw new VirtualMachineRuntimeException("not implemented instance field = " + clazzName + " - " + fieldName + " - " + fieldType);
            }
        }
    }

    public IAtomField getField(final IAtomFrame frame, final int source, final int fieldIndex, final int destination) {
        IAtomMethod method = frame.getMethod();
        String clazzName = method.getFieldClasses()[fieldIndex];
        String fieldName = method.getFieldNames()[fieldIndex];
        String fieldType = method.getFieldTypes()[fieldIndex];

        //calculate if the field is static given field name
        boolean instanceField = false;
        for (IAtomField field : method.getOwnerClass().getInstanceFields()) {
            if (instanceField)
                break;
            instanceField = field.getName().equals(fieldName);
        }
        boolean isStatic = !instanceField;
        IAtomField field = getField(isStatic, frame, clazzName, fieldName, source);
        if (field != null) {
            switch (fieldType.charAt(0)) {
                case 'C':
                    frame.getIntRegisters()[destination] = (char) field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'B':
                    //byte
                    frame.getIntRegisters()[destination] = (byte) field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'S':
                    //short
                    frame.getIntRegisters()[destination] = (short) field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'I':
                    //int. checked. ok
                    frame.getIntRegisters()[destination] = field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'Z':
                    //boolean checked. ok
                    frame.getIntRegisters()[destination] = field.getIntValue();
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'F':
                    //float. checked. ok
                    //TODO check
                    DynamicUtils.setLong(frame.getIntRegisters(), destination, field.getLongValue());
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'J':
                    //long. checked. ok
                    //TODO check
                    DynamicUtils.setLong(frame.getIntRegisters(), destination, field.getLongValue());
                    frame.getIsObjectRegister()[destination] = false;
                    break;
                case 'L':
                    //classname checked. ok
                    //TODO check
                    frame.getObjectRegisters()[destination] = field.getObjectValue();
                    frame.getIsObjectRegister()[destination] = true;
                    break;
                case '[':
                    //TODO check
                    frame.getObjectRegisters()[destination] = field.getObjectValue();
                    frame.getIsObjectRegister()[destination] = true;
                    break;
                default:
                    throw new VirtualMachineRuntimeException("not supported field type");
            }
        } else {
            /*if (isStatic) {
                if (!vm.handleClassFieldGetter(frame, clazzName, fieldName, fieldType, destination)) {
                    throw new VirtualMachineRuntimeException("not implemented class field = " + clazzName + " - " + fieldName + " - " + fieldType);
                }
            } else {
                //TODO WTF???
                //if (!com.handleInstanceFieldGetter(frame, clazzName, fieldName, fieldType, register)) {
                throw new VirtualMachineRuntimeException("not implemented instance field = " + clazzName + " - " + fieldName + " - " + fieldType);
                //}
            }*/
        }
        return field;
    }

    public boolean isInstance(final Object checked, final String type) throws ClassNotFoundException {
        if (checked == null) {
            return false;
        }
        String className = type.startsWith("L") ? type.substring(1, type.length() - 1) : type;
        IDroidefenseClass vmClass = DexClassReader.getInstance().load(className);
        if (vmClass != null) {
            if (checked instanceof IAtomInstance) {
                IDroidefenseClass instanceClazz = ((IAtomInstance) checked).getOwnerClass();
                while (vmClass != null) {
                    if (instanceClazz == vmClass) {
                        return true;
                    }
                    vmClass = DexClassReader.getInstance().load(vmClass.getSuperClass());
                }
            }
            return false;
        } else {
            Class nativeClass = Class.forName(className.replace('/', '.'));
            if (checked instanceof IAtomInstance) {
                return nativeClass.isInstance(((IAtomInstance) checked).getParentInstance());
            } else {
                return nativeClass.isInstance(checked);
            }
        }
    }

    public IAtomFrame callMethod(final boolean isVirtual, IAtomMethod method, final IAtomFrame frame) {

        IAtomFrame newFrame = pushFrame();

        Object instance = null;
        if (method.isInstance()) {
            instance = frame.getObjectRegisters()[0];
            if (isVirtual && instance != null && instance instanceof IAtomInstance) {
                // Handle override method
                method = ((IAtomInstance) instance).getOwnerClass().getVirtualMethod(method.getName(), method.getDescriptor(), true);
            }
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
            acquireLock(newFrame.getMonitor(), true);
        }

        return newFrame;
    }

    public Object handleNewArray(final String classDescriptor, final int lengthNumber, final int length1, final int length2, final int length3) {
        int dimension = 0;
        for (int i = 0; i < classDescriptor.length() && classDescriptor.charAt(i) == '['; i++) {
            dimension++;
        }
        switch (classDescriptor.charAt(dimension)) {
            case 'L':
                return vm.handleNewObjectArray(classDescriptor.substring(dimension + 1, classDescriptor.length() - 1), dimension, lengthNumber, length1, length2, length3);
            case 'B':
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
            case 'C':
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
            case 'I':
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
            case 'J':
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
            case 'F':
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
            case 'D':
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
            case 'S':
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
            case 'Z':
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

    protected IAtomFrame handleThrowable(final Throwable e, IAtomFrame frame) {
        if (e instanceof ChangeThreadRuntimeException) {
            throw (ChangeThreadRuntimeException) e;
        }
        // At the end, #popFrameByThrowable throws a ChangeThreadRuntimeException exception
        while (true) {
            IAtomMethod method = frame.getMethod();
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
                        if (vm.isSubClass(e, exceptionHandlerTypes[i])) {
                            frame.setThrowableReturn(e);
                            frame.increasePc(exceptionHandlerAddresses[i]);
                            return frame;
                        }
                    }
                }
            }
            frame = popFrameByThrowable(e);
        }
    }

    // end code is contained in #popFrame
    public void start() {
        if (status != STATUS_NOT_STARTED) {
            throw new IllegalThreadStateException();
        }
        vm.addThread(this);
        status = STATUS_RUNNING;
    }

    public void interrupt() {
        switch (status) {
            case STATUS_SLEEP:
                wakeUpTime = 0;
                status = STATUS_INTERRUPTED;
                break;
            case STATUS_JOIN:
                status = STATUS_INTERRUPTED;
                break;
            case STATUS_WAIT_FOR_NOTIFICATION:
                wakeUpTime = 0;
                acquireLock(monitorToResume, false);
                status = STATUS_INTERRUPTED;
                break;
        }
    }

    public void handleInterrupted() throws Throwable {
        // need to change the status here because #handleThrowable will throw a exception or not
        status = STATUS_RUNNING;
        handleThrowable(new InterruptedException(), getCurrentFrame());
    }

    public boolean isAlive() {
        return STATUS_RUNNING <= status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int newPriority) {
        if (priority < Thread.MIN_PRIORITY || Thread.MAX_PRIORITY < priority) {
            throw new IllegalArgumentException();
        }
        priority = newPriority;
    }

    public String getName() {
        return name;
    }

    public void join(final AbstractDVMThread caller) {
        caller.status = STATUS_JOIN;
        joinedThreads.addElement(caller);
        throw new ChangeThreadRuntimeException();
    }

    public String toString() {
        return "AbstractDVMThread[" + getName() + "," + getPriority() + "]";
    }

    public void acquireLock(final Object instance, final boolean changeThread) {
        if (isLocked(instance)) {
            status = STATUS_WAIT_FOR_MONITOR;
            monitorToResume = instance;
            if (changeThread) {
                throw new ChangeThreadRuntimeException();
            }
        } else {
            lock(instance);
        }
    }

    protected boolean isLocked(final Object instance) {
        for (int i = 0, length = vm.getThreadCount(); i < length; i++) {
            AbstractDVMThread thread = vm.getThread(i);
            if (thread != this) {
                if (thread.monitorList.contains(instance)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void lock(final Object instance) {
        monitorList.addElement(instance);
    }

    public void releaseLock(final Object instance) {
        monitorList.removeElementAt(monitorList.size() - 1);
        if (!monitorList.contains(instance)) {
            for (int i = 0, length = vm.getThreadCount(); i < length; i++) {
                AbstractDVMThread thread = vm.getThread(i);
                if (thread != this && thread.status == STATUS_WAIT_FOR_MONITOR && thread.monitorToResume == instance) {
                    thread.status = STATUS_RUNNING;
                    thread.monitorToResume = null;
                    thread.lock(instance);
                    break;
                }
            }
        }
    }

    //GETTERS AND SETTERS

    public boolean hasLock(final Object instance) {
        return monitorList.contains(instance);
    }

    public DalvikVM getVirtualMachine() {
        return vm;
    }

    public Vector getFrames() {
        return frames;
    }

    public Vector getMonitorList() {
        return monitorList;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(long wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public Object getMonitorToResume() {
        return monitorToResume;
    }

    public void setMonitorToResume(Object monitorToResume) {
        this.monitorToResume = monitorToResume;
    }

    public Vector getJoinedThreads() {
        return joinedThreads;
    }

    public void setJoinedThreads(Vector joinedThreads) {
        this.joinedThreads = joinedThreads;
    }

    public Object[] getLastMethodArgs() {
        return new Object[0];
    }

    //abstract methods

    public abstract void preload();

    public abstract void run() throws Throwable;

    public abstract void finish();

    public abstract IAtomMethod[] getInitialMethodToRun(IDroidefenseClass clazz);

    public abstract int getInitialArgumentCount(IDroidefenseClass cls, IAtomMethod m);

    public abstract Object getInitialArguments(IDroidefenseClass cls, IAtomMethod m);

    public abstract IDroidefenseClass[] getInitialDVMClass();

    public abstract AbstractDVMThread reset();

    public void removeFrames() {
        this.frames.clear();
    }
}
