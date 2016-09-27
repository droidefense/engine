package com.zerjioang.apkr.v1.core.rulengine;

import java.io.Serializable;

/**
 * Created by sergio on 6/6/16.
 */
public class Rule implements Serializable {

    public static final String TAG = "#";
    public static final String NONE = "";

    /**
     * rule params
     */
    public static final String DESC_ID = TAG + "description: ";
    public static final String AUTHOR_ID = TAG + "author: ";
    public static final String DATE_ID = TAG + "date: ";
    public static final String FAMILY_ID = TAG + "family: ";
    public static final String VARIANT_ID = TAG + "variant: ";
    public static final String RULE_ID = TAG + "rule: ";

    private static final String VAR_SEPARATOR = ";";
    private static final String RULE_SEPARATOR = " > ";
    private final transient String ruleData;
    private final String name;
    private transient final String[] nodes;
    private transient final String[] lines;
    private String desc, author, date, family, variant;
    private transient String rule;

    public Rule(String ruleData, String name) {
        this.ruleData = ruleData;
        this.name = name.replace("_", " ").replace(".rule", "");
        this.lines = this.ruleData.split(VAR_SEPARATOR);
        this.nodes = parseLines(lines);
    }

    private String[] parseLines(String[] lines) {
        for (String str : lines) {
            str = cleanString(str);
            if (str.startsWith(DESC_ID)) {
                desc = str.replace(DESC_ID, NONE);
            } else if (str.startsWith(AUTHOR_ID)) {
                author = str.replace(AUTHOR_ID, NONE);
            } else if (str.startsWith(DATE_ID)) {
                date = str.replace(DATE_ID, NONE);
            } else if (str.startsWith(FAMILY_ID)) {
                family = str.replace(FAMILY_ID, NONE);
            } else if (str.startsWith(VARIANT_ID)) {
                variant = str.replace(VARIANT_ID, NONE);
            } else if (str.startsWith(RULE_ID)) {
                rule = str.replace(RULE_ID, NONE);
            }
        }
        return rule.split(RULE_SEPARATOR);
    }

    private String cleanString(String str) {
        if (str != null) {
            str = str.replace("\\s+", " ");
            str = str.replace("\n|\r|\t+", NONE);
            return str.trim();
        }
        return NONE;
    }

    public int getNodeCount() {
        return nodes.length;
    }

    public String getName() {
        return name;
    }

    public String getNode(int i) {
        if (i >= 0 && i < nodes.length)
            return nodes[i];
        return null;
    }

    public String[] intelligentSplit(int idx) {
        String source = getNode(idx);
        if (source != null) {
            return source.split("\\.");
        }
        return null;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", family='" + family + '\'' +
                ", variant='" + variant + '\'' +
                ", rule='" + rule + '\'' +
                '}';
    }
}
