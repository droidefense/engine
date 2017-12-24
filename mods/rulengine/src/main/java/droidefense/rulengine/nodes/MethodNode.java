package droidefense.rulengine.nodes;

import droidefense.rulengine.base.AbstractAtomNode;
import droidefense.rulengine.base.AbstractFlowMap;

public class MethodNode extends AbstractAtomNode {

    protected int instruction_count;
    private String className;
    private String methodName;
    private String params, returnType;
    private String nodeType, color;
    private double entropy;
    private int pc;
    private boolean onscope;
    private boolean reflected;
    private String args;
    private boolean isFakeMethod;
    private String topClassName;

    private MethodNode(String instructionName, String methodName, int instructionCount, String ownerClassName, String topClassName, boolean onScope,
                       String params, String returnType, String nodetype, String color, double entropy, int pc, boolean isFakeMethod) {
        super(instructionName);
        this.methodName = methodName;
        this.instruction_count = instructionCount;
        this.className = ownerClassName; //DynamicUtils.classNameToJava(method.getOwnerClass().getName());
        this.topClassName = topClassName;
        this.pc = pc;
        this.onscope = onScope; //!method.isFake();
        this.params = params; //DynamicUtils.getParamStringFromDescriptor(method.getDescriptor());
        this.returnType = returnType;//DynamicUtils.getReturnTypeFromDescriptor(method.getDescriptor());
        this.isFakeMethod = isFakeMethod;
        this.color = color;
        this.nodeType = nodetype;
        if (this.onscope) {
            //method is real
            this.entropy = entropy; //EntropyCalculator.getInstance().getMethodEntropy(method.getOpcodes());
        } else {
            //faked method. no usable
            this.entropy = 0;
            this.instruction_count = 0;
        }
    }

    private MethodNode(String instructionName, String methodName, int instructionCount, String ownerClassName, String topClassName, boolean onScope,
                       String params, String returnType, String nodetype, String color, double entropy, int pc, String args, boolean isFakeMethod) {
        this(instructionName, methodName, instructionCount, ownerClassName, topClassName, onScope, params, returnType, nodetype, color, entropy, pc, isFakeMethod);
        this.args = args;
    }

    public static MethodNode builder(AbstractFlowMap map, String instructionName, String methodName, int instructionCount, String ownerClassName, String topClassName, boolean onScope,
                                     String params, String returnType, String nodetype, String color, double entropy, int pc, boolean isFakeMethod) {
        //check if a node with this method exist;
        MethodNode temp = new MethodNode(instructionName, methodName, instructionCount, ownerClassName, topClassName, onScope, params, returnType, nodetype, color, entropy, pc, isFakeMethod);
        AbstractAtomNode node = map.getNode(temp.getId());
        MethodNode cn;
        if (node != null) {
            cn = (MethodNode) node;
            return cn;
        }
        return temp;
    }

    public static MethodNode builder(AbstractFlowMap map, String instructionName, String methodName, int instructionCount, String ownerClassName, String topClassName, boolean onScope,
                                     String params, String returnType, String nodetype, String color, double entropy, int pc, boolean isFakeMethod, String args) {
        //check if a node with this method exist;
        MethodNode temp = new MethodNode(instructionName, methodName, instructionCount, ownerClassName, topClassName, onScope, params, returnType, nodetype, color, entropy, pc, args, isFakeMethod);
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
        String dotName = methodName;
        String nodeType = "type";
        if (methodName.equals("<init>") || methodName.equals("<clinit>")) {
            dotName = "new " + this.getSimpleClassName();
        } else {
            dotName = methodName;
        }
        setType(nodeType);

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

    private String getSimpleClassName() {
        String name = this.className;
        if (className.contains("/")) {
            String[] splitted = name.split("/");
            return splitted[splitted.length - 1];
        }
        return name;
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
            if (isFakeMethod) {
                style = "color=Black, fontcolor=Black, fontname=Courier, fontsize=15, shape=Mrecord";
            } else {
                if (methodName.equals("<init>"))
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

    public String getKey() {
        return className + "." + methodName;
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

    public int getInstruction_count() {
        return instruction_count;
    }

    public String getArgs() {
        return args;
    }

    public String getTopClassName() {
        return topClassName;
    }

    public void setTopClassName(String topClassName) {
        this.topClassName = topClassName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
