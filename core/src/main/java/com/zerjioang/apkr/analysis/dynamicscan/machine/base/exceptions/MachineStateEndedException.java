package com.zerjioang.apkr.analysis.dynamicscan.machine.base.exceptions;

import java.io.Serializable;

/**
 * Created by sergio on 28/3/16.
 */
public class MachineStateEndedException extends VirtualMachineRuntimeException implements Serializable {

    public MachineStateEndedException(String s) {
        super(s);
    }
}
