package droidefense.sdk.model.holder;


import apkr.external.modules.rulengine.Rule;

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
    private StringAnalysisResultModel stringAnalysisResult;

    public ArrayList<Rule> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(ArrayList<Rule> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public StringAnalysisResultModel getStringAnalysisResult() {
        return stringAnalysisResult;
    }

    public void setStringAnalysisResult(StringAnalysisResultModel stringAnalysisResult) {
        this.stringAnalysisResult = stringAnalysisResult;
    }
}
