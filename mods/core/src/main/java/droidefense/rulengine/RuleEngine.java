package droidefense.rulengine;

import droidefense.rulengine.base.AbstractAtomNode;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.rulengine.nodes.EntryPointNode;
import droidefense.rulengine.nodes.MethodNode;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RuleEngine {

    private ArrayList<Rule> dataset;
    private File ruleDir;

    private ArrayList<AbstractAtomNode> nodeList;
    private ArrayList<Rule> matchedRules;

    public RuleEngine(File ruleDir) throws IOException {
        this.ruleDir = ruleDir;
        nodeList = new ArrayList<>();
        matchedRules = new ArrayList<>();
        readDir(ruleDir.listFiles());
    }

    private void readDir(File[] files) throws IOException {
        dataset = new ArrayList<>();
        if(files!=null){
            for (int i = 0; i < files.length; i++) {
                String base = new String(Files.readAllBytes(Paths.get(files[i].getAbsolutePath())));
                Rule r = new Rule(base, files[i].getName());
                dataset.add(r);
            }
        }
        else{
            Log.write(LoggerType.INFO, "No rules available on folder"+ruleDir.getAbsolutePath());
        }
    }

    public void analyzeFlow(AbstractFlowMap flowMap) {
        for (Rule rule : dataset) {
            boolean result = processRule(rule, flowMap);
            if (result) {
                if (!matchedRules.contains(rule))
                    this.matchedRules.add(rule);
            }
        }
    }

    public boolean processRule(Rule rule, AbstractFlowMap flowMap) {
        //get initial node
        ArrayList<AbstractAtomNode> nodes = getInitialNode(flowMap.getNodeList(), rule);
        if (nodes == null || nodes.size() == 0) {
            return false;
        } else {
            boolean result;
            for (AbstractAtomNode node : nodes) {
                result = processNode(node, " ", rule, 0);
                if (result) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean processNode(AbstractAtomNode node, String separator, Rule rule, int currentLevel) {
        boolean anyMatch = false;
        if (node != null && currentLevel < rule.getNodeCount()) {
            currentLevel++;
            ArrayList<AbstractAtomNode> nodes = parseRuleInNode(rule, new ArrayList<>(), node.getOutNodes(), currentLevel);
            if (currentLevel == rule.getNodeCount() - 1) {
                //scan in deep finished. check results
                anyMatch |= (nodes != null && nodes.size() > 0);
            } else {
                for (AbstractAtomNode n : nodes) {
                    if (anyMatch)
                        return true;
                    anyMatch |= processNode(n, separator + " ", rule, currentLevel);
                }
            }
        }
        return anyMatch;
    }

    private ArrayList<AbstractAtomNode> getInitialNode(final ArrayList<AbstractAtomNode> nodelist, Rule rule) {
        if(nodelist!=null && !nodelist.isEmpty()){
            ArrayList<AbstractAtomNode> returnList = new ArrayList<>();
            return parseRuleInNode(rule, returnList, nodelist, 0);
        }
        return new ArrayList<>();
    }

    private ArrayList<AbstractAtomNode> parseRuleInNode(Rule rule, ArrayList<AbstractAtomNode> returnList, ArrayList<AbstractAtomNode> nodelist, int idx) {
        if (nodelist == null || nodelist.size() == 0) {
            return new ArrayList<>();
        }

        String name = rule.getNode(idx);
        String[] data = rule.intelligentSplit(idx);
        for (AbstractAtomNode node : nodelist) {
            if (name.equals("Entry")) {
                if (node instanceof EntryPointNode) {
                    returnList.add(node);
                    break;
                } else {

                }
            } else {
                if (node instanceof MethodNode) {
                    MethodNode mn = (MethodNode) node;
                    String methodMask = data[1];
                    if (methodMask.equals("CONSTRUCTOR")) {
                        methodMask = "<init> | <clinit>";
                    } else if (methodMask.equals("ANY")) {
                        methodMask = "[^<>]+";
                    }
                    boolean sameMethodName = mn.getMethodName().equals(data[1]) || checkByRegex(mn.getMethodName(), methodMask);
                    if(sameMethodName) {
                        //current method name matches rule method name
                        if(data[0].equals("Object")){
                            returnList.add(node);
                        }
                        else if(data[0].equals(mn.getClassName())){
                            returnList.add(node);
                        }
                        else {
                            //check class name
                            String parentClass = mn.getTopClassName(); //ApkrIntelligence.getInstance().getSimpleNodeType(mn.getMethod());
                            boolean sameParentClass = false;
                            if (parentClass.equals("Object")) {
                                //check class name
                                sameParentClass = NodeCalculator.getClassNameForFullPath(mn.getClassName()).equals(data[0]) || checkByRegex(parentClass, data[0]);
                            } else {
                                //compare parent class
                                sameParentClass = parentClass.equals(data[0]) || checkByRegex(parentClass, data[0]);
                            }
                            if (sameParentClass) {
                                returnList.add(node);
                            }
                        }
                    }
                }
            }
        }
        return returnList;
    }

    private boolean checkByRegex(String name, String methodMask) {
        try {
            return name.matches(methodMask);
        } catch (Exception e) {
            return false;
        }
    }

    public int getRuleCount() {
        return dataset.size();
    }

    public ArrayList<Rule> getMatchedRules() {
        return matchedRules;
    }
}
