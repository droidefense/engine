package com.zerjioang.apkr.v1.core.cfg.nodes;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomField;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.inst.Instruction;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;

/**
 * Created by sergio on 10/4/16.
 */
public class FieldNode extends AbstractAtomNode {

    private final String className;
    private final String fieldName;
    private final String type;

    private final Instruction inst;

    private transient final IAtomField field;
    private final int pc;

    public FieldNode(Instruction inst, IAtomField field, int pc) {
        super();
        this.inst = inst;
        this.field = field;
        this.pc = pc;
        className = field.getOwnerClass().getName();
        fieldName = field.getName();
        type = field.getType();
    }

    public static FieldNode builder(AbstractFlowMap map, Instruction inst, IAtomField field, int pc) {
        //check if a node with this method exist;
        AbstractAtomNode node = map.getNode(-1);
        if (node != null) {
            return (FieldNode) node;
        }
        return new FieldNode(inst, field, pc);
    }

    @Override
    public String getConnectionLabel() {
        return inst.description();
    }

    @Override
    public String getNodeLabel() {
        return (className + "." + fieldName).replace("/", ".");
    }

    @Override
    public String getConnectionStyle() {
        return "";
    }

    @Override
    public String getNodeStyle() {
        return "";
    }

    @Override
    public boolean isDrawable() {
        return true;
    }
}
