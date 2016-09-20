package com.zerjioang.apkr.v1.core.ml;

import java.io.Serializable;

/**
 * Created by sergio on 14/6/16.
 */
public class MLResult implements Serializable {

    private String name, result;
    private double value;

    public MLResult(String name, String result, double value) {
        this.name = name;
        this.result = result;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
