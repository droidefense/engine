package droidefense.om.flow.base;

import apkr.external.module.entropy.EntropyCalculator;
import com.droidefense.map.base.AbstractFlowMap;
import com.droidefense.nodes.ConstStrNode;
import com.droidefense.nodes.FieldNode;
import com.droidefense.nodes.MethodNode;
import com.droidefense.nodes.NormalNode;
import droidefense.om.machine.base.DynamicUtils;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.om.machine.inst.DalvikInstruction;

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
