package com.zerjioang.apkr.v1.core.cfg.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;

/**
 * Created by sergio on 10/4/16.
 */
public class ConstStrNode extends AbstractAtomNode {

    @JsonIgnore
    private transient IAtomFrame currentFrameObject;
    @JsonIgnore
    private Instruction currentInstruction;
    private int dest;
    private String data;

    private ConstStrNode(IAtomFrame currentFrameObject, Instruction currentInstruction, int dest, String data) {
        super();
        this.currentFrameObject = currentFrameObject;
        this.currentInstruction = currentInstruction;
        this.dest = dest;
        this.data = data;
    }

    public static ConstStrNode builder(AbstractFlowMap map, IAtomFrame currentFrameObject, Instruction currentInstruction, int dest, String str) {
        //check if a node with this method exist;
        AbstractAtomNode node = map.getNode(-1);
        if (node != null)
            return (ConstStrNode) node;
        return new ConstStrNode(currentFrameObject, currentInstruction, dest, str);
    }

    @Override
    public String getConnectionLabel() {
        return currentInstruction.description();
    }

    @Override
    public String getNodeLabel() {
        if (data == null || data.isEmpty())
            return "v" + dest + " = EMPTY";
        return "v" + dest + " = " + Util.quote(data);
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
