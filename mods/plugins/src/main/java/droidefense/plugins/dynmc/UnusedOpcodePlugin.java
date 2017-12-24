package droidefense.plugins.dynmc;

import droidefense.emulator.machine.inst.DalvikInstruction;
import droidefense.sdk.AbstractDynamicPlugin;
import droidefense.sdk.model.dex.OpcodeInformation;

import java.util.ArrayList;

/**
 * Created by sergio on 31/5/16.
 */
public class UnusedOpcodePlugin extends AbstractDynamicPlugin {

    private static final int THRESHOLD_INSTRUCTIONS = 0;
    private transient ArrayList<Integer> opcodesTypes;
    private int unusedCount;

    @Override
    public void onPreExecute() {
        opcodesTypes = new ArrayList<>();
        //dynamic get a list of unused DalvikInstruction from set
        DalvikInstruction[] instructionSet = DalvikInstruction.values();
        for (int i = 0; i < instructionSet.length; i++) {
            DalvikInstruction inst = instructionSet[i];
            if (inst.description().equals("unused")) {
                opcodesTypes.add(i);
            }
        }
        log("Current DalvikInstruction set has " + opcodesTypes.size() + " unused instructions", 1);
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
    }

    @Override
    public String getPluginName() {
        return "Unused Opcode Plugin";
    }

    @Override
    protected String getResultAsJson() {
        return null;
    }
}
