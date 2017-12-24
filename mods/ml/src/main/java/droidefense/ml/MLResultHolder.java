package droidefense.ml;

import java.io.Serializable;
import java.util.ArrayList;


public class MLResultHolder implements Serializable {

    private static final String GOODWARE = "Goodware";
    private static final String MALWARE = "Malware";
    private static final String UNCLASSIFIED = "Unclassified";

    private int positives;
    private int total;
    private double malwareRatio;
    private ArrayList<MLResult> results;

    public MLResultHolder() {
        this.results = new ArrayList<>();
        this.total = 0;
        this.positives = 0;
        this.malwareRatio = 0;
    }

    public void add(String name, double value) {
        String res = getValue(value);
        results.add(new MLResult(name, res, value));
        this.total++;
    }

    private String getValue(double value) {
        if (value == 1.0) {
            return GOODWARE;
        } else if (value == 0.0) {
            this.positives++;
            return MALWARE;
        } else {
            return UNCLASSIFIED;
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

    public double getMalwareRatio() {
        return malwareRatio;
    }

    public void setMalwareRatio(double ratio) {
        this.malwareRatio = ratio;
    }

    public void updateMalwareRatio() {
        if (total != 0) {
            this.setMalwareRatio(positives / (double) total);
        } else {
            this.setMalwareRatio(0);
        }
    }
}
