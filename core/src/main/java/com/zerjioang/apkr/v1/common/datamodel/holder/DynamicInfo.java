package com.zerjioang.apkr.v1.common.datamodel.holder;

import com.zerjioang.apkr.v1.core.rulengine.Rule;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sergio on 7/6/16.
 */
public class DynamicInfo implements Serializable {

    /**
     * Matched dynamic rules
     */
    private ArrayList<Rule> matchedRules;

    /**
     * Classified strings and counters
     */
    private StringAnalysis stringAnalysisResult;

    public ArrayList<Rule> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(ArrayList<Rule> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public StringAnalysis getStringAnalysisResult() {
        return stringAnalysisResult;
    }

    public void setStringAnalysisResult(StringAnalysis stringAnalysisResult) {
        this.stringAnalysisResult = stringAnalysisResult;
    }
}
