package com.zerjioang.apkr.sdk.model.dex;

import java.io.Serializable;

/**
 * Created by sergio on 28/5/16.
 */
public class OpcodeInformation implements Serializable {

    private int[] opcodesCount;
    private int instructionCount;

    public int[] getOpcodesCount() {
        return opcodesCount;
    }

    public void setOpcodesCount(int[] opcodesCount) {
        this.opcodesCount = opcodesCount;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public void setInstructionCount(int instructionCount) {
        this.instructionCount = instructionCount;
    }
}
