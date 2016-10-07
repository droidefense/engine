package com.zerjioang.apkr.analysis.flow;

import apkr.external.module.entropy.EntropyCalculator;
import apkr.external.modules.controlflow.model.map.base.AbstractFlowMap;
import apkr.external.modules.controlflow.model.nodes.ConstStrNode;
import apkr.external.modules.controlflow.model.nodes.FieldNode;
import apkr.external.modules.controlflow.model.nodes.MethodNode;
import apkr.external.modules.controlflow.model.nodes.NormalNode;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.DynamicUtils;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomFrame;
import com.zerjioang.apkr.analysis.dynamicscan.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.analysis.dynamicscan.machine.inst.Instruction;

/**
 * Created by .local on 07/10/2016.
 */
public class NodeGenerator {

    private static final NodeGenerator instance = new NodeGenerator();

    public static NodeGenerator getInstance() {
        return instance;
    }

    public final MethodNode buildMethodNode(AbstractFlowMap map, Instruction inst, IAtomFrame frame, IAtomMethod method) {
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

    public final NormalNode builNormalNode(AbstractFlowMap map, Instruction currentInstruction) {
        return NormalNode.builder(map, currentInstruction.description(), currentInstruction.code(), currentInstruction.description());
    }

    public final NormalNode builNormalNode(AbstractFlowMap map, Instruction currentInstruction, String key, String value) {
        return NormalNode.builder(map, currentInstruction.description(), key, value);
    }

    public final FieldNode buildFieldNode(AbstractFlowMap map, Instruction inst, IAtomField field, int pc) {
        return FieldNode.builder(map, inst.description(), field.getOwnerClass().getName(), field.getName(), field.getType(), pc);
    }

    public ConstStrNode buildStringNode(AbstractFlowMap map, Instruction instruction, IAtomFrame currentFrame, int destination, String str) {
        return ConstStrNode.builder(map, instruction.description(), destination, str);
    }
}
