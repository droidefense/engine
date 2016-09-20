package com.zerjioang.apkr.v1.core.rulengine;

import com.zerjioang.apkr.temp.ApkrIntelligence;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerFilter;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerHandler;
import com.zerjioang.apkr.v1.core.cfg.base.AbstractAtomNode;
import com.zerjioang.apkr.v1.core.cfg.map.BasicCFGFlowMap;
import com.zerjioang.apkr.v1.core.cfg.nodes.EntryPointNode;
import com.zerjioang.apkr.v1.core.cfg.nodes.MethodNode;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by sergio on 6/6/16.
 */
public class RuleEngine {

    private ArrayList<Rule> dataset;
    private File ruleDir;

    private ArrayList<AbstractAtomNode> nodeList;
    private ArrayList<Rule> matchedRules;

    public RuleEngine(File ruleDir) {
        this.ruleDir = ruleDir;
        nodeList = new ArrayList<>();
        matchedRules = new ArrayList<>();
        readDir();
    }

    private void readDir() {
        DirScannerHandler scanner = new DirScannerHandler(ruleDir, false, new DirScannerFilter() {
            @Override
            public boolean addFile(File f) {
                return f.getName().endsWith(".rule");
            }
        });
        scanner.doTheJob();
        ArrayList<ResourceFile> files = scanner.getFiles();
        dataset = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                String base = new String(Files.readAllBytes(Paths.get(files.get(i).getAbsolutePath())));
                Rule r = new Rule(base, files.get(i).getFilename());
                dataset.add(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void analyzeFlow(BasicCFGFlowMap flowMap) {
        for (Rule rule : dataset) {
            Log.write(LoggerType.TRACE, rule.toString());
            boolean result = processRule(rule, flowMap);
            if (result) {
                if (!matchedRules.contains(rule))
                    this.matchedRules.add(rule);
            }
        }
    }

    private boolean processRule(Rule rule, BasicCFGFlowMap flowMap) {
        //get initial node
        ArrayList<AbstractAtomNode> nodes = getInitialNode(flowMap, rule);
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

    private ArrayList<AbstractAtomNode> getInitialNode(BasicCFGFlowMap flowMap, Rule rule) {
        ArrayList<AbstractAtomNode> returnList = new ArrayList<>();
        ArrayList<AbstractAtomNode> nodelist = flowMap.getNodeList();
        parseRuleInNode(rule, returnList, nodelist, 0);
        return returnList;
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
                    boolean sameMethodName = mn.getMethod().getName().equals(data[1]) || checkByRegex(mn.getMethod().getName(), methodMask);
                    if (sameMethodName) {
                        //check class name
                        String parentClass = ApkrIntelligence.getInstance().getSimpleNodeType(mn.getMethod());
                        boolean sameParentClass = false;
                        if (parentClass.equals("Object")) {
                            //check class name
                            sameParentClass = Util.getClassNameForFullPath(mn.getMethod().getOwnerClass().getName()).equals(data[0]) || checkByRegex(parentClass, data[0]);
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

    public void printResults() {
        Log.write(LoggerType.DEBUG, "---- RULE ENGINE SCAN RESULTS ----");
        for (Rule r : matchedRules) {
            Log.write(LoggerType.TRACE, "Matched dynamic rule: " + r.getName());
        }
        Log.write(LoggerType.DEBUG, "---- RULE SCAN DONE ----");
    }

    public ArrayList<Rule> getMatchedRules() {
        return matchedRules;
    }
}
