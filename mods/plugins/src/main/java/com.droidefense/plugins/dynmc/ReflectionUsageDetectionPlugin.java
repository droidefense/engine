package com.droidefense.plugins.dynmc;

import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.sdk.AbstractDynamicPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sergio on 31/5/16.
 */
public class ReflectionUsageDetectionPlugin extends AbstractDynamicPlugin {

    private float found;

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onExecute() {
        String[] keys = {
                "java/lang/ClassLoader",
                "java/lang/Class",
                "java/lang/reflect/Field",
                "java/lang/reflect/Method",
                "java/lang/reflect/Constructor",
        };
        int found = 0;
        float total = keys.length;
        SharedPool pool = SharedPool.getInstance();

        log("Looking for reflection usage descriptors...", 1);
        // physical classes inside .dex file
        // Set<String> fclasses = new HashSet<String>(Arrays.asList(pool.getFieldClasses()));
        //Set<String> ftypes = new HashSet<String>(Arrays.asList(pool.getFieldTypes()));

        //classes used in whole .dex file
        Set<String> mclasses = new HashSet<String>(Arrays.asList(pool.getMethodClasses()));

        //names of the methods
        //Set<String> mnames = new HashSet<String>(Arrays.asList(pool.getMethodNames()));

        //methods arguments and return type
        //Set<String> mtypes = new HashSet<String>(Arrays.asList(pool.getMethodTypes()));

        for (String key : keys) {
            log("Searching descriptor: " + key, 2);
            found += mclasses.contains(key) ? 1 : 0;
        }
        log("Found: " + found, 1);
        this.found = (found / total) * 100;
        positiveMatch = this.found >= 0.5;
    }

    @Override
    protected void postExecute() {
    }

    @Override
    public String getPluginName() {
        return "Reflection usage detection plugin";
    }
}
