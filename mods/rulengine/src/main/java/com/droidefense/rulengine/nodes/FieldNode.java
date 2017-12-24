package com.droidefense.rulengine.nodes;

import com.droidefense.rulengine.base.AbstractAtomNode;
import com.droidefense.rulengine.base.AbstractFlowMap;

public class FieldNode extends AbstractAtomNode {

    private final String type;
    private final String ownerclassName;
    private final String fieldName;
    private final int pc;

    public FieldNode(String instructionName, String ownerclassName, String fieldName, String type, int pc) {
        super();
        this.instructionName = instructionName;
        this.ownerclassName = ownerclassName;
        this.fieldName = fieldName;
        this.type = type;
        this.pc = pc;
    }

    public static FieldNode builder(AbstractFlowMap map, String instructionName, String ownerclassName, String methodName, String type, int pc) {
        //check if a node with this method exist;
        AbstractAtomNode node = map.getNode(-1);
        if (node != null) {
            return (FieldNode) node;
        }
        return new FieldNode(instructionName, ownerclassName, methodName, type, pc);
    }

    @Override
    public String getConnectionLabel() {
        return instructionName;
    }

    @Override
    public String getNodeLabel() {
        return (ownerclassName + "." + fieldName).replace("/", ".");
    }

    @Override
    public String getConnectionStyle() {
        return "";
    }

    @Override
    public String getNodeStyle() {
        return "";
    }

    @Override
    public boolean isDrawable() {
        return true;
    }
}
