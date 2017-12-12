package droidefense.ml;

import java.io.Serializable;
import java.util.ArrayList;

public class MLResult extends ArrayList implements Serializable {

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
