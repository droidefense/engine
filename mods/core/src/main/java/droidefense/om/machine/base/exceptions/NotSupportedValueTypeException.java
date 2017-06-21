package droidefense.om.machine.base.exceptions;


import droidefense.om.machine.base.constants.ValueFormat;

import java.io.Serializable;

/**
 * Created by B328316 on 26/02/2016.
 */
public class NotSupportedValueTypeException extends RuntimeException implements Serializable {

    public NotSupportedValueTypeException(ValueFormat type) {
        super("\nDroidefense Engine detected an unsupported value type in the current .dex file.\nPlease see the next information:"
                + "\n\tUnsupported value type:\t0x" + Integer.toHexString(type.getInstructionByteId())
                + "\n\tResolved as:\t" + type.getName() + " value type"
        );
    }
}
