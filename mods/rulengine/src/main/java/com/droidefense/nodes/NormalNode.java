package com.droidefense.nodes;

import com.droidefense.base.AbstractAtomNode;
import com.droidefense.map.base.AbstractFlowMap;

/**
 * Created by sergio on 10/4/16.
 */
public class NormalNode extends AbstractAtomNode {

    private String key;
    private String value;

    private NormalNode(String instructionName, String key, String value) {
        super(instructionName);
        this.key = key;
        this.value = value;
    }

    public static NormalNode builder(AbstractFlowMap map, String instructionName, String key, String value) {
        //check if a node with this method exist;
        return new NormalNode(instructionName, key, value);
    }

    @Override
    public String getConnectionLabel() {
        return instructionName;
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
