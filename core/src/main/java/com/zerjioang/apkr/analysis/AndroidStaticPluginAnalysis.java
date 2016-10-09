package com.zerjioang.apkr.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.handler.DirScannerHandler;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.handler.base.DirScannerFilter;
import com.zerjioang.apkr.sdk.AbstractApkrStaticPlugin;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;
import com.zerjioang.apkr.sdk.model.base.AtomTimeStamp;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 17/2/16.
 */
public final class AndroidStaticPluginAnalysis extends AbstractAndroidAnalysis {

    public transient static final String PLUGIN_PACKAGE_NAME = "com.zerjioang.apkr.plugins.collection.statico.";

    public AndroidStaticPluginAnalysis() {
        timeStamp = new AtomTimeStamp();
    }

    @Override
    public boolean analyze() {
        positiveMatch = false;
        //set current currentProject
        currentProject = ApkrProject.getProject(apkFile);
        Log.write(LoggerType.TRACE, "\n\n --- Running Android static plugin analysis ---\n\n");
        File plFolder = FileIOHandler.getStaticPluginsFolderFile();
        if (plFolder.exists()) {
            DirScannerHandler scanner = new DirScannerHandler(plFolder, false, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return f.getName().endsWith(ApkrConstants.COMPILED_JAVA_EXTENSION);
                }
            });
            scanner.doTheJob();
            ArrayList<ApkrFile> pluginsList = scanner.getFiles();
            for (ApkrFile plugin : pluginsList) {
                //run each plugin in a different thread
                String pluginName = plugin.getThisFile().getName();
                if (pluginName.endsWith(ApkrConstants.COMPILED_JAVA_EXTENSION)) {
                    Log.write(LoggerType.TRACE, plugin.getAbsolutePath());
                    Class aClass;
                    try {
                        ClassLoader classLoader = this.getClass().getClassLoader();
                        aClass = classLoader.loadClass(PLUGIN_PACKAGE_NAME + pluginName.replace(".class", ""));
                        Log.write(LoggerType.TRACE, "Executing " + aClass.getName());
                        AbstractApkrStaticPlugin staticPlugin = (AbstractApkrStaticPlugin) aClass.newInstance();

                        staticPlugin.setApk(apkFile);
                        staticPlugin.setCurrentProject(currentProject);
                        staticPlugin.analyze();

                        //add result to currentProject
                        currentProject.addStaticPlugin(staticPlugin);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        addError(e);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        addError(e);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        addError(e);
                    } catch (Exception e) {
                        e.printStackTrace();
                        addError(e);
                    }
                } else {
                    //todo load jar content using reflection
                    Log.write(LoggerType.TRACE, plugin.getAbsolutePath());
                }
            }
        }
        Log.write(LoggerType.TRACE, "\n--- RUNNING PLUGINS (DONE)---\n");
        //stop timer
        stop();
        positiveMatch = true;
        return positiveMatch;
    }

    @Override
    public String getName() {
        return "Android static plugin analysis";
    }

}
