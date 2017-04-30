package droidefense.ml;

import java.io.Serializable;
import java.util.ArrayList;


public class MachineLearningResult implements Serializable {

    private ArrayList<MLResult> results;
    private int positives;
    private int total;
    private double ratio;

    public MachineLearningResult() {
        results = new ArrayList<>();
    }

    public void add(String name, double value) {
        String res = getValue(value);
        results.add(new MLResult(name, res, value));
    }

    private String getValue(double value) {
        if (value == 1.0) {
            return "Goodware";
        } else if (value == 0.0) {
            return "Malware";
        } else {
            return "Unclassified";
        }
    }

    public int getPositives() {
        return positives;
    }

    public void setPositives(int positives) {
        this.positives = positives;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
