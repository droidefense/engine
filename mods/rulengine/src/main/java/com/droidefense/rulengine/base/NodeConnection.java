package com.droidefense.rulengine.base;

import com.droidefense.rulengine.nodes.EntryPointNode;

import java.io.Serializable;

public class NodeConnection implements IDotGraphNode, Serializable {

    private final int id;
    private final String name;
    private final transient AbstractAtomNode source;
    private final transient AbstractAtomNode destination;
    private final String instructionName;
    private final int sourceId, destId;
    private int weight;

    public NodeConnection(final AbstractAtomNode source, final AbstractAtomNode destination, String instructionName) {
        this.source = source;
        this.destination = destination;
        this.instructionName = instructionName;
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

    public String getInstructionName(String name) {
        if (name == null)
            return "null";
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
        return getInstructionName(source.getInstructionName());
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
