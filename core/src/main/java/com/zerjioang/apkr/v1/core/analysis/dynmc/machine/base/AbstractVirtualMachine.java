package com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base;

import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;

import java.io.Serializable;
import java.util.Vector;

public abstract class AbstractVirtualMachine implements Serializable {

    public static int counter = 0;
    protected final Vector waitSets;
    protected final ApkrProject currentProject;
    protected String mainClassName;
    protected String[] argument;
    protected volatile boolean isEnd;
    protected volatile boolean stopRequested;
    private Object stopWait;
    private Vector<AbstractDVMThread> threads;

    /**
     * Run VM for a given class with no arguments
     *
     * @param mainClassName
     */
    public AbstractVirtualMachine(String mainClassName, ApkrProject project) {
        this.mainClassName = mainClassName;
        this.currentProject = project;
        this.argument = new String[0];

        isEnd = true;
        stopRequested = false;
        setStopWait(new Object());

        setThreads(new Vector());
        waitSets = new Vector();
        counter++;
    }


    /**
     * Run VM for a given class with arguments
     *
     * @param mainClassName
     * @param argument
     */
    public AbstractVirtualMachine(String mainClassName, String[] argument, ApkrProject project) {
        this(mainClassName, project);
        this.argument = argument;
    }

    /**
     * @return the counter
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public static void setCounter(int counter) {
        AbstractVirtualMachine.counter = counter;
    }

    public void addToWaitSet(final Object instance, final AbstractDVMThread thread) {
        VMWaitSet waitSet = getWaitSet(instance);
        if (waitSet != null) {
            waitSet.addThread(thread);
            return;
        }

        waitSet = findEmptyWaitSet(instance);
        if (waitSet == null) {
            waitSet = new VMWaitSet();
            waitSets.addElement(waitSet);
        }
        waitSet.setInstance(instance);
        waitSet.addThread(thread);
    }

    public void error(final String message) {
        System.err.println(message);
    }

    public void error(final Throwable e) {
        error(e.getClass().getName() + " --> " + e.getMessage());
    }

    public VMWaitSet findEmptyWaitSet(final Object instance) {
        for (int i = 0, length = waitSets.size(); i < length; i++) {
            VMWaitSet waitSet = (VMWaitSet) waitSets.elementAt(i);
            if (waitSet.getInstance() == null) {
                return waitSet;
            }
        }
        return null;
    }

    protected long getLong(final int[] ints, final int offset) {
        return DynamicUtils.getLong(ints, offset);
    }

    public Object getStopWait() {
        return stopWait;
    }

    public void setStopWait(Object stopWait) {
        this.stopWait = stopWait;
    }

    //---------- GETTERS AND SETTERS
    public void addThread(AbstractDVMThread worker) {
        threads.addElement(worker);
    }

    public void clearThreads() {
        threads.clear();
    }

    public void setThreads(Vector threads) {
        this.threads = threads;
    }

    public VMWaitSet getWaitSet(final Object instance) {
        for (int i = 0, length = waitSets.size(); i < length; i++) {
            VMWaitSet waitSet = (VMWaitSet) waitSets.elementAt(i);
            if (waitSet.getInstance() == instance) {
                return waitSet;
            }
        }
        return null;
    }

    public final boolean isEnd() {
        return isEnd;
    }

    /**
     * @param isEnd the isEnd to set
     */
    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isSubClass(final Throwable checked, final String targetClassName) {
        if (targetClassName == null) {
            return true;
        }
        try {
            return Class.forName(targetClassName.replace('/', '.')).isAssignableFrom(checked.getClass());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public abstract void load(ResourceFile dex, byte[] bytes, boolean multidex);

    public abstract void run() throws Throwable;

    protected void setLong(final int[] ints, final int offset, final long value) {
        DynamicUtils.setLong(ints, offset, value);
    }

    public final void stop() {
        synchronized (getStopWait()) {
            stopRequested = true;
            try {
                getStopWait().wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public String toDimesionString(final int dimension) {
        StringBuffer returned = new StringBuffer();
        for (int i = 0; i < dimension; i++) {
            returned.append("[]");
        }
        return returned.toString();
    }

    /**
     * @return the mainClassName
     */
    public String getMainClassName() {
        return mainClassName;
    }

    /**
     * @param mainClassName the mainClassName to set
     */
    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    /**
     * @return the argument
     */
    public String[] getArgument() {
        return argument;
    }

    /**
     * @param argument the argument to set
     */
    public void setArgument(String[] argument) {
        this.argument = argument;
    }

    public Vector getWaitSets() {
        return waitSets;
    }

    public void setIsEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public boolean isStopRequested() {
        return stopRequested;
    }

    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }

    public ApkrProject getCurrentProject() {
        return currentProject;
    }

    public int getThreadCount() {
        return threads.size();
    }

    public AbstractDVMThread getThread(int i) {
        if (i < threads.size())
            return threads.get(i);
        return null;
    }

    public void removeThread(AbstractDVMThread thread) {
        this.threads.remove(thread);
    }
}
