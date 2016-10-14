package droidefense.analysis;

import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.DirScannerHandler;
import droidefense.handler.FileIOHandler;
import droidefense.handler.base.DirScannerFilter;
import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.sdk.model.base.HashedFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sergio on 17/2/16.
 */
public final class AndroidStaticPluginAnalysis extends AbstractAndroidAnalysis {

    public transient static final String PLUGIN_PACKAGE_NAME = "com.zerjioang.apkr.plugins.collection.statico.";

    public AndroidStaticPluginAnalysis() {
        timeStamp = new ExecutionTimer();
    }

    @Override
    public boolean analyze() {
        positiveMatch = false;
        //set current currentProject
        currentProject = DroidefenseProject.getProject(apkFile);
        Log.write(LoggerType.TRACE, "\n\n --- Running Android static plugin analysis ---\n\n");
        File plFolder = FileIOHandler.getStaticPluginsFolderFile();
        if (plFolder.exists()) {
            DirScannerHandler scanner = new DirScannerHandler(plFolder, false, new DirScannerFilter() {
                @Override
                public boolean addFile(File f) {
                    return f.getName().endsWith(InternalConstant.COMPILED_JAVA_EXTENSION);
                }
            });
            scanner.doTheJob();
            ArrayList<HashedFile> pluginsList = scanner.getFiles();
            for (HashedFile plugin : pluginsList) {
                //run each plugin in a different thread
                String pluginName = plugin.getThisFile().getName();
                if (pluginName.endsWith(InternalConstant.COMPILED_JAVA_EXTENSION)) {
                    Log.write(LoggerType.TRACE, plugin.getAbsolutePath());
                    Class aClass;
                    try {
                        ClassLoader classLoader = this.getClass().getClassLoader();
                        aClass = classLoader.loadClass(PLUGIN_PACKAGE_NAME + pluginName.replace(".class", ""));
                        Log.write(LoggerType.TRACE, "Executing " + aClass.getName());
                        AbstractStaticPlugin staticPlugin = (AbstractStaticPlugin) aClass.newInstance();

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
