package com.zerjioang.apkr.v1.core.analysis.task;

import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.common.datamodel.base.ApkrProject;
import com.zerjioang.apkr.v1.common.datamodel.base.AtomTimeStamp;
import com.zerjioang.apkr.v1.common.datamodel.base.ResourceFile;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerFilter;
import com.zerjioang.apkr.v1.core.analysis.sttc.handlers.DirScannerHandler;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrDynamicPlugin;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 17/2/16.
 */
public class AndroidDynamicPluginAnalysis extends AbstractAndroidAnalysis {

    public transient static final String PLUGIN_PACKAGE_NAME = "com.zerjioang.apkr.v2.plugins.collection.dynmc.";

    public AndroidDynamicPluginAnalysis() {
        timeStamp = new AtomTimeStamp();
    }

    @Override
    public boolean analyze() {
        positiveMatch = false;
        //set current currentProject
        currentProject = ApkrProject.getProject(apkFile);
        Log.write(LoggerType.TRACE, "\n\n --- Running apkr dynamic plugin analysis ---\n\n");
        File plFolder = FileIOHandler.getDynamicPluginsFolderFile();
        if (plFolder.exists()) {
            DirScannerHandler scanner = new DirScannerHandler(plFolder, false, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return f.getName().endsWith(ApkrConstants.COMPILED_JAVA_EXTENSION);
                }
            });
            scanner.doTheJob();
            ArrayList<ResourceFile> pluginsList = scanner.getFiles();
            for (ResourceFile plugin : pluginsList) {
                //run each plugin in a different thread
                String pluginName = plugin.getThisFile().getName();
                if (pluginName.endsWith(ApkrConstants.COMPILED_JAVA_EXTENSION)) {
                    Log.write(LoggerType.TRACE, plugin.getAbsolutePath());
                    Class aClass = null;
                    try {
                        ClassLoader classLoader = this.getClass().getClassLoader();
                        aClass = classLoader.loadClass(PLUGIN_PACKAGE_NAME + pluginName.replace(".class", ""));
                        Log.write(LoggerType.TRACE, "Executing " + aClass.getName());
                        AbstractApkrDynamicPlugin pluginDynamic = (AbstractApkrDynamicPlugin) aClass.newInstance();

                        pluginDynamic.setApk(apkFile);
                        pluginDynamic.setCurrentProject(currentProject);
                        //TODO fix this name
                        //name = pluginDynamic.getPluginName();
                        pluginDynamic.analyze();
                        //result = pluginDynamic.getResult();

                        //add result to currentProject
                        currentProject.addDynamicPlugin(pluginDynamic);
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
        return "Android dynamic plugin analysis";
    }

}
