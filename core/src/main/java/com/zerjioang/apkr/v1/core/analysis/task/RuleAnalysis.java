package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.core.cfg.map.BasicCFGFlowMap;
import com.zerjioang.apkr.v1.core.rulengine.RuleEngine;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;

/**
 * Created by sergio on 16/2/16.
 */
public class RuleAnalysis extends AbstractAndroidAnalysis {

    private transient RuleEngine engine;

    public RuleAnalysis() {
        engine = new RuleEngine(FileIOHandler.getRuleEngineDir());
    }

    @Override
    public boolean analyze() {
        Log.write(LoggerType.TRACE, "\n\n --- Running apkr rule engine analysis ---\n\n");
        positiveMatch = false;
        log("loading engine rules", 0);
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
        engine.printResults();
        currentProject.setMatchedRules(engine.getMatchedRules());
        log("Rule scan done", 1);
        this.stop();
        positiveMatch = true;
        return positiveMatch;
    }

    @Override
    public String getName() {
        return "Apkr rule engine";
    }
}
