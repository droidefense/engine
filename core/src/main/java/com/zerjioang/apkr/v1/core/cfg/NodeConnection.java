package com.zerjioang.apkr.v1.core.cfg;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.base.IDotGraphNode;
import com.zerjioang.apkr.v1.core.cfg.nodes.EntryPointNode;

import java.io.Serializable;

/**
 * Created by sergio on 31/3/16.
 */
public class NodeConnection implements IDotGraphNode, Serializable {

    private final int id;
    private final String name;
    private final transient AbstractAtomNode source;
    private final transient AbstractAtomNode destination;

    private final Instruction currentInstruction;
    private final int sourceId, destId;
    private int weight;

    public NodeConnection(final AbstractAtomNode source, final AbstractAtomNode destination, Instruction currentInstruction) {
        this.source = source;
        this.destination = destination;
        this.currentInstruction = currentInstruction;
        this.name = source.toString() + "->" + destination.toString();
        this.id = source.getId() * 10 + destination.getId();
        this.sourceId = this.source.getId();
        this.destId = this.destination.getId();
        weight = 1;
        //update in out ratio
        destination.addIn(1);
        source.addOut(1);
        //save nodes
        destination.addInNode(source);
        source.addOutNode(destination);
    }

    public AbstractAtomNode getSource() {
        return source;
    }

    public AbstractAtomNode getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void increaseWeight(int i) {
        weight++;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return source.getNodeLabel() + " -> " + destination.getNodeLabel() + " (" + weight + ")";
    }

    @Override
    public String getAsDotGraph() {
        return "\t" + source.getId() + " -> " + destination.getId() + " [label = \"" + getConnectionLabel() + "\" " + getConnectionStyle() + "];\n";
    }

    public String getInstructionName() {
        String name = currentInstruction.description();
        int idx = name.indexOf(" ");
        if (idx != -1)
            return name.substring(0, idx);
        return name;
    }

    @Override
    public String getConnectionLabel() {
        if (source instanceof EntryPointNode) {
            return source.getConnectionLabel();
        }
        return getInstructionName();
    }

    @Override
    public String getNodeLabel() {
        return "";
    }

    @Override
    public String getConnectionStyle() {
        return source.getConnectionStyle();
    }

    @Override
    public String getNodeStyle() {
        return "";
    }

    @Override
    public boolean isDrawable() {
        return source.isDrawable() && destination.isDrawable();
    }
}
