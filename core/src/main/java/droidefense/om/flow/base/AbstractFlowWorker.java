package droidefense.om.flow.base;

import com.droidefense.NodeConnection;
import com.droidefense.base.AbstractAtomNode;
import com.droidefense.map.base.AbstractFlowMap;
import com.droidefense.nodes.FieldNode;
import com.droidefense.nodes.MethodNode;
import com.droidefense.nodes.NormalNode;
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

    private final static NodeGenerator generator = NodeGenerator.getInstance();
    protected static AbstractFlowMap flowMap;
    protected AbstractAtomNode fromNode;
    protected AbstractAtomNode toNode;

    public AbstractFlowWorker(DalvikVM vm, DroidefenseProject currentProject) {
        super(vm, currentProject);
    }

    protected final MethodNode buildMethodNode(DalvikInstruction inst, IAtomFrame frame, IAtomMethod method) {
        return generator.buildMethodNode(flowMap, inst, frame, method);
    }

    protected final NormalNode builNormalNode(DalvikInstruction currentInstruction) {
        return generator.builNormalNode(flowMap, currentInstruction);
    }

    protected final NormalNode builNormalNode(DalvikInstruction currentInstruction, String key, String value) {
        return generator.builNormalNode(flowMap, currentInstruction, key, value);
    }

    protected final FieldNode buildFieldNode(DalvikInstruction inst, IAtomField field, int pc) {
        return generator.buildFieldNode(flowMap, inst, field, pc);
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
}
