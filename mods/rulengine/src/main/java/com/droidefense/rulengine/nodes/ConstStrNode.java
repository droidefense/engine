package com.droidefense.rulengine.nodes;

import com.droidefense.rulengine.base.AbstractAtomNode;
import com.droidefense.rulengine.base.AbstractFlowMap;

public class ConstStrNode extends AbstractAtomNode {

    private int dest;
    private String data;

    private ConstStrNode(String instructionName, int dest, String data) {
        this.instructionName = instructionName;
        this.dest = dest;
        this.data = data;
    }

    public static ConstStrNode builder(AbstractFlowMap map, String instructionName, int dest, String str) {
        //check if a node with this method exist;
        AbstractAtomNode node = map.getNode(-1);
        if (node != null)
            return (ConstStrNode) node;
        return new ConstStrNode(instructionName, dest, str);
    }

    @Override
    public String getConnectionLabel() {
        return instructionName;
    }

    @Override
    public String getNodeLabel() {
        if (data == null || data.isEmpty())
            return "v" + dest + " = EMPTY";
        return "v" + dest + " = " + data;
    }

    @Override
    public String getConnectionStyle() {
        return "fontcolor=Black, fontname=Courier, fontsize=15";
    }

    @Override
    public String getNodeStyle() {
        return "color=Black, fontcolor=Black, fontname=Courier, fontsize=15, shape=box";
    }

    @Override
    public boolean isDrawable() {
        return true;
    }
}
