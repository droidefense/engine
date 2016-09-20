package com.zerjioang.apkr.v1.core.cfg.nodes;

import apkr.external.module.entropy.EntropyCalculator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerjioang.apkr.temp.ApkrIntelligence;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.DynamicUtils;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomMethod;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;

/**
 * Created by sergio on 31/3/16.
 */
public class MethodNode extends AbstractAtomNode {

    private final String className;
    private final String methodName;
    private final String params, returnType;
    private final double entropy;
    @JsonIgnore
    private final transient IAtomMethod method;
    protected int instruction_count;
    private int pc;
    private boolean onscope;
    private int[] instructions;
    private boolean reflected;
    private String args;

    private MethodNode(IAtomMethod method, int pc) {
        super();
        this.method = method;
        this.instructions = method.getOpcodes();
        this.instruction_count = instructions.length;
        this.methodName = method.getName();
        this.className = DynamicUtils.classNameToJava(method.getOwnerClass().getName());
        this.pc = pc;
        this.onscope = !method.isFake();
        this.params = DynamicUtils.getParamStringFromDescriptor(method.getDescriptor());
        this.returnType = DynamicUtils.getReturnTypeFromDescriptor(method.getDescriptor());
        if (this.onscope) {
            //method is real
            this.entropy = EntropyCalculator.getInstance().getMethodEntropy(method.getOpcodes());
        } else {
            //faked method. no usable
            this.entropy = 0;
            this.instructions = null;
            this.instruction_count = 0;
        }
    }

    private MethodNode(IAtomMethod method, int pc, String args) {
        this(method, pc);
        this.args = args;
    }

    public static MethodNode builder(AbstractFlowMap map, IAtomMethod method, int pc) {
        //check if a node with this method exist;
        MethodNode temp = new MethodNode(method, pc);
        AbstractAtomNode node = map.getNode(temp.getId());
        MethodNode cn;
        if (node != null) {
            cn = (MethodNode) node;
            return cn;
        }
        return temp;
    }

    public static MethodNode builder(AbstractFlowMap map, IAtomMethod method, int pc, String args) {
        //check if a node with this method exist;
        MethodNode temp = new MethodNode(method, pc, args);
        AbstractAtomNode node = map.getNode(temp.getId());
        MethodNode cn;
        if (node != null) {
            cn = (MethodNode) node;
            return cn;
        }
        return temp;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public boolean isOnscope() {
        return onscope;
    }

    public int[] getInstructions() {
        return instructions;
    }

    public void setInstructions(int[] instructions) {
        this.instructions = instructions;
    }

    public boolean isReflected() {
        return reflected;
    }

    public void setReflected(boolean reflected) {
        this.reflected = reflected;
    }

    @Override
    public String getConnectionLabel() {
        return "";
    }

    @Override
    public String getNodeLabel() {
        //get node label
        String dotName = getMethodName();
        if (getMethodName().endsWith("<init>")) {
            dotName = "new " + Util.getClassNameForFullPath(getClassName());
        } else if (getMethodName().endsWith("<clinit>")) {
            dotName = "new " + Util.getClassNameForFullPath(getClassName());
        } else {
            dotName = Util.quote(dotName);
        }
        String nodeType = ApkrIntelligence.getInstance().classifyNode(method, getClassName(), getMethodName());
        setType(nodeType);
        String color = ApkrIntelligence.getInstance().classifyNodeColor(getClassName(), getMethodName(), this);
        /*if(args!=null && args.length()>0)
            return nodeType+" | "+Util.quote(getClassName())+" |"+Util.quote(getMethodName())+"|"+args;
        else
            return nodeType+" | "+Util.quote(getClassName())+" |"+Util.quote(getMethodName());*/

        String arguments = " ";
        String html =
                "<<table border=\"2\" cellspacing=\"0\">" +
                        "<tr><td port=\"port1\" border=\"2\" bgcolor=\"" + color + "\">" + nodeType + "</td></tr>" +
                        "<tr><td port=\"port2\" border=\"1\">" + getClassName() + "</td></tr>" +
                        "<tr><td port=\"port3\" border=\"1\">" + dotName + "()" + "</td></tr>" +
                        "<tr><td port=\"port4\" border=\"1\">" + arguments + "</td></tr>" +
                        "</table>>";
        return html;
    }

    @Override
    public String getConnectionStyle() {
        return "color=Black, fontcolor=Blue, fontname=Courier, fontsize=15, penwidth = " + normalizedSize(in);
    }

    private double normalizedSize(int in) {
        int in_min = 1;
        int out_max = 12;
        int out_min = 1;
        int in_max = maxInt;
        return (in - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        /*int maxArrowSize = 100;
        int min = 1;
        int max = maxArrowSize;
        double normalized = (in - min)/(double)(max-min);
        return normalized;*/
    }

    @Override
    public String getNodeStyle() {
        String style = "";
        //set node style
        if (isReflected()) {
            style = "color=Red, fontcolor=Red, fontname=Courier, fontsize=15, shape=Mrecord";
        } else {
            if (method.isFake()) {
                style = "color=Black, fontcolor=Black, fontname=Courier, fontsize=15, shape=Mrecord";
            } else {
                if (method.getName().equals("<init>"))
                    style = "color=Orange, fontcolor=Orange, fontname=Courier, fontsize=15, shape=Mrecord";
                else
                    style = "color=Blue, fontcolor=Blue, fontname=Courier, fontsize=15, shape=Mrecord";
            }
        }
        return "";
        //return style;
    }

    @Override
    public boolean isDrawable() {
        return true;
        /*
        boolean isInvalid = getClassName().equals("java.lang.Object")
                || ApkrIntelligence.getInstance().isAndroidRclass(getClassName());

        return !isInvalid;
        */
    }

    @Override
    public String getAsDotGraph() {
        return "\t" + getId() + " [label=" + getNodeLabel() + "];\n";
    }

    public String getReturnType() {
        return returnType;
    }

    public String getParams() {
        return params;
    }

    public double getEntropy() {
        return entropy;
    }

    public IAtomMethod getMethod() {
        return method;
    }

    public int getInstruction_count() {
        return instruction_count;
    }

    public String getArgs() {
        return args;
    }
}
