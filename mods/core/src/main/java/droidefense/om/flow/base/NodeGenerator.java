package droidefense.om.flow.base;

import droidefense.entropy.EntropyCalculator;
import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.nodes.ConstStrNode;
import droidefense.rulengine.nodes.FieldNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.rulengine.nodes.NormalNode;

/**
 * Created by .local on 07/10/2016.
 */
public class NodeGenerator {

    private static final NodeGenerator instance = new NodeGenerator();

    public static NodeGenerator getInstance() {
        return instance;
    }

    public final MethodNode buildMethodNode(AbstractFlowMap map, DalvikInstruction inst, IAtomFrame frame, IAtomMethod method) {
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

    public final FieldNode buildFieldNode(AbstractFlowMap map, DalvikInstruction inst, IAtomField field, int pc) {
        return FieldNode.builder(map, inst.description(), field.getOwnerClass().getName(), field.getName(), field.getType(), pc);
    }

    public ConstStrNode buildStringNode(AbstractFlowMap map, DalvikInstruction DalvikInstruction, IAtomFrame currentFrame, int destination, String str) {
        return ConstStrNode.builder(map, DalvikInstruction.description(), destination, str);
    }
}
