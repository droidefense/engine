package com.zerjioang.apkr.analysis.dynamicscan.machine.base;

import java.io.Serializable;
import java.util.Vector;

public final class VMWaitSet implements Serializable {

    private final Vector<AbstractDVMThread> threads;
    private Object instance;

    public VMWaitSet() {
        threads = new Vector<>();
    }

    public AbstractDVMThread getFirstThreadAndRemove() {
        if (threads.isEmpty()) {
            return null;
        }
        AbstractDVMThread thread = threads.remove(0);
        if (threads.isEmpty()) {
            setInstance(null);
        }
        return thread;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public void addThread(AbstractDVMThread thread) {
        this.threads.addElement(thread);
    }
}
