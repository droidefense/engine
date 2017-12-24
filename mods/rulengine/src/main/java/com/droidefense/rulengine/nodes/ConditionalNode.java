package com.droidefense.rulengine.nodes;

import com.droidefense.rulengine.base.AbstractAtomNode;
import com.droidefense.rulengine.base.NodeCondition;

public class ConditionalNode extends AbstractAtomNode {

    private final int firstValue;
    private final int secondValue;
    private final int offsetA;
    private final int offsetB;

    private NodeCondition condition;

    private ConditionalNode(String instructionName, int firstValue, int secondValue, int offsetA, int offsetB) {
        super();
        this.instructionName = instructionName;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.offsetA = offsetA;
        this.offsetB = offsetB;
    }

    public static ConditionalNode builder(String instructionName, int firstValue, int secondValue, int offsetA, int offsetB) {
        return new ConditionalNode(instructionName, firstValue, secondValue, offsetA, offsetB);
    }

    //getters and setters


    public int getOffsetB() {
        return offsetB;
    }

    public int getOffsetA() {
        return offsetA;
    }

    public int getSecondValue() {
        return secondValue;
    }

    public int getFirstValue() {
        return firstValue;
    }

    public NodeCondition getCondition() {
        return condition;
    }

    public void setCondition(NodeCondition condition) {
        this.condition = condition;
    }

    @Override
    public String getConnectionLabel() {
        return instructionName;
    }

    @Override
    public String getNodeLabel() {
        return instructionName;
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
