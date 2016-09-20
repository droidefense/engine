package com.zerjioang.apkr.v2.plugins.collection.dynmc;

import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.model.SharedPool;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrDynamicPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sergio on 31/5/16.
 */
public class ReflectionUsageDetectionPlugin extends AbstractApkrDynamicPlugin {

    private float founded;

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
        founded = (found / total) * 100;
        positiveMatch = founded >= 0.5;
    }

    @Override
    protected void postExecute() {
        if (positiveMatch) {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-red\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">Java Reflection usage detected (" + founded + "%)</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        } else {
            this.html = "<div class=\"info-box\">" +
                    "<span class=\"info-box-icon bg-green\"><i id=\"plugin-icon\"class=\"fa fa-flag-o\"></i></span>" +
                    "<div class=\"info-box-content\">" +
                    "<span class=\"info-box-text\" id=\"plugin-name\">" + getPluginName() + "</span>" +
                    "<span class=\"info-box-number\" id=\"plugin-result\">No Java Reflection usage detected</span>" +
                    "</div>" +
                    "<!-- /.info-box-content -->" +
                    "</div>";
        }
    }

    @Override
    protected String getPluginName() {
        return "Reflection usage detection plugin";
    }
}
