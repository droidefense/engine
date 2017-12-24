package droidefense.emulator.flow.base;

import droidefense.emulator.machine.base.DynamicUtils;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseField;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseFrame;
import droidefense.emulator.machine.base.struct.generic.IDroidefenseMethod;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.nodes.ConstStrNode;
import droidefense.rulengine.nodes.FieldNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.rulengine.nodes.NormalNode;
import droidefense.entropy.EntropyCalculator;
import droidefense.emulator.machine.inst.DalvikInstruction;
import droidefense.sdk.helpers.DroidDefenseEnvironment;

/**
 * Created by .local on 07/10/2016.
 */
public class NodeGenerator {

    private static final NodeGenerator instance = new NodeGenerator();
    private static DroidDefenseEnvironment environment = DroidDefenseEnvironment.getInstance();

    public static NodeGenerator getInstance() {
        return instance;
    }

    public final MethodNode buildMethodNode(AbstractFlowMap map, DalvikInstruction inst, IDroidefenseFrame frame, IDroidefenseMethod method) {
        String nodeType = environment.classifyNode(method.getOwnerClass().getName());
        String color = environment.classifyNodeColor(method.getOwnerClass().getName(), nodeType);
        return MethodNode.builder(
                map,
                inst.description(),
                method.getName(),
                method.getByteCode().length,
                method.getOwnerClass().getName(),
                method.getTopClass().getName(),
                !method.isFake(),
                DynamicUtils.getParamStringFromDescriptor(method.getDescriptor()),
                DynamicUtils.getReturnTypeFromDescriptor(method.getDescriptor()),
                nodeType,
                color,
                EntropyCalculator.getInstance().getMethodEntropy(method.getOpcodes()),
                frame.getPc(),
                method.isFake()
        );
    }

    public final NormalNode builNormalNode(AbstractFlowMap map, DalvikInstruction currentInstruction) {
        return NormalNode.builder(map, currentInstruction.description(), currentInstruction.code(), currentInstruction.description());
    }

    public final NormalNode builNormalNode(AbstractFlowMap map, DalvikInstruction currentInstruction, String key, String value) {
        return NormalNode.builder(map, currentInstruction.description(), key, value);
    }

    public final FieldNode buildFieldNode(AbstractFlowMap map, DalvikInstruction inst, IDroidefenseField field, int pc) {
        return FieldNode.builder(map, inst.description(), field.getOwnerClass().getName(), field.getName(), field.getType(), pc);
    }

    public ConstStrNode buildStringNode(AbstractFlowMap map, DalvikInstruction DalvikInstruction, IDroidefenseFrame currentFrame, int destination, String str) {
        return ConstStrNode.builder(map, DalvikInstruction.description(), destination, str);
    }
}
