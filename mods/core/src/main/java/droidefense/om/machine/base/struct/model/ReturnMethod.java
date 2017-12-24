package droidefense.om.machine.base.struct.model;


import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.om.machine.base.struct.generic.IDroidefenseMethod;

import java.io.Serializable;

/**
 * Created by sergio on 21/2/16.
 */
public class ReturnMethod implements Serializable {


    private IDroidefenseFrame frame;

    private IDroidefenseMethod method;
    private int[] lowerCodes;
    private int[] upperCodes;
    private int[] codes;

    public ReturnMethod(AbstractDVMThread worker) {
        frame = worker.popFrame();
        method = frame.getMethod();
        lowerCodes = method.getOpcodes();
        upperCodes = method.getRegistercodes();
        codes = method.getIndex();
    }

    public IDroidefenseFrame getFrame() {
        return frame;
    }

    public IDroidefenseMethod getMethod() {
        return method;
    }

    public int[] getLowerCodes() {
        return lowerCodes;
    }

    public int[] getUpperCodes() {
        return upperCodes;
    }

    public int[] getCodes() {
        return codes;
    }
}