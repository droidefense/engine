package droidefense.emulator.machine.inst;

import droidefense.emulator.flow.base.NodeGenerator;
import droidefense.emulator.machine.base.AbstractDVMThread;
import droidefense.emulator.machine.base.DynamicUtils;
import droidefense.emulator.machine.base.exceptions.VirtualMachineRuntimeException;
import droidefense.emulator.machine.base.struct.generic.*;
import droidefense.emulator.machine.base.struct.model.DVMInstance;
import droidefense.emulator.machine.reader.DexClassReader;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.nodes.ConstStrNode;
import droidefense.rulengine.nodes.FieldNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.rulengine.nodes.NormalNode;

import java.io.Serializable;

/**
 * Created by sergio on 25/3/16.
 */
public enum DalvikInstruction implements Serializable {

    DALVIK_0x0 {
        @Override
        public String description() {
            return "nop";
        }

        @Override
        public int fakePcIncrement() {
            return 0;
        }

        @Override
        public String code() {
            return "0x0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //pop current frame too
            if (frame.getMethod().isFake()) {
                /*IDroidefenseFrame previousFrame = frame.getThread().popFrame();
                IDroidefenseMethod previousMethod = previousFrame.getMethod();
                lowerCodes = previousMethod.getOpcodes();
                upperCodes = previousMethod.getRegisterOpcodes();
                codes = previousMethod.getIndex();
                return new InstructionReturn(previousFrame, previousMethod, lowerCodes, upperCodes, codes, null, null);
                */
            } else {
                // nop
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x1 {
        @Override
        public String description() {
            return "move vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x2 {
        @Override
        public String description() {
            return "move/from16 vAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move/from16 vAA, vBBBB
            int destination = upperCodes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x3 {
        @Override
        public String description() {
            return "move/16 vAAAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move/16 vAAAA, vBBBB
            frame.increasePc();
            int destination = codes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x4 {
        @Override
        public String description() {
            return "move-wide vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-wide vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIntRegisters()[destination + 1] = frame.getIntRegisters()[source + 1];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x5 {
        @Override
        public String description() {
            return "move-wide/from16 vAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-wide/from16 vAA, vBBBB
            int destination = upperCodes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIntRegisters()[destination + 1] = frame.getIntRegisters()[source + 1];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x6 {
        @Override
        public String description() {
            return "move-wide/16 vAAAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-wide/16 vAAAA, vBBBB
            frame.increasePc();
            int destination = codes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = frame.getIntRegisters()[source];
            frame.getIntRegisters()[destination + 1] = frame.getIntRegisters()[source + 1];
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x7 {
        @Override
        public String description() {
            return "move-object vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-object vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            frame.getObjectRegisters()[destination] = frame.getObjectRegisters()[source];
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0x8 {
        @Override
        public String description() {
            return "move-object/from16 vAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-object/from16 vAA, vBBBB
            int destination = upperCodes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getObjectRegisters()[destination] = frame.getObjectRegisters()[source];
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0x9 {
        @Override
        public String description() {
            return "move-object/16 vAAAA, vBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-object/16 vAAAA, vBBBB
            frame.increasePc();
            int destination = codes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            frame.getObjectRegisters()[destination] = frame.getObjectRegisters()[source];
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0xA {
        @Override
        public String description() {
            return "move-result vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //  move-result vAA
            int destination = upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = frame.getSingleReturn();
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB {
        @Override
        public String description() {
            return "move-result-wide vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-result-wide vAA
            int destination = upperCodes[frame.increasePc()];
            DynamicUtils.setLong(frame.getIntRegisters(), destination, frame.getDoubleReturn());
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC {
        @Override
        public String description() {
            return "move-result-object vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-result-object vAA
            int destination = upperCodes[frame.increasePc()];
            frame.getObjectRegisters()[destination] = frame.getObjectReturn();
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0xD {
        @Override
        public String description() {
            return "move-exception vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // move-exception vAA
            int destination = upperCodes[frame.increasePc()];
            frame.getObjectRegisters()[destination] = frame.getThrowableReturn();
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0xE {
        @Override
        public String description() {
            return "return-void";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // return-void
            IDroidefenseFrame newFrame = frame.getThread().popFrame();
            if (newFrame != null) {
                IDroidefenseMethod method = newFrame.getMethod();
                if (method != null) {
                    lowerCodes = method.getOpcodes();
                    upperCodes = method.getRegisterOpcodes();
                    codes = newFrame.getMethod().getIndex();
                    return new InstructionReturn(newFrame, method, lowerCodes, upperCodes, codes, null);
                }
            }
            return new InstructionReturn(new VirtualMachineRuntimeException("void return detected"));
        }
    },
    DALVIK_0xF {
        @Override
        public String description() {
            return "return vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // return vAA
            int result = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            
            IDroidefenseFrame newFrame = frame.getThread().popFrame();frame.setSingleReturn(result);IDroidefenseMethod method = frame.getMethod();
            lowerCodes = frame.getMethod().getOpcodes();
            upperCodes = frame.getMethod().getRegisterOpcodes();
            codes = frame.getMethod().getIndex();return new InstructionReturn(newFrame, method, lowerCodes, upperCodes, codes, null);
        }
    },
    DALVIK_0x10 {
        @Override
        public String description() {
            return "return-wide vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x10";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // return-wide vAA
            long result = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);IDroidefenseFrame newFrame = frame.getThread().popFrame();frame.setDoubleReturn(result);IDroidefenseMethod method = frame.getMethod();
            lowerCodes = frame.getMethod().getOpcodes();
            upperCodes = frame.getMethod().getRegisterOpcodes();
            codes = frame.getMethod().getIndex();return new InstructionReturn(newFrame, method, lowerCodes, upperCodes, codes, null);
        }
    },
    DALVIK_0x11 {
        @Override
        public String description() {
            return "return-object vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x11";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // return-object vAA
            Object result = frame.getObjectRegisters()[upperCodes[frame.increasePc()]];IDroidefenseFrame newFrame = frame.getThread().popFrame();frame.setObjectReturn(result);IDroidefenseMethod method = frame.getMethod();
            lowerCodes = frame.getMethod().getOpcodes();
            upperCodes = frame.getMethod().getRegisterOpcodes();
            codes = frame.getMethod().getIndex();return new InstructionReturn(newFrame, method, lowerCodes, upperCodes, codes, null);
        }
    },
    DALVIK_0x12 {
        @Override
        public String description() {
            return "const/4 vA, #+B";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x12";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const/4 vA, #+B
            int data = upperCodes[frame.increasePc()];
            int destination = data & 0xF;
            int value = (data << 24) >> 28;
            frame.getIntRegisters()[destination] = value;
            if (value == 0) {
                frame.getObjectRegisters()[destination] = null;
            }
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x13 {
        @Override
        public String description() {
            return "const/16 vAA, #+BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x13";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const/16 vAA, #+BBBB
            int destination = upperCodes[frame.increasePc()];
            int value = codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = (short) value;
            frame.getIsObjectRegister()[destination] = false;
            NormalNode node = reporting.builNormalNode(map, this, "int16:", "v" + destination + "=" + value);
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);
        }
    },
    DALVIK_0x14 {
        @Override
        public String description() {
            return "const vAA, #+BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x14";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const vAA, #+BBBBBBBB
            int destination = upperCodes[frame.increasePc()];
            int value = codes[frame.increasePc()];
            value |= codes[frame.increasePc()] << 16;
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            NormalNode node = reporting.builNormalNode(map, this, "int:", "v" + destination + "=" + value);
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);
        }
    },
    DALVIK_0x15 {
        @Override
        public String description() {
            return "const/high16 vAA, #+BBBB0000";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x15";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const/high16 vAA, #+BBBB0000
            int destination = upperCodes[frame.increasePc()];
            int value = codes[frame.increasePc()] << 16;
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x16 {
        @Override
        public String description() {
            return "const-wide/16 vAA, #+BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x16";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-wide/16 vAA, #+BBBB
            int destination = upperCodes[frame.increasePc()];
            long value = (short) codes[frame.increasePc()];
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x17 {
        @Override
        public String description() {
            return "const-wide/32 vAA, #+BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x17";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-wide/32 vAA, #+BBBBBBBB
            int destination = upperCodes[frame.increasePc()];
            long value = codes[frame.increasePc()];
            value = (int) (value | codes[frame.increasePc()] << 16);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x18 {
        @Override
        public String description() {
            return "const-wide vAA, #+BBBBBBBBBBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 5;
        }

        @Override
        public String code() {
            return "0x18";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-wide vAA, #+BBBBBBBBBBBBBBBB
            int destination = upperCodes[frame.increasePc()];
            long value = codes[frame.increasePc()];
            value |= codes[frame.increasePc()] << 16;
            value |= codes[frame.increasePc()] << 32;
            value |= codes[frame.increasePc()] << 48;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x19 {
        @Override
        public String description() {
            return "const-wide/high16 vAA, #+BBBB000000000000";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x19";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-wide/high16 vAA, #+BBBB000000000000
            int destination = upperCodes[frame.increasePc()];
            long value = (long) codes[frame.increasePc()] << 48;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x1A {
        @Override
        public String description() {
            return "const-string vAA, string@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x1A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-string vAA, string@BBBB
            int destination = upperCodes[frame.increasePc()];
            String str = frame.getMethod().getStrings()[codes[frame.increasePc()]];
            frame.getObjectRegisters()[destination] = str;
            frame.getIsObjectRegister()[destination] = true;
            ConstStrNode node = reporting.buildStringNode(map, this, frame, destination, str);
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);
        }
    },
    DALVIK_0x1B {
        @Override
        public String description() {
            return "const-string/jumbo vAA, string@BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x1B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // const-string/jumbo vAA, string@BBBBBBBB
            int destination = upperCodes[frame.increasePc()];
            int source = codes[frame.increasePc()];
            source |= codes[frame.increasePc()] << 16;
            frame.getObjectRegisters()[destination] = frame.getMethod().getStrings()[source];
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0x1C {
        @Override
        public String description() {
            return "const-class vAA, type@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x1C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //  const-class vAA, type@BBBB
            int destination = upperCodes[frame.increasePc()];
            Object value = null;
            try {
                value = frame.getThread().getVirtualMachine().handleClassGetter(frame.getMethod().getTypes()[codes[frame.increasePc()]]);
                frame.getObjectRegisters()[destination] = value;
                frame.getIsObjectRegister()[destination] = true;
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
        }
    },
    DALVIK_0x1D {
        @Override
        public String description() {
            return "monitor-enter vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x1D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // monitor-enter vAA
            Object instance = frame.getObjectRegisters()[upperCodes[frame.increasePc()]];
            if (instance == null) {
                Exception e = new NullPointerException();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
            frame.getThread().acquireLock(instance, true);
            return null;
        }
    },
    DALVIK_0x1E {
        @Override
        public String description() {
            return "monitor-exit vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x1E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // monitor-exit vAA
            Object instance = frame.getObjectRegisters()[upperCodes[frame.increasePc()]];
            if (instance == null) {
                Exception e = new NullPointerException();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
            frame.getThread().releaseLock(instance);
            return null;
        }
    },
    DALVIK_0x1F {
        @Override
        public String description() {
            return "check-cast vAA, type@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x1F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // check-cast vAA, type@BBBB
            Object checked = frame.getObjectRegisters()[upperCodes[frame.increasePc()]];
            String type = frame.getMethod().getTypes()[codes[frame.increasePc()]];
            try {
                if (checked != null && !frame.getThread().isInstance(checked, type)) {
                    Exception e = new ClassCastException();
                    return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
            return null;
        }
    },
    DALVIK_0x20 {
        @Override
        public String description() {
            return "instance-of vA, vB, type@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x20";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // instance-of vA, vB, type@CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            Object object = frame.getObjectRegisters()[upperCodes[frame.increasePc()] >> 4];
            String type = frame.getMethod().getTypes()[codes[frame.increasePc()]];
            try {
                frame.getIntRegisters()[destination] = DynamicUtils.toInt(frame.getThread().isInstance(object, type));
                frame.getIsObjectRegister()[destination] = false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
            return null;
        }
    },
    DALVIK_0x21 {
        @Override
        public String description() {
            return "array-length vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x21";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // array-length vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            Object array = frame.getObjectRegisters()[upperCodes[frame.increasePc()] >> 4];
            int value;
            if (array != null) {
                if (array instanceof boolean[]) {
                    value = ((boolean[]) array).length;
                } else if (array instanceof byte[]) {
                    value = ((byte[]) array).length;
                } else if (array instanceof short[]) {
                    value = ((short[]) array).length;
                } else if (array instanceof int[]) {
                    value = ((int[]) array).length;
                } else if (array instanceof long[]) {
                    value = ((long[]) array).length;
                } else {
                    value = ((Object[]) array).length;
                }
                frame.getIntRegisters()[destination] = value;
                frame.getIsObjectRegister()[destination] = false;
            }
            return null;
        }
    },
    DALVIK_0x22 {
        @Override
        public String description() {
            return "new-instance vAA, type@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x22";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // new-instance vAA, type@BBBB
            int destination = upperCodes[frame.increasePc()];
            int typeIndex = codes[frame.increasePc()];
            String type = frame.getMethod().getTypes()[typeIndex];
            String className = type.substring(1, type.length() - 1);
            IDroidefenseClass cls = DexClassReader.getInstance().load(className);
            if (cls != null) {
                frame.getObjectRegisters()[destination] = new DVMInstance(cls);
            } else {
                frame.getObjectRegisters()[destination] = className; // This instance will be replaced when executing invokespecial
            }
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0x23 {
        @Override
        public String description() {
            return "new-array vA, vB, type@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x23";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // new-array vA, vB, type@CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int size = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            String type = frame.getMethod().getTypes()[codes[frame.increasePc()]];frame.getObjectRegisters()[destination] = frame.getThread().handleNewArray(type, 1, size, -1, -1);
            frame.getIsObjectRegister()[destination] = true;
            return null;
        }
    },
    DALVIK_0x24 {
        @Override
        public String description() {
            return "filled-new-array {vD, vE, vF, vG, vA}, type@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x24";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // filled-new-array {vD, vE, vF, vG, vA}, type@CCCC
            int elements = upperCodes[frame.increasePc()] << 16;
            String type = frame.getMethod().getTypes()[codes[frame.increasePc()]];
            elements |= codes[frame.increasePc()];if ("[I".equals(type)) {
                int[] value = new int[elements >> 20];
                for (int i = 0, length = value.length; i < length; i++) {
                    value[i] = frame.getIntRegisters()[(elements >> (i * 4)) & 0xF];
                }
                frame.setObjectReturn(value);
            } else {
                throw new VirtualMachineRuntimeException("not supported array type: " + type);
            }
            return null;
        }
    },
    DALVIK_0x25 {
        @Override
        public String description() {
            return "filled-new-array/range {vCCCC .. vNNNN}, type@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x25";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // filled-new-array/range {vCCCC .. vNNNN}, type@BBBB
            int size = upperCodes[frame.increasePc()] << 16;
            String type = frame.getMethod().getTypes()[codes[frame.increasePc()]];
            int firstRegister = codes[frame.increasePc()];if ("[I".equals(type)) {
                int[] array = new int[size];
                for (int i = 0, length = array.length; i < length; i++) {
                    array[i] = frame.getIntRegisters()[firstRegister + i];
                }
                frame.setObjectReturn(array);
            } else {
                throw new VirtualMachineRuntimeException("not supported array type: " + type);
            }
            return null;
        }
    },
    DALVIK_0x26 {
        @Override
        public String description() {
            return "fill-array-data vAA, +BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x26";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // fill-array-data vAA, +BBBBBBBB
            Object array = frame.getObjectRegisters()[upperCodes[frame.increasePc()]];
            int offset = codes[frame.increasePc()];
            offset |= codes[frame.increasePc()] << 16;
            int address = frame.getPc() + offset - 3;
            if (codes[address] != 0x0300) {
                throw new RuntimeException("illegal array data header");
            }
            if (array != null) {
                if (array instanceof int[]) {
                    int[] intArray = (int[]) array;
                    if (codes[address + 1] != 4) {
                        throw new RuntimeException("illegal array element size");
                    }
                    int elementCount = (codes[address + 3] << 16) | codes[address + 2];
                    for (int i = 0; i < elementCount; i++) {
                        int elementAddress = address + 4 + i * 2;
                        intArray[i] = (codes[elementAddress + 1] << 16) | codes[elementAddress];
                    }
                } else {
                    throw new RuntimeException("not supported array type: " + array.getClass().getName());
                }
            }
            return null;
        }
    },
    DALVIK_0x27 {
        @Override
        public String description() {
            return "throw vAA";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x27";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // throw vAA
            Throwable throwable = (Throwable) frame.getObjectRegisters()[upperCodes[frame.increasePc()]];
            if (throwable == null) {
                Exception e = new NullPointerException();
                return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, e);
            }
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, throwable);
        }
    },
    DALVIK_0x28 {
        @Override
        public String description() {
            return "goto +AA";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 1;
        }

        @Override
        public String code() {
            return "0x28";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //do not hook function behaviour because it has a variable offset. also, it is straightfordward
            // goto +AA
            int offset = (byte) upperCodes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    frame.increasePc(frame.getPc() + offset - 1);
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x29 {
        @Override
        public String description() {
            return "goto/16 +AAAA";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 1;
        }

        @Override
        public String code() {
            return "0x29";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // goto/16 +AAAA
            //do not hook function behaviour because it has a variable offset. also, it is straightfordward
            frame.increasePc();
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    frame.increasePc(frame.getPc() + offset - 2);
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x2A {
        @Override
        public String description() {
            return "goto/32 +AAAAAAAA";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 1;
        }

        @Override
        public String code() {
            return "0x2A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // goto/32 +AAAAAAAA
            //do not hook function behaviour because it has a variable offset. also, it is straightfordward
            frame.increasePc();
            int offset = codes[frame.increasePc()];
            offset |= codes[frame.increasePc()] << 16;//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    frame.increasePc(frame.getPc() + offset - 3);
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x2B {
        @Override
        public String description() {
            return "packed-switch vAA, +BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 1;
        }

        @Override
        public String code() {
            return "0x2B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //do not hook function behaviour because it has a variable offset. also, it is straightfordward
            // packed-switch vAA, +BBBBBBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = codes[frame.increasePc()];
            offset |= codes[frame.increasePc()] << 16;int address = frame.getPc() - 3 + offset;
            // skip ident
            address += 1;
            int size = codes[address++];
            int firstValue = codes[address] | (codes[address + 1] << 16);
            address += 2;if (firstValue <= comparedValue && comparedValue < firstValue + size) {
                int index = (comparedValue - firstValue) * 2;
                frame.increasePc(frame.getPc() - 3 + (codes[address + index] | (codes[address + index + 1] << 16)));
            }
            return null;
        }
    },
    DALVIK_0x2C {
        @Override
        public String description() {
            return "sparse-switch vAA, +BBBBBBBB";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 1;
        }

        @Override
        public String code() {
            return "0x2C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sparse-switch vAA, +BBBBBBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = codes[frame.increasePc()];
            offset |= codes[frame.increasePc()] << 16;int address = frame.getPc() - 3 + offset;
            // skip ident
            address += 1;
            int size = codes[address++];
            for (int i = 0; i < size; i++) {
                int value = codes[address] | (codes[address + 1] << 16);
                if (value == comparedValue) {
                    address += size * 2;
                    frame.increasePc(frame.getPc() - 3 + (codes[address] | (codes[address + 1] << 16)));
                    break;
                }
                address += 2;
            }
            return null;
        }
    },
    DALVIK_0x2D {
        @Override
        public String description() {
            return "cmpl-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x2D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // cmpl-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            if (Float.isNaN(firstValue) || Float.isNaN(secondValue)) {
                frame.getIntRegisters()[destination] = -1;
            } else if (firstValue == secondValue) {
                frame.getIntRegisters()[destination] = 0;
            } else if (firstValue < secondValue) {
                frame.getIntRegisters()[destination] = -1;
            } else {
                frame.getIntRegisters()[destination] = 1;
            }
            return null;
        }
    },
    DALVIK_0x2E {
        @Override
        public String description() {
            return "cmpg-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x2E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // cmpg-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            if (Float.isNaN(firstValue) || Float.isNaN(secondValue)) {
                frame.getIntRegisters()[destination] = 1;
            } else if (firstValue == secondValue) {
                frame.getIntRegisters()[destination] = 0;
            } else if (firstValue < secondValue) {
                frame.getIntRegisters()[destination] = -1;
            } else {
                frame.getIntRegisters()[destination] = 1;
            }
            return null;
        }
    },
    DALVIK_0x2F {
        @Override
        public String description() {
            return "cmpl-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x2F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // cmpl-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            if (Double.isNaN(firstValue) || Double.isNaN(secondValue)) {
                frame.getIntRegisters()[destination] = -1;
            } else if (firstValue == secondValue) {
                frame.getIntRegisters()[destination] = 0;
            } else if (firstValue < secondValue) {
                frame.getIntRegisters()[destination] = -1;
            } else {
                frame.getIntRegisters()[destination] = 1;
            }
            return null;
        }
    },
    DALVIK_0x30 {
        @Override
        public String description() {
            return "cmpg-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x30";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // cmpg-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            if (Double.isNaN(firstValue) || Double.isNaN(secondValue)) {
                frame.getIntRegisters()[destination] = 1;
            } else if (firstValue == secondValue) {
                frame.getIntRegisters()[destination] = 0;
            } else if (firstValue < secondValue) {
                frame.getIntRegisters()[destination] = -1;
            } else {
                frame.getIntRegisters()[destination] = 1;
            }
            return null;
        }
    },
    DALVIK_0x31 {
        @Override
        public String description() {
            return "cmp-long vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x31";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // cmp-long vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            if (firstValue < secondValue) {
                frame.getIntRegisters()[destination] = -1;
            } else if (firstValue == secondValue) {
                frame.getIntRegisters()[destination] = 0;
            } else {
                frame.getIntRegisters()[destination] = 1;
            }
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x32 {
        @Override
        public String description() {
            return "if-eq vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc count
            return 2;
        }

        @Override
        public String code() {
            return "0x32";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-eq vA, vB, +CCCC
            int firstRegister = upperCodes[frame.getPc()] & 0xF;
            int secondRegister = upperCodes[frame.increasePc()] >> 4;
            int offset = (short) codes[frame.increasePc()];
            boolean result;
            if (frame.getIsObjectRegister()[firstRegister]) {
                if (frame.getIsObjectRegister()[secondRegister]) {
                    result = frame.getObjectRegisters()[firstRegister] == frame.getObjectRegisters()[secondRegister];
                } else {
                    result = frame.getObjectRegisters()[firstRegister] == AbstractDVMThread.toObject(frame.getIntRegisters()[secondRegister]);
                }
            } else {
                if (frame.getIsObjectRegister()[secondRegister]) {
                    result = AbstractDVMThread.toObject(frame.getIntRegisters()[firstRegister]) == frame.getObjectRegisters()[secondRegister];
                } else {
                    result = frame.getIntRegisters()[firstRegister] == frame.getIntRegisters()[secondRegister];
                }
            }//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (result) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x33 {
        @Override
        public String description() {
            return "if-ne vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x33";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-ne vA, vB, +CCCC
            int firstRegister = upperCodes[frame.getPc()] & 0xF;
            int secondRegister = upperCodes[frame.increasePc()] >> 4;
            int offset = (short) codes[frame.increasePc()];
            boolean result;
            if (frame.getIsObjectRegister()[firstRegister]) {
                if (frame.getIsObjectRegister()[secondRegister]) {
                    result = frame.getObjectRegisters()[firstRegister] != frame.getObjectRegisters()[secondRegister];
                } else {
                    result = frame.getObjectRegisters()[firstRegister] != AbstractDVMThread.toObject(frame.getIntRegisters()[secondRegister]);
                }
            } else {
                if (frame.getIsObjectRegister()[secondRegister]) {
                    result = AbstractDVMThread.toObject(frame.getIntRegisters()[firstRegister]) != frame.getObjectRegisters()[secondRegister];
                } else {
                    result = frame.getIntRegisters()[firstRegister] != frame.getIntRegisters()[secondRegister];
                }
            }//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (result) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x34 {
        @Override
        public String description() {
            return "if-lt vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x34";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-lt vA, vB, +CCCC
            int firstValue = frame.getIntRegisters()[upperCodes[frame.getPc()] & 0xF];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int offset = (short) codes[frame.increasePc()];/*TODO rethink this
            ConditionalNode node = ConditionalNode.builder(this, firstValue, secondValue, offset, offset - 2);node.setCondition(new NodeCondition() {                      @Override
                                  public boolean condition() {
                                      return firstValue < secondValue;
                                  }                      @Override
                                  public void branchTrue() {
                                      frame.increasePc(offset - 2);
                                  }                      @Override
                                  public void branchFalse() {                      }
                              }
            );
            *///return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (firstValue < secondValue) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x35 {
        @Override
        public String description() {
            return "if-ge vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x35";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-ge vA, vB, +CCCC
            int firstValue = frame.getIntRegisters()[upperCodes[frame.getPc()] & 0xF];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (firstValue >= secondValue) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x36 {
        @Override
        public String description() {
            return "if-gt vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x36";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-gt vA, vB, +CCCC
            int firstValue = frame.getIntRegisters()[upperCodes[frame.getPc()] & 0xF];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (firstValue > secondValue) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x37 {
        @Override
        public String description() {
            return "if-le vA, vB, +CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x37";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-le vA, vB, +CCCC
            int firstValue = frame.getIntRegisters()[upperCodes[frame.getPc()] & 0xF];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (firstValue <= secondValue) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x38 {
        @Override
        public String description() {
            return "if-eqz vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            //TODO variable pc increment
            return 2;
        }

        @Override
        public String code() {
            return "0x38";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-eqz vAA, +BBBB
            int comparedRegister = upperCodes[frame.increasePc()];
            int offset = (short) codes[frame.increasePc()];
            boolean result;
            if (frame.getIsObjectRegister()[comparedRegister]) {
                result = frame.getObjectRegisters()[comparedRegister] == null;
            } else {
                result = frame.getIntRegisters()[comparedRegister] == 0;
            }//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (result) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x39 {
        @Override
        public String description() {
            return "if-nez vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x39";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-nez vAA, +BBBB
            int comparedRegister = upperCodes[frame.increasePc()];
            int offset = (short) codes[frame.increasePc()];
            boolean result;
            if (frame.getIsObjectRegister()[comparedRegister]) {
                result = frame.getObjectRegisters()[comparedRegister] != null;
            } else {
                result = frame.getIntRegisters()[comparedRegister] != 0;
            }//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (result) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x3A {
        @Override
        public String description() {
            return "if-ltz vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x3A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-ltz vAA, +BBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (comparedValue < 0) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x3B {
        @Override
        public String description() {
            return "if-gez vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x3B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-gez vAA, +BBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (comparedValue >= 0) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x3C {
        @Override
        public String description() {
            return "if-gtz vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x3C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-gtz vAA, +BBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (comparedValue > 0) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x3D {
        @Override
        public String description() {
            return "if-lez vAA, +BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x3D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // if-lez vAA, +BBBB
            int comparedValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            int offset = (short) codes[frame.increasePc()];//act depending on execution environment
            switch (executionEnv) {
                case CFG_EXECUTION:
                    break;
                case REAL_EXECUTION:
                    if (comparedValue <= 0) {
                        frame.increasePc(offset - 2);
                    }
                    break;
                case MULTIPATH_EXECUTION:
                    break;
            }
            return null;
        }
    },
    DALVIK_0x3E {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x3E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x3F {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x3F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x40 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x40";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x41 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x41";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x42 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x42";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x43 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x43";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x44 {
        @Override
        public String description() {
            return "aget vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x44";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            Object array = frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            if (array == null) {
                throw new NullPointerException();
            } else if (array instanceof int[]) {
                int[] intArray = (int[]) array;
                frame.getIntRegisters()[destination] = intArray[index];
            } else if (array instanceof float[]) {
                float[] floatArray = (float[]) array;
                frame.getIntRegisters()[destination] = Float.floatToIntBits(floatArray[index]);
            } else {
                throw new VirtualMachineRuntimeException("not supported type:" + array.getClass());
            }
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x45 {
        @Override
        public String description() {
            return "aget-wide vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x45";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-wide vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            Object array = frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            if (array == null) {
                throw new NullPointerException();
            } else if (array instanceof long[]) {
                long[] longArray = (long[]) array;
                DynamicUtils.setLong(frame.getIntRegisters(), destination, longArray[index]);
            } else if (array instanceof double[]) {
                double[] doubleArray = (double[]) array;
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(doubleArray[index]));
            } else {
                throw new VirtualMachineRuntimeException("not supported type:" + array.getClass());
            }
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x46 {
        @Override
        public String description() {
            return "aget-object vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x46";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-object vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            Object[] array = (Object[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                frame.getObjectRegisters()[destination] = array[index];
                frame.getIsObjectRegister()[destination] = true;
            } else {
                //increase fake pc
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x47 {
        @Override
        public String description() {
            return "aget-boolean vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x47";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-boolean vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            boolean[] array = (boolean[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                frame.getIntRegisters()[destination] = array[index] ? 1 : 0;
                frame.getIsObjectRegister()[destination] = false;
            } else {
                //increase fake pc
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x48 {
        @Override
        public String description() {
            return "aget-byte vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x48";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-byte vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            byte[] array = (byte[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                frame.getIntRegisters()[destination] = array[index];
                frame.getIsObjectRegister()[destination] = false;
            } else {
                //increase fake pc
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x49 {
        @Override
        public String description() {
            return "aget-char vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x49";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-char vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            char[] array = (char[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                frame.getIntRegisters()[destination] = array[index];
                frame.getIsObjectRegister()[destination] = false;
            } else {
                //increase fake pc
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x4A {
        @Override
        public String description() {
            return "aget-short vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aget-short vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            short[] array = (short[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                frame.getIntRegisters()[destination] = array[index];
                frame.getIsObjectRegister()[destination] = false;
            } else {
                //increase fake pc
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0x4B {
        @Override
        public String description() {
            return "aput vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            Object array = frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            if (array == null) {
                throw new NullPointerException();
            } else if (array instanceof int[]) {
                ((int[]) array)[index] = frame.getIntRegisters()[source];
            } else if (array instanceof float[]) {
                ((float[]) array)[index] = Float.intBitsToFloat(frame.getIntRegisters()[source]);
            } else {
                throw new VirtualMachineRuntimeException("not supported type:" + array.getClass());
            }
            return null;
        }
    },
    DALVIK_0x4C {
        @Override
        public String description() {
            return "aput-wide vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-wide vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            Object array = frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            if (array == null) {
                throw new NullPointerException();
            } else if (array instanceof long[]) {
                ((long[]) array)[index] = DynamicUtils.getLong(frame.getIntRegisters(), source);
            } else if (array instanceof double[]) {
                ((double[]) array)[index] = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), source));
            } else {
                throw new VirtualMachineRuntimeException("not supported type:" + array.getClass());
            }
            return null;
        }
    },
    DALVIK_0x4D {
        @Override
        public String description() {
            return "aput-object vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-object vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            Object[] array = (Object[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            array[index] = frame.getObjectRegisters()[source];
            return null;
        }
    },
    DALVIK_0x4E {
        @Override
        public String description() {
            return "aput-boolean vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-boolean vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            boolean[] array = (boolean[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            array[index] = frame.getIntRegisters()[source] != 0;
            return null;
        }
    },
    DALVIK_0x4F {
        @Override
        public String description() {
            return "aput-byte vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x4F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-byte vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            byte[] array = (byte[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            if (array != null) {
                int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
                array[index] = (byte) frame.getIntRegisters()[source];
                return null;
            } else {
                frame.increasePc();
                return null;
            }
        }
    },
    DALVIK_0x50 {
        @Override
        public String description() {
            return "aput-char vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x50";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-char vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            char[] array = (char[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            array[index] = (char) frame.getIntRegisters()[source];
            return null;
        }
    },
    DALVIK_0x51 {
        @Override
        public String description() {
            return "aput-short vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x51";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // aput-short vAA, vBB, vCC
            int source = upperCodes[frame.increasePc()];
            short[] array = (short[]) frame.getObjectRegisters()[lowerCodes[frame.getPc()]];
            int index = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            array[index] = (short) frame.getIntRegisters()[source];
            return null;
        }
    },
    DALVIK_0x52 {
        @Override
        public String description() {
            return "iget vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x52";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x53 {
        @Override
        public String description() {
            return "iget-wide vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x53";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-wide vA, vB, field@CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x54 {
        @Override
        public String description() {
            return "iget-object vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x54";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-object vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x55 {
        @Override
        public String description() {
            return "iget-boolean vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x55";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-boolean vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x56 {
        @Override
        public String description() {
            return "iget-byte vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x56";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-byte vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x57 {
        @Override
        public String description() {
            return "iget-char vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x57";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-char vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x58 {
        @Override
        public String description() {
            return "iget-short vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x58";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iget-short vA, vB, field@CCCC
            
            int destination = upperCodes[frame.getPc()] & 0xF;
            int source = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, source, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x59 {
        @Override
        public String description() {
            return "iput vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x59";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5A {
        @Override
        public String description() {
            return "iput-wide vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-wide vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5B {
        @Override
        public String description() {
            return "iput-object vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-object vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5C {
        @Override
        public String description() {
            return "iput-boolean vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-boolean vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5D {
        @Override
        public String description() {
            return "iput-byte vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-byte vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5E {
        @Override
        public String description() {
            return "iput-char vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-char vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x5F {
        @Override
        public String description() {
            return "iput-short vA, vB, field@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x5F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // iput-short vA, vB, field@CCCC
            
            int source = upperCodes[frame.getPc()] & 0xF;
            int destination = upperCodes[frame.increasePc()] >> 4;
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, destination, fieldIndex);
            return null;
        }
    },
    DALVIK_0x60 {
        @Override
        public String description() {
            return "sget";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x60";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            IDroidefenseField field = frame.getThread().getField(frame, 0, fieldIndex, destination);
            FieldNode node = reporting.buildFieldNode(map, this, field, frame.getPc());
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);
        }
    },
    DALVIK_0x61 {
        @Override
        public String description() {
            return "sget-wide";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x61";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-wide
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, 0, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x62 {
        @Override
        public String description() {
            return "sget-object";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x62";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-object
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            IDroidefenseField field = frame.getThread().getField(frame, 0, fieldIndex, destination);FieldNode node = reporting.buildFieldNode(map, this, field, frame.getPc());
            return new InstructionReturn(frame, frame.getMethod(), lowerCodes, upperCodes, codes, null, node);
        }
    },
    DALVIK_0x63 {
        @Override
        public String description() {
            return "sget-boolean";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x63";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-boolean
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, 0, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x64 {
        @Override
        public String description() {
            return "sget-byte";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x64";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-byte
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, 0, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x65 {
        @Override
        public String description() {
            return "sget-boolean";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x65";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-boolean
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, 0, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x66 {
        @Override
        public String description() {
            return "sget-short";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x66";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sget-short
            
            int destination = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().getField(frame, 0, fieldIndex, destination);
            return null;
        }
    },
    DALVIK_0x67 {
        @Override
        public String description() {
            return "sput";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x67";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x68 {
        @Override
        public String description() {
            return "sput-wide";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x68";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-wide
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x69 {
        @Override
        public String description() {
            return "sput-object";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x69";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-object
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x6A {
        @Override
        public String description() {
            return "sput-boolean";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x6A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-boolean
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x6B {
        @Override
        public String description() {
            return "sput-byte";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x6B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-byte
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x6C {
        @Override
        public String description() {
            return "sput-char";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x6C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-char
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x6D {
        @Override
        public String description() {
            return "sput-short";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x6D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sput-short
            
            int source = upperCodes[frame.increasePc()];
            int fieldIndex = codes[frame.increasePc()];
            frame.getThread().setField(frame, source, 0, fieldIndex);
            return null;
        }
    },
    DALVIK_0x6E {
        @Override
        public String description() {
            return "invoke-virtual {vD, vE, vF, vG, vA}, meth@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x6E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            IDroidefenseMethod method = frame.getMethod();// invoke-virtual {vD, vE, vF, vG, vA}, meth@CCCC
            int registers = upperCodes[frame.increasePc()] << 16;
            int methodIndex = codes[frame.increasePc()];
            registers |= codes[frame.increasePc()];String clazzName, methodName, methodDescriptor;if (registers < method.getMethodClasses().length) {
                clazzName = method.getMethodClasses()[registers];
                methodName = method.getMethodNames()[registers];
                methodDescriptor = method.getMethodTypes()[registers];
            } else {
                clazzName = method.getMethodClasses()[methodIndex]; //index out of bounds
                methodName = method.getMethodNames()[methodIndex];
                methodDescriptor = method.getMethodTypes()[methodIndex];
            }String args = AbstractDVMThread.setArguments(true, frame, methodDescriptor, registers);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                //TODO check if the method exist on current class or is inherited and build a proper object
                object = new DVMInstance(DexClassReader.getInstance().load(clazzName));
            }if (object instanceof IDroidefenseInstance) {
                IDroidefenseInstance instance = (IDroidefenseInstance) object;
                IDroidefenseMethod target = instance.getOwnerClass().getVirtualMethod(methodName, methodDescriptor, true);
                if (target != null) {
                    frame = frame.getThread().callMethod(true, target, frame);
                    method = frame.getMethod();
                    lowerCodes = method.getOpcodes();
                    upperCodes = method.getRegisterOpcodes();
                    codes = method.getIndex();
                    MethodNode node = reporting.buildMethodNode(map, this, frame, method);
                    return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, null, node);
                } else if (clazzName.equals(instance.getOwnerClass().getName())) {
                    clazzName = instance.getOwnerClass().getSuperClass();
                }
            }
            try {
                if (!frame.getThread().getVirtualMachine().handleInstanceMethod(frame, clazzName, methodName, methodDescriptor)) {
                    VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                    return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                }
            } catch (Exception e) {
                return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
            }
            return null;
        }
    },
    DALVIK_0x6F {
        @Override
        public String description() {
            return "invoke-super {vD, vE, vF, vG, vA}, meth@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x6F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {// invoke-super {vD, vE, vF, vG, vA}, meth@CCCC
            IDroidefenseMethod method = frame.getMethod();// invoke-direct {vD, vE, vF, vG, vA}, meth@CCCC
            int registers = upperCodes[frame.increasePc()] << 16;
            int methodIndex = codes[frame.increasePc()];
            registers |= codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, registers);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
            if (cls != null) {
                frame = frame.getThread().callMethod(false, cls.getDirectMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = frame.getMethod().getOpcodes();
                upperCodes = frame.getMethod().getRegisterOpcodes();
                codes = frame.getMethod().getIndex();
                MethodNode calledNode = reporting.buildMethodNode(map, this, frame, method);
                return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, null, calledNode);
            } else {
                if (methodName.equals("<init>")) {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleConstructor(frame, clazzName, methodName, methodDescriptor)) {
                            VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented constructor = " + clazzName + " - " + methodDescriptor);
                            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                        }
                    } catch (Exception e) {
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                } else {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleInstanceMethod(frame, clazzName, methodName, methodDescriptor)) {
                            VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                }
            }
            return null;
        }
    },
    DALVIK_0x70 {
        @Override
        public String description() {
            return "invoke-direct {vD, vE, vF, vG, vA}, meth@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x70";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {// invoke-direct {vD, vE, vF, vG, vA}, meth@CCCC
            IDroidefenseMethod method = frame.getMethod();
int registers = upperCodes[frame.increasePc()] << 16;
            int methodIndex = codes[frame.increasePc()];
            registers |= codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, registers);//'this' represent itself reference
            Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }IDroidefenseClass cls;
            if (object == null)
                cls = DexClassReader.getInstance().load(clazzName);
            else {
                DVMInstance instance = (DVMInstance) object;
            }
            cls = DexClassReader.getInstance().load(clazzName);
            if (cls != null) {
                frame = frame.getThread().callMethod(false, cls.getDirectMethod(methodName, methodDescriptor, true), frame);
                method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
                MethodNode node = reporting.buildMethodNode(map, this, frame, method);
                return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, null, node);
            } else {
                if (methodName.equals("<init>")) {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleConstructor(frame, clazzName, methodName, methodDescriptor)) {
                            VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented constructor = " + clazzName + " - " + methodDescriptor);
                            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                        }
                    } catch (Exception e) {
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                } else {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleInstanceMethod(frame, clazzName, methodName, methodDescriptor)) {
                            VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                }
            }
            return null;
        }
    },
    DALVIK_0x71 {
        @Override
        public String description() {
            return "invoke-static {vD, vE, vF, vG, vA}, meth@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x71";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {

            IDroidefenseMethod method = frame.getMethod();// invoke-static {vD, vE, vF, vG, vA}, meth@CCCC
            int registers = upperCodes[frame.increasePc()] << 16;
            int methodIndex = codes[frame.increasePc()];
            registers |= codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(false, frame, methodDescriptor, registers);IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
            if (cls != null) {
                frame = frame.getThread().callMethod(false, cls.getDirectMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
                return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, null);
            } else {
                try {
                    if (!frame.getThread().getVirtualMachine().handleClassMethod(frame, method, clazzName, methodName, methodDescriptor, null)) {
                        VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented class method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                } catch (Exception e) {
                    return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                }
            }
            return null;
        }
    },
    DALVIK_0x72 {
        @Override
        public String description() {
            return "invoke-interface {vD, vE, vF, vG, vA}, meth@CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x72";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {

            IDroidefenseMethod method = frame.getMethod();// invoke-interface {vD, vE, vF, vG, vA}, meth@CCCC
            int registers = upperCodes[frame.increasePc()] << 16;
            int methodIndex = codes[frame.increasePc()];
            registers |= codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, registers);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }if (object instanceof IDroidefenseInstance) {
                IDroidefenseClass cls = ((IDroidefenseInstance) object).getOwnerClass();
                frame = frame.getThread().callMethod(false, cls.getVirtualMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
                return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, null);
            } else {
                try {
                    if (!frame.getThread().getVirtualMachine().handleInterfaceMethod(frame, clazzName, methodName, methodDescriptor)) {
                        VirtualMachineRuntimeException e = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                        return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                    }
                } catch (Exception e) {
                    return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
                }
            }
            return null;
        }
    },
    DALVIK_0x73 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x73";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x74 {
        @Override
        public String description() {
            return "invoke-virtual/range {vCCCC .. vNNNN}, meth@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x74";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // invoke-virtual/range {vCCCC .. vNNNN}, meth@BBBB

            IDroidefenseMethod method = frame.getMethod();int range = upperCodes[frame.increasePc()];
            int methodIndex = codes[frame.increasePc()];
            int firstRegister = codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, firstRegister, range);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }VirtualMachineRuntimeException e = null;
            if (object instanceof IDroidefenseInstance) {
                IDroidefenseInstance instance = (IDroidefenseInstance) object;
                IDroidefenseMethod target = instance.getOwnerClass().getVirtualMethod(methodName, methodDescriptor, true);
                if (target != null) {
                    frame = frame.getThread().callMethod(true, target, frame);        method = frame.getMethod();
                    lowerCodes = method.getOpcodes();
                    upperCodes = method.getRegisterOpcodes();
                    codes = method.getIndex();
                } else if (clazzName.equals(instance.getOwnerClass().getName())) {
                    clazzName = instance.getOwnerClass().getSuperClass();
                }
            }
            try {
                if (!frame.getThread().getVirtualMachine().handleInstanceMethod(frame, clazzName, methodName, methodDescriptor)) {
                    e = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                }
            } catch (Exception e1) {
                e = new VirtualMachineRuntimeException("Exception on instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
            }
            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, e);
        }
    },
    DALVIK_0x75 {
        @Override
        public String description() {
            return "invoke-super/range {vCCCC .. vNNNN}, meth@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x75";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // invoke-super/range {vCCCC .. vNNNN}, meth@BBBB
            // fall through
            return null;
        }
    },
    DALVIK_0x76 {
        @Override
        public String description() {
            return "invoke-direct/range {vCCCC .. vNNNN}, meth@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x76";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {// invoke-direct/range {vCCCC .. vNNNN}, meth@BBBB
            IDroidefenseMethod method = frame.getMethod();int range = upperCodes[frame.increasePc()];
            int methodIndex = codes[frame.increasePc()];
            int firstRegister = codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, firstRegister, range);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
            Exception error = null;
            if (cls != null) {
                frame = frame.getThread().callMethod(false, cls.getDirectMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
            } else {
                if (methodName.equals("<init>")) {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleConstructor(frame, clazzName, methodName, methodDescriptor)) {
                            error = new VirtualMachineRuntimeException("not implemented constructor = " + clazzName + " - " + methodDescriptor);
                        }
                    } catch (Exception e) {
                        Log.write(LoggerType.FATAL, "Error executing DalvikInstruction 0x76", e.getLocalizedMessage(), e.getStackTrace());
                        error = e;
                    }
                } else {
                    try {
                        if (!frame.getThread().getVirtualMachine().handleInstanceMethod(frame, clazzName, methodName, methodDescriptor)) {
                            error = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                        }
                    } catch (Exception e) {
                        error = new VirtualMachineRuntimeException("Exception on instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                    }
                }
            }
            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, error);
        }
    },
    DALVIK_0x77 {
        @Override
        public String description() {
            return "invoke-static/range {vCCCC .. vNNNN}, meth@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x77";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // invoke-static/range {vCCCC .. vNNNN}, meth@BBBB
            IDroidefenseMethod method = frame.getMethod();int range = upperCodes[frame.increasePc()];
            int methodIndex = codes[frame.increasePc()];
            int firstRegister = codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(false, frame, methodDescriptor, firstRegister, range);
            IDroidefenseClass cls = DexClassReader.getInstance().load(clazzName);
            Exception error = null;
            if (cls != null) {
                frame = frame.getThread().callMethod(false, cls.getDirectMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
            } else {
                try {
                    if (!frame.getThread().getVirtualMachine().handleClassMethod(frame, method, clazzName, methodName, methodDescriptor, null)) {
                        error = new VirtualMachineRuntimeException("not implemented class method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                    }
                } catch (Exception e) {
                    error = new VirtualMachineRuntimeException("Exception on class method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                }
            }
            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, error);
        }
    },
    DALVIK_0x78 {
        @Override
        public String description() {
            return "invoke-interface/range {vCCCC .. vNNNN}, meth@BBBB";
        }

        @Override
        public int fakePcIncrement() {
            return 3;
        }

        @Override
        public String code() {
            return "0x78";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {// invoke-interface/range {vCCCC .. vNNNN}, meth@BBBB
            IDroidefenseMethod method = frame.getMethod();int range = upperCodes[frame.increasePc()];
            int methodIndex = codes[frame.increasePc()];
            int firstRegister = codes[frame.increasePc()];String clazzName = frame.getMethod().getMethodClasses()[methodIndex];
            String methodName = frame.getMethod().getMethodNames()[methodIndex];
            String methodDescriptor = frame.getMethod().getMethodTypes()[methodIndex];AbstractDVMThread.setArguments(true, frame, methodDescriptor, firstRegister, range);Object object = frame.getObjectArguments()[0];if (object == null) {
                //build a fake 'this' object and set it as real
                object = new DVMInstance(method.getOwnerClass());
            }Exception error = null;
            if (object instanceof IDroidefenseInstance) {
                IDroidefenseClass cls = ((IDroidefenseInstance) object).getOwnerClass();
                frame = frame.getThread().callMethod(false, cls.getVirtualMethod(methodName, methodDescriptor, true), frame);    method = frame.getMethod();
                lowerCodes = method.getOpcodes();
                upperCodes = method.getRegisterOpcodes();
                codes = method.getIndex();
            } else {
                try {
                    if (!frame.getThread().getVirtualMachine().handleInterfaceMethod(frame, clazzName, methodName, methodDescriptor)) {
                        error = new VirtualMachineRuntimeException("not implemented instance method = " + clazzName + " - " + methodName + " - " + methodDescriptor);
                    }
                } catch (Exception e) {
                    Log.write(LoggerType.FATAL, "Error executing DalvikInstruction 0x78", e.getLocalizedMessage(), e.getStackTrace());
                    error = e;
                }
            }
            return new InstructionReturn(frame, method, lowerCodes, upperCodes, codes, error);
        }
    },
    DALVIK_0x79 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x79";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x7A {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0x7B {
        @Override
        public String description() {
            return "neg-int vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // neg-int vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = -frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x7C {
        @Override
        public String description() {
            return "not-int vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // not-int vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = ~frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x7D {
        @Override
        public String description() {
            return "neg-long vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // neg-long vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long value = -DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x7E {
        @Override
        public String description() {
            return "not-long vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // not-long vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long value = ~DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x7F {
        @Override
        public String description() {
            return "neg-float vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x7F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // neg-float vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float value = -Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x80 {
        @Override
        public String description() {
            return "neg-double vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x80";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // neg-double vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            double value = -Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(value));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x81 {
        @Override
        public String description() {
            return "int-to-long";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x81";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // int-to-long
            int destination = upperCodes[frame.getPc()] & 0xF;
            long value = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x82 {
        @Override
        public String description() {
            return "int-to-float vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x82";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // int-to-float vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float value = (float) frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = Float.floatToIntBits(value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x83 {
        @Override
        public String description() {
            return "int-to-double vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x83";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // int-to-double vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            double value = (double) frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(value));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x84 {
        @Override
        public String description() {
            return "long-to-int";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x84";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // long-to-int
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (int) DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x85 {
        @Override
        public String description() {
            return "long-to-float";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x85";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // long-to-float
            int destination = upperCodes[frame.getPc()] & 0xF;
            float value = (float) DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x86 {
        @Override
        public String description() {
            return "long-to-double";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x86";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // long-to-double
            int destination = upperCodes[frame.getPc()] & 0xF;
            double value = (double) DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(value));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x87 {
        @Override
        public String description() {
            return "float-to-int vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x87";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // float-to-int vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (int) Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x88 {
        @Override
        public String description() {
            return "float-to-long vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x88";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // float-to-long vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long value = (long) Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x89 {
        @Override
        public String description() {
            return "float-to-double vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x89";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // float-to-double vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            double value = (double) Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(value));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8A {
        @Override
        public String description() {
            return "double-to-int vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // double-to-int vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (int) Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8B {
        @Override
        public String description() {
            return "double-to-long vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // double-to-long vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long value = (long) Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8C {
        @Override
        public String description() {
            return "double-to-float vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // double-to-float vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float value = (float) Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
            frame.getIntRegisters()[destination] = Float.floatToIntBits(value);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8D {
        @Override
        public String description() {
            return "int-to-byte";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // int-to-byte
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (byte) frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8E {
        @Override
        public String description() {
            return "int-to-char";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (char) frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x8F {
        @Override
        public String description() {
            return "int-to-short";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0x8F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // int-to-short
            int destination = upperCodes[frame.getPc()] & 0xF;
            int value = (short) frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = value;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x90 {
        @Override
        public String description() {
            return "add-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x90";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue + secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x91 {
        @Override
        public String description() {
            return "sub-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x91";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue - secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x92 {
        @Override
        public String description() {
            return "mul-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x92";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue * secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x93 {
        @Override
        public String description() {
            return "div-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x93";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue / secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x94 {
        @Override
        public String description() {
            return "rem-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x94";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue % secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x95 {
        @Override
        public String description() {
            return "and-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x95";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue & secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x96 {
        @Override
        public String description() {
            return "or-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x96";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue | secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x97 {
        @Override
        public String description() {
            return "xor-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x97";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue ^ secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x98 {
        @Override
        public String description() {
            return "shl-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x98";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shl-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue << secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x99 {
        @Override
        public String description() {
            return "shr-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x99";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shr-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue >> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9A {
        @Override
        public String description() {
            return "ushr-int vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9A";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // ushr-int vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]];
            frame.getIntRegisters()[destination] = firstValue >>> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9B {
        @Override
        public String description() {
            return "add-long vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9B";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-long vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue + secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9C {
        @Override
        public String description() {
            return "sub-long vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9C";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-long vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue - secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9D {
        @Override
        public String description() {
            return "mul-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9D";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue * secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9E {
        @Override
        public String description() {
            return "div-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9E";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue / secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0x9F {
        @Override
        public String description() {
            return "rem-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0x9F";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue % secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA0 {
        @Override
        public String description() {
            return "and-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue & secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA1 {
        @Override
        public String description() {
            return "or-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue | secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA2 {
        @Override
        public String description() {
            return "xor-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue ^ secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA3 {
        @Override
        public String description() {
            return "shl-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shl-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]] & 0x3F;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue << secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA4 {
        @Override
        public String description() {
            return "shr-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shr-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]] & 0x3F;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue >> secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA5 {
        @Override
        public String description() {
            return "ushr-long";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // ushr-long
            int destination = upperCodes[frame.increasePc()];
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]);
            long secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()]] & 0x3F;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue >>> secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA6 {
        @Override
        public String description() {
            return "add-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue + secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA7 {
        @Override
        public String description() {
            return "sub-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue - secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA8 {
        @Override
        public String description() {
            return "mul-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue * secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xA9 {
        @Override
        public String description() {
            return "div-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xA9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue / secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAA {
        @Override
        public String description() {
            return "rem-float vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-float vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[lowerCodes[frame.getPc()]]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()]]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue % secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAB {
        @Override
        public String description() {
            return "add-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue + secondValue));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAC {
        @Override
        public String description() {
            return "sub-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue - secondValue));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAD {
        @Override
        public String description() {
            return "mul-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue * secondValue));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAE {
        @Override
        public String description() {
            return "div-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue / secondValue));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xAF {
        @Override
        public String description() {
            return "rem-double vAA, vBB, vCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xAF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-double vAA, vBB, vCC
            int destination = upperCodes[frame.increasePc()];
            double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), lowerCodes[frame.getPc()]));
            double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()]));
            DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue % secondValue));
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB0 {
        @Override
        public String description() {
            return "add-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue + secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB1 {
        @Override
        public String description() {
            return "sub-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue - secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB2 {
        @Override
        public String description() {
            return "mul-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue * secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB3 {
        @Override
        public String description() {
            return "div-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue / secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB4 {
        @Override
        public String description() {
            return "rem-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue % secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB5 {
        @Override
        public String description() {
            return "and-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue & secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB6 {
        @Override
        public String description() {
            return "or-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue | secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB7 {
        @Override
        public String description() {
            return "xor-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue ^ secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB8 {
        @Override
        public String description() {
            return "shl-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shl-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue << secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xB9 {
        @Override
        public String description() {
            return "shr-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xB9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shr-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue >> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBA {
        @Override
        public String description() {
            return "ushr-int/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // ushr-int/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[destination];
            int secondValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            frame.getIntRegisters()[destination] = firstValue >>> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBB {
        @Override
        public String description() {
            return "add-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue + secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBC {
        @Override
        public String description() {
            return "sub-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue - secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBD {
        @Override
        public String description() {
            return "mul-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue * secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBE {
        @Override
        public String description() {
            return "div-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue / secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xBF {
        @Override
        public String description() {
            return "rem-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xBF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue % secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC0 {
        @Override
        public String description() {
            return "and-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue & secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC1 {
        @Override
        public String description() {
            return "or-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue | secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC2 {
        @Override
        public String description() {
            return "xor-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4);
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue ^ secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC3 {
        @Override
        public String description() {
            return "shl-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shl-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4) & 0x3F;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue << secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC4 {
        @Override
        public String description() {
            return "shr-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shr-long/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
                long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4) & 0x3F;
                DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue >> secondValue);
                frame.getIsObjectRegister()[destination] = false;
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Execution error on DalvikInstruction " + description());
                frame.increasePc();
            }
            return null;
        }
    },
    DALVIK_0xC5 {
        @Override
        public String description() {
            return "ushr-long/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // ushr-long/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            long firstValue = DynamicUtils.getLong(frame.getIntRegisters(), destination);
            long secondValue = DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4) & 0x3F;
            DynamicUtils.setLong(frame.getIntRegisters(), destination, firstValue >>> secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC6 {
        @Override
        public String description() {
            return "add-float/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-float/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[destination]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue + secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC7 {
        @Override
        public String description() {
            return "sub-float/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-float/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[destination]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue - secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC8 {
        @Override
        public String description() {
            return "mul-float/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-float/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[destination]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue * secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xC9 {
        @Override
        public String description() {
            return "div-float/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xC9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-float/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[destination]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue / secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xCA {
        @Override
        public String description() {
            return "rem-float/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-float/2addr vA, vB
            int destination = upperCodes[frame.getPc()] & 0xF;
            float firstValue = Float.intBitsToFloat(frame.getIntRegisters()[destination]);
            float secondValue = Float.intBitsToFloat(frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4]);
            frame.getIntRegisters()[destination] = Float.floatToIntBits(firstValue % secondValue);
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xCB {
        @Override
        public String description() {
            return "add-double/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-double/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), destination));
                double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue + secondValue));
                frame.getIsObjectRegister()[destination] = false;
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Error on DalvikInstruction (" + description() + ") logic");
            }
            return null;
        }
    },
    DALVIK_0xCC {
        @Override
        public String description() {
            return "sub-double/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // sub-double/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), destination));
                double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue - secondValue));
                frame.getIsObjectRegister()[destination] = false;
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Error on DalvikInstruction (" + description() + ") logic");
            }
            return null;
        }
    },
    DALVIK_0xCD {
        @Override
        public String description() {
            return "mul-double/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-double/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), destination));
                double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue * secondValue));
                frame.getIsObjectRegister()[destination] = false;
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Error on DalvikInstruction (" + description() + ") logic");
            }
            return null;
        }
    },
    DALVIK_0xCE {
        @Override
        public String description() {
            return "div-double/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-double/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), destination));
                double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue / secondValue));
                frame.getIsObjectRegister()[destination] = false;
            } catch (Exception e) {
                Log.write(LoggerType.FATAL, "Error on DalvikInstruction (" + description() + ") logic");
            }
            return null;
        }
    },
    DALVIK_0xCF {
        @Override
        public String description() {
            return "rem-double/2addr vA, vB";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xCF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-double/2addr vA, vB
            try {
                int destination = upperCodes[frame.getPc()] & 0xF;
                double firstValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), destination));
                double secondValue = Double.longBitsToDouble(DynamicUtils.getLong(frame.getIntRegisters(), upperCodes[frame.increasePc()] >> 4));
                DynamicUtils.setLong(frame.getIntRegisters(), destination, Double.doubleToLongBits(firstValue % secondValue));
                frame.getIsObjectRegister()[destination] = false;
            } finally {
                Log.write(LoggerType.FATAL, "Error on DalvikInstruction (" + description() + ") logic");
            }
            return null;
        }
    },
    DALVIK_0xD0 {
        @Override
        public String description() {
            return "add-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue + secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD1 {
        @Override
        public String description() {
            return "rsub-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rsub-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = secondValue - firstValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD2 {
        @Override
        public String description() {
            return "mul-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue * secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD3 {
        @Override
        public String description() {
            return "div-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue / secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD4 {
        @Override
        public String description() {
            return "rem-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue % secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD5 {
        @Override
        public String description() {
            return "and-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue & secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD6 {
        @Override
        public String description() {
            return "or-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue | secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD7 {
        @Override
        public String description() {
            return "xor-int/lit16 vA, vB, #+CCCC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-int/lit16 vA, vB, #+CCCC
            int destination = upperCodes[frame.getPc()] & 0xF;
            int firstValue = frame.getIntRegisters()[upperCodes[frame.increasePc()] >> 4];
            int secondValue = (short) codes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue ^ secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD8 {
        @Override
        public String description() {
            return "add-int/lit8 vx,vy,lit8";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // add-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue + secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xD9 {
        @Override
        public String description() {
            return "rsub-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xD9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rsub-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = secondValue - firstValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDA {
        @Override
        public String description() {
            return "mul-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // mul-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue * secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDB {
        @Override
        public String description() {
            return "div-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // div-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue / secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDC {
        @Override
        public String description() {
            return "rem-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // rem-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue % secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDD {
        @Override
        public String description() {
            return "and-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // and-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue & secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDE {
        @Override
        public String description() {
            return "or-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // or-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue | secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xDF {
        @Override
        public String description() {
            return "xor-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xDF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // xor-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue ^ secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xE0 {
        @Override
        public String description() {
            return "shl-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xE0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shl-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue << secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xE1 {
        @Override
        public String description() {
            return "shr-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xE1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // shr-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue >> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xE2 {
        @Override
        public String description() {
            return "ushr-int/lit8 vAA, vBB, #+CC";
        }

        @Override
        public int fakePcIncrement() {
            return 2;
        }

        @Override
        public String code() {
            return "0xE2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            // ushr-int/lit8 vAA, vBB, #+CC
            int destination = upperCodes[frame.increasePc()];
            int firstValue = frame.getIntRegisters()[lowerCodes[frame.getPc()]];
            int secondValue = (byte) upperCodes[frame.increasePc()];
            frame.getIntRegisters()[destination] = firstValue >>> secondValue;
            frame.getIsObjectRegister()[destination] = false;
            return null;
        }
    },
    DALVIK_0xE3 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE4 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE5 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE6 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE7 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE8 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xE9 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xE9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xEA {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xEA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xEB {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xEB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xEC {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xEC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xED {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xED";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xEE {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xEE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xEF {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xEF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF0 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF0";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF1 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF1";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF2 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF2";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF3 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF3";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF4 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF4";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF5 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF5";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF6 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF6";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF7 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF7";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF8 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF8";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xF9 {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xF9";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFA {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFA";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFB {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFB";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFC {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFC";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFD {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFD";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFE {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFE";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    },
    DALVIK_0xFF {
        @Override
        public String description() {
            return "unused";
        }

        @Override
        public int fakePcIncrement() {
            return 1;
        }

        @Override
        public String code() {
            return "0xFF";
        }

        @Override
        public InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv) {
            //unused
            frame.increasePc(fakePcIncrement());
            return null;
        }
    };

    public static final byte CFG_EXECUTION = 0x0;
    public static final byte REAL_EXECUTION = 0x1;
    public static final byte MULTIPATH_EXECUTION = 0x2;
    private static final NodeGenerator reporting = NodeGenerator.getInstance();

    public abstract String description();

    public abstract String code();

    public abstract int fakePcIncrement();

    public abstract InstructionReturn execute(AbstractFlowMap map, IDroidefenseFrame frame, int[] lowerCodes, int[] upperCodes, int[] codes, byte executionEnv);

    @Override
    public String toString() {
        return "inst: " + description();
    }
}