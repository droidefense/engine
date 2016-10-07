package com.zerjioang.apkr.analysis.dynamicscan.machine.base.exceptions;

import java.io.Serializable;

public class VirtualMachineRuntimeException extends RuntimeException implements Serializable {

    public VirtualMachineRuntimeException(final String message) {
        super(message);
    }
}
