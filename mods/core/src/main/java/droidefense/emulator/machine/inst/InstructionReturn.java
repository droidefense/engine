package droidefense.emulator.machine.inst;

import droidefense.emulator.machine.base.exceptions.VirtualMachineRuntimeException;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseField;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.rulengine.base.AbstractAtomNode;

import java.io.Serializable;

/**
 * Created by sergio on 25/3/16.
 */
public final class InstructionReturn implements Serializable {

    private IDroidefenseFrame frame;
    private IDroidefenseField field;
    private IDroidefenseMethod method;
    private int[] lowerCodes, upperCodes, codes;
    private Throwable error;
    private AbstractAtomNode node;

    public InstructionReturn(IDroidefenseFrame frame, IDroidefenseMethod method, int[] opcodes, int[] upperCodes, int[] mIdx, Throwable error) {
        this.frame = frame;
        this.method = method;
        this.lowerCodes = opcodes;
        this.upperCodes = upperCodes;
        this.codes = mIdx;
        this.error = error;
    }

    public InstructionReturn(IDroidefenseFrame frame, IDroidefenseMethod method, int[] lowerCodes, int[] upperCodes, int[] codes, Throwable error, AbstractAtomNode node) {
        this(frame, method, lowerCodes, upperCodes, codes, error);
        this.node = node;
    }

    public InstructionReturn(IDroidefenseFrame frame, IDroidefenseField field) {
        this.frame = frame;
        this.field = field;
    }

    public InstructionReturn(VirtualMachineRuntimeException e) {
        this.error = e;
    }

    public IDroidefenseFrame getFrame() {
        return frame;
    }

    public void setFrame(IDroidefenseFrame frame) {
        this.frame = frame;
    }

    public IDroidefenseMethod getMethod() {
        return method;
    }

    public void setMethod(IDroidefenseMethod method) {
        this.method = method;
    }

    public int[] getLowerCodes() {
        return lowerCodes;
    }

    public void setLowerCodes(int[] lowerCodes) {
        this.lowerCodes = lowerCodes;
    }

    public int[] getUpperCodes() {
        return upperCodes;
    }

    public void setUpperCodes(int[] upperCodes) {
        this.upperCodes = upperCodes;
    }

    public int[] getCodes() {
        return codes;
    }

    public void setCodes(int[] codes) {
        this.codes = codes;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public IDroidefenseField getField() {
        return field;
    }

    public void setField(IDroidefenseField field) {
        this.field = field;
    }

    public AbstractAtomNode getNode() {
        return node;
    }
}
