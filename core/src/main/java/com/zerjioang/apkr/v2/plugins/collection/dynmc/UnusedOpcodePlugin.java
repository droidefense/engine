package com.zerjioang.apkr.v2.plugins.collection.dynmc;


import com.zerjioang.apkr.v1.common.datamodel.dex.OpcodeInformation;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrDynamicPlugin;

import java.util.ArrayList;

/**
 * Created by sergio on 31/5/16.
 */
public class UnusedOpcodePlugin extends AbstractApkrDynamicPlugin {

    private static final int THRESHOLD_INSTRUCTIONS = 3;
    private transient ArrayList<Integer> opcodesTypes;
    private int unusedCount;

    @Override
    public void onPreExecute() {
        opcodesTypes = new ArrayList<>();
        //dynamic get a list of unused instruction from set
        Instruction[] instructionSet = Instruction.values();
        for (int i = 0; i < instructionSet.length; i++) {
            Instruction inst = instructionSet[i];
            if (inst.description().equals("unused")) {
                opcodesTypes.add(i);
            }
        }
        log("Current instruction set has " + opcodesTypes.size() + " unused instructions", 1);
    }

    @Override
    public void onExecute() {
        log("Looking for unused instructions", 1);
        OpcodeInformation opcodes = currentProject.getOpcodeInfo();
        if (opcodes != null) {
            int[] count = opcodes.getOpcodesCount();
            for (Integer opcode : opcodesTypes) {
                unusedCount += count[opcode] > THRESHOLD_INSTRUCTIONS ? 1 : 0;
            }
            positiveMatch = unusedCount != 0;
        } else {
            positiveMatch = false;
        }
    }

    @Override
    protected void postExecute() {
        if (positiveMatch) {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-red\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">Detected " + unusedCount + " unused opcodes (TR=" + THRESHOLD_INSTRUCTIONS + ")</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        } else {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-green\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">No unused opcode detected</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        }
    }

    @Override
    protected String getPluginName() {
        return "Unused Opcode Plugin";
    }

    @Override
    protected String getResultAsJson() {
        return null;
    }
}
