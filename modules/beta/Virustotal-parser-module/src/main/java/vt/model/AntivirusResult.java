package vt.model;

import java.io.Serializable;

/**
 * Created by B328316 on 09/05/2016.
 */
public class AntivirusResult implements Serializable{

    private final String name, result, lastUpdated;

    public AntivirusResult(String name, String result, String lastUpdated) {
        this.name = name;
        this.result = result;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
