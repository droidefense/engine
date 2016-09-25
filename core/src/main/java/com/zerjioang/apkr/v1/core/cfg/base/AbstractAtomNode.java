package com.zerjioang.apkr.v1.core.cfg.base;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 10/4/16.
 */
public abstract class AbstractAtomNode implements IDotGraphNode, Serializable {

    protected static int maxInt = 0;
    private static int counter = 0;
    protected final int id;

    private final transient ArrayList<AbstractAtomNode> outList;

    private final transient ArrayList<AbstractAtomNode> inList;
    protected int in, out;
    protected String type;

    public AbstractAtomNode() {
        counter++;
        this.id = counter;
        in = 0;
        out = 0;
        type = "";
        outList = new ArrayList<AbstractAtomNode>();
        inList = new ArrayList<AbstractAtomNode>();
    }

    public final int getId() {
        return id;
    }

    public String getAsDotGraph() {
        return "\t" + getId() + " [label=\"" + getNodeLabel() + "\" " + getNodeStyle() + "];\n";
    }

    public void addIn(int i) {
        this.in += i;
        if (in > maxInt)
            maxInt = in;
    }

    public void addOut(int i) {
        this.out += i;
    }

    public int getIn() {
        return in;
    }

    public int getOut() {
        return out;
    }

    public boolean isOrphane() {
        return getIn() == 0 && getOut() == 0;
    }

    public boolean hasNoParent() {
        //because every node is linked with entrypoint node
        return getIn() == 1;
    }

    @Override
    public final String toString() {
        return String.valueOf(id);
    }

    public int getNodeFamaLevel() {
        return in + out;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<AbstractAtomNode> getOutNodes() {
        return outList;
    }

    public ArrayList<AbstractAtomNode> getInNodes() {
        return inList;
    }

    public void addOutNode(AbstractAtomNode n) {
        outList.add(n);
    }

    public void addInNode(AbstractAtomNode n) {
        inList.add(n);
    }
}
