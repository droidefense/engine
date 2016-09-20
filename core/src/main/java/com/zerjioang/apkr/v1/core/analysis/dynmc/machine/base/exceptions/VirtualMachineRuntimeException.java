package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.exceptions;

import java.io.Serializable;

public class VirtualMachineRuntimeException extends RuntimeException implements Serializable {

    public VirtualMachineRuntimeException(final String message) {
        super(message);
    }
}
