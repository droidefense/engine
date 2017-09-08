package droidefense.sdk.model.holder;

import droidefense.rulengine.Rule;

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
    private StringInfo stringAnalysisResult;

    public ArrayList<Rule> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(ArrayList<Rule> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public StringInfo getStringAnalysisResult() {
        return stringAnalysisResult;
    }

    public void setStringAnalysisResult(StringInfo stringAnalysisResult) {
        this.stringAnalysisResult = stringAnalysisResult;
    }
}
