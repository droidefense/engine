package droidefense.om.machine.base.exceptions;

import java.io.Serializable;

// This cls is used to change threads and not used as a normal exception.
// As you can see inherits from runtimeException, so that it can only be thrown as runtime. You cant wait for it!
public final class ChangeThreadRuntimeException extends RuntimeException implements Serializable {

    private final Throwable throwable;

    public ChangeThreadRuntimeException() {
        this(null);
    }

    public ChangeThreadRuntimeException(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public Throwable getCause() {
        return throwable;
    }
}
