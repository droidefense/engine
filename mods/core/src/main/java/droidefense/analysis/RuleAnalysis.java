package droidefense.analysis;

import droidefense.rulengine.Rule;
import droidefense.rulengine.RuleEngine;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.rulengine.map.BasicCFGFlowMap;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.FileIOHandler;

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
        Log.write(LoggerType.TRACE, "\n\n --- Running Droidefense rule droidefense.droidefense.om analysis ---\n\n");
        log("loading droidefense.droidefense.om rules", 0);
        log("Current rules: " + engine.getRuleCount(), 1);
        log("Preparing to scan...", 1);
        BasicCFGFlowMap flowmap = currentProject.getNormalControlFlowMap();
        if (flowmap != null) {
            log("Scanning...", 2);
            engine.analyzeFlow(flowmap);
            log("Collecting results...", 1);
            printMatchedRules(engine.getMatchedRules());
            currentProject.setMatchedRules(engine.getMatchedRules());
            log("Rule scan done", 1);
            executionSuccessful = engine!=null && engine.getRuleCount() > 0;
        } else {
            log("Scan aborted: no flowmap information", 2);
            executionSuccessful = false;
        }
        return executionSuccessful;
    }

    private void printMatchedRules(ArrayList<Rule> matchedRules) {
        if (matchedRules != null && !matchedRules.isEmpty()) {
            for (Rule r : matchedRules) {
                Log.write(LoggerType.INFO, "Matched rule: " + r.getDesc());
                Log.write(LoggerType.INFO, "Rule details:" + r.toString());
            }
        }
    }

    @Override
    public String getName() {
        return "Droidefense rule engine";
    }
}
