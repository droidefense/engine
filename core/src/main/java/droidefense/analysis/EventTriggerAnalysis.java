package droidefense.analysis;

import droidefense.analysis.base.AbstractAndroidAnalysis;

/**
 * Created by .local on 23/10/2016.
 */
public class EventTriggerAnalysis extends AbstractAndroidAnalysis {

    public EventTriggerAnalysis() {
    }

    @Override
    protected boolean analyze() {
        return false;
    }


    @Override
    public String getName() {
        return "Event Trigger Analysis";
    }
}
