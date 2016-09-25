package com.zerjioang.apkr.v1.core.cfg.nodes;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.base.NodeCondition;

/**
 * Created by sergio on 10/4/16.
 */
public class ConditionalNode extends AbstractAtomNode {

    private final Instruction inst;
    private final int firstValue;
    private final int secondValue;
    private final int offsetA;
    private final int offsetB;

    private NodeCondition condition;

    private ConditionalNode(Instruction inst, int firstValue, int secondValue, int offsetA, int offsetB) {
        super();
        this.inst = inst;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
        this.offsetA = offsetA;
        this.offsetB = offsetB;
    }

    public static ConditionalNode builder(Instruction inst, int firstValue, int secondValue, int offsetA, int offsetB) {
        return new ConditionalNode(inst, firstValue, secondValue, offsetA, offsetB);
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
        return inst.description();
    }

    @Override
    public String getNodeLabel() {
        return inst.description();
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
