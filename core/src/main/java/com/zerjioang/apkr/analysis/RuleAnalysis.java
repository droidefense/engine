package com.zerjioang.apkr.analysis;

import apkr.external.modules.controlflow.model.map.BasicCFGFlowMap;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.rulengine.Rule;
import apkr.external.modules.rulengine.RuleEngine;
import com.zerjioang.apkr.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.handler.FileIOHandler;

import java.util.ArrayList;

/**
 * Created by sergio on 16/2/16.
 */
public final class RuleAnalysis extends AbstractAndroidAnalysis {

    private transient RuleEngine engine;

    public RuleAnalysis() {
        engine = new RuleEngine(FileIOHandler.getRuleEngineDir());
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running apkr rule om analysis ---\n\n");
        positiveMatch = false;
        log("loading om rules", 0);
        log("Current rules: " + engine.getRuleCount(), 1);
        log("Preparing to scan...", 1);
        BasicCFGFlowMap flowmap = currentProject.getNormalControlFlowMap();
        if (flowmap != null) {
            log("Scanning...", 2);
            engine.analyzeFlow(flowmap);
        } else {
            log("Scan aborted: no flowmap information", 2);
        }
        log("Collecting results...", 1);
        printMatchedRules(engine.getMatchedRules());
        currentProject.setMatchedRules(engine.getMatchedRules());
        log("Rule scan done", 1);
        this.stop();
        positiveMatch = true;
        return positiveMatch;
    }

    private void printMatchedRules(ArrayList<Rule> matchedRules) {
        if (matchedRules != null && !matchedRules.isEmpty()) {
            for (Rule r : matchedRules) {
                Log.write(LoggerType.INFO, "Matched rule: " + r.getName());
            }
        }
    }

    @Override
    public String getName() {
        return "Apkr rule om";
    }
}
