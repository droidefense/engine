package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.model;


import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.AbstractDVMThread;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;

import java.io.Serializable;

/**
 * Created by sergio on 21/2/16.
 */
public class ReturnMethod implements Serializable {


    private IAtomFrame frame;

    private IAtomMethod method;
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

    public IAtomFrame getFrame() {
        return frame;
    }

    public IAtomMethod getMethod() {
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