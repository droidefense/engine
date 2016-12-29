package com.droidefense.nodes;

import com.droidefense.base.AbstractAtomNode;

import java.util.ArrayList;

/**
 * Created by sergio on 10/4/16.
 */
public class EntryPointNode extends AbstractAtomNode {


    private transient static ArrayList<EntryPointNode> entryList = new ArrayList<>();

    private static EntryPointNode singleEntry = new EntryPointNode();

    public static EntryPointNode builder() {
        /*EntryPointNode entry = new EntryPointNode();
        //entryList.add(entry);
        return entry;*/
        return singleEntry;
    }

    public static ArrayList<EntryPointNode> getList() {
        entryList.add(singleEntry);
        return (ArrayList<EntryPointNode>) entryList.clone();
    }

    @Override
    public String getConnectionLabel() {
        return "START";
    }

    @Override
    public String getNodeLabel() {
        return "START";
    }

    @Override
    public String getConnectionStyle() {
        return "color=red, fontname=Courier, fontsize=15";
    }

    @Override
    public String getNodeStyle() {
        return "color=red, shape=doublecircle, fontname=Courier, fontsize=15";
    }

    @Override
    public boolean isDrawable() {
        return true;
    }

}
