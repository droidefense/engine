package droidefense.om.flow.base;

import droidefense.rulengine.base.NodeConnection;
import droidefense.rulengine.base.AbstractAtomNode;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.nodes.FieldNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.rulengine.nodes.NormalNode;
import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.sdk.model.base.DroidefenseProject;

/**
 * Created by .local on 07/10/2016.
 */
public abstract class AbstractFlowWorker extends AbstractDVMThread {

    private final static NodeGenerator reporting = NodeGenerator.getInstance();
    protected static AbstractFlowMap flowMap;
    protected AbstractAtomNode fromNode;
    protected AbstractAtomNode toNode;

    public AbstractFlowWorker(DalvikVM vm, DroidefenseProject currentProject) {
        super(currentProject);
    }

    protected final MethodNode buildMethodNode(DalvikInstruction inst, IAtomFrame frame, IAtomMethod method) {
        return reporting.buildMethodNode(flowMap, inst, frame, method);
    }

    protected final NormalNode builNormalNode(DalvikInstruction currentInstruction) {
        return reporting.builNormalNode(flowMap, currentInstruction);
    }

    protected final NormalNode builNormalNode(DalvikInstruction currentInstruction, String key, String value) {
        return reporting.builNormalNode(flowMap, currentInstruction, key, value);
    }

    protected final FieldNode buildFieldNode(DalvikInstruction inst, IAtomField field, int pc) {
        return reporting.buildFieldNode(flowMap, inst, field, pc);
    }

    protected final void createNewConnection(AbstractAtomNode from, AbstractAtomNode to, DalvikInstruction currentInstruction) {
        from = flowMap.addNode(from);
        to = flowMap.addNode(to);
        //avoid connections with itself
        if (true || !from.getConnectionLabel().equals(to.getConnectionLabel())) {
            NodeConnection conn = new NodeConnection(from, to, currentInstruction.description());
            flowMap.addConnection(conn);
        }
    }

    protected boolean isVoidInstruction(int currentInstructionOpcode) {
        return currentInstructionOpcode >= 0xE && currentInstructionOpcode <= 0x11;
    }

    protected boolean isNOPInstruction(int currentInstructionOpcode) {
        return currentInstructionOpcode == 0x00;
    }

    protected boolean isCallMethodInstruction(int currentInstructionOpcode) {
        return (currentInstructionOpcode >= 0x6E && currentInstructionOpcode <= 0x78) || (currentInstructionOpcode == 0xF0) || (currentInstructionOpcode >= 0xF8 && currentInstructionOpcode <= 0xFB);
    }

    protected boolean isGetterOrSetterInstruction(int currentInstructionOpcode) {
        return currentInstructionOpcode >= 0x44 && currentInstructionOpcode <= 0x6D;
    }
}
