package vt;

import org.jsoup.nodes.Document;

import java.io.Serializable;

/**
 * Created by B328316 on 09/05/2016.
 */
public abstract class VirusTotalPair implements IPair, Serializable{

    private final String key, value;

    public VirusTotalPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VirusTotalPair{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public abstract void process(Document doc);
}
