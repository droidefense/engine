package com.zerjioang.apkr.v1.core.cfg.nodes;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;

/**
 * Created by sergio on 10/4/16.
 */
public class NormalNode extends AbstractAtomNode {


    private Instruction instruction;
    private String key;
    private String value;

    private NormalNode(Instruction instruction, String key, String value) {
        this.instruction = instruction;
        this.key = key;
        this.value = value;
    }

    public static NormalNode builder(AbstractFlowMap map, Instruction instruction, String key, String value) {
        //check if a node with this method exist;
        return new NormalNode(instruction, key, value);
    }

    @Override
    public String getConnectionLabel() {
        return instruction.description();
    }

    @Override
    public String getNodeLabel() {
        //return key + " " + value;
        return key;
    }

    @Override
    public String getConnectionStyle() {
        return "color=Black, fontcolor=Blue, fontname=Courier, fontsize=15";
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
