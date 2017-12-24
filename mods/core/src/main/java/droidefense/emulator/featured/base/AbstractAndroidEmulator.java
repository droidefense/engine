package droidefense.emulator.featured.base;

import java.io.Serializable;

public abstract class AbstractAndroidEmulator implements Serializable {

    protected static int counter;

    public AbstractAndroidEmulator() {
        counter = 0;
    }

    protected final void increaseCounter() {
        counter++;
    }

    protected final void decreaseCounter() {
        counter--;
    }

    public abstract void emulate();
}
