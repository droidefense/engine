package droidefense.sdk.model.holder;

import droidefense.sdk.model.enums.SDK_VERSION;

import java.io.Serializable;

/**
 * Created by .local on 05/11/2016.
 */
public class SDKCompatibility implements Serializable{

    private SDK_VERSION minimum;
    private SDK_VERSION maximum;
    private SDK_VERSION target;

    public SDK_VERSION getMinimum() {
        return minimum;
    }

    public void setMinimum(SDK_VERSION minimum) {
        this.minimum = minimum;
    }

    public SDK_VERSION getMaximum() {
        return maximum;
    }

    public void setMaximum(SDK_VERSION maximum) {
        this.maximum = maximum;
    }

    public SDK_VERSION getTarget() {
        return target;
    }

    public void setTarget(SDK_VERSION target) {
        this.target = target;
    }
}
