package com.zerjioang.apkr.temp;

import apkr.external.modules.controlflow.model.base.AbstractAtomNode;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by sergio on 3/2/16.
 */
public class ApkrIntelligence implements Serializable {

    private static final String DEFAULT_NAIVE_BAYES_RESULT = "Unknown";
    /**
     * ApkrIntelligence singleton variable.
     */
    private static ApkrIntelligence instance = new ApkrIntelligence();

    /**
     * Bayesian classifier harcoded apimodel instance for singleton access
     */

    /* Extracted from official source folders */
    private HashSet<String> javaClassList;
    private HashSet<String> androidClassList;
    private HashSet<String> androidSupportClassList;

    private ApkrIntelligence() {
        //load a list of native jdk8 classes
        javaClassList = new HashSet<String>();
        try {
            ObjectInputStream jdk8ObjectFile = FileIOHandler.getResourceObjectStream(ApkrConstants.INTERNAL_DATA_FOLDER + File.separator + ApkrConstants.JAVA_SDK_CLASS_HASHSET_NAME);
            javaClassList = (HashSet<String>) FileIOHandler.readAsRAW(jdk8ObjectFile);
            Log.write(LoggerType.TRACE, "Java whitelisted dataset length: " + javaClassList.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //load a list of native android sdk classes
        androidClassList = new HashSet<String>();
        try {
            ObjectInputStream sdkFile = FileIOHandler.getResourceObjectStream(ApkrConstants.INTERNAL_DATA_FOLDER + File.separator + ApkrConstants.ANDROID_SDK_CLASS_HASHSET_NAME);
            androidClassList = (HashSet<String>) FileIOHandler.readAsRAW(sdkFile);
            Log.write(LoggerType.TRACE, "Android whitelisted dataset length: " + androidClassList.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //load a list of native android support sdk classes
        androidSupportClassList = new HashSet<String>();
        try {
            ObjectInputStream supportFile = FileIOHandler.getResourceObjectStream(ApkrConstants.INTERNAL_DATA_FOLDER + File.separator + ApkrConstants.ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME);
            androidSupportClassList = (HashSet<String>) FileIOHandler.readAsRAW(supportFile);
            Log.write(LoggerType.TRACE, "Android support whitelisted dataset length: " + androidSupportClassList.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ApkrIntelligence getInstance() {
        return instance;
    }

    public boolean isAndroidNative(String name) {
        if (androidClassList == null)
            return false;
        name = cleanClassName(name);
        return androidClassList.contains(name);
    }

    public boolean isJavaNative(String name) {
        if (javaClassList == null)
            return false;
        name = cleanClassName(name);
        return javaClassList.contains(name);
    }

    public boolean isAndroidSupportClass(String name) {
        if (javaClassList == null)
            return false;
        name = cleanClassName(name);
        return androidSupportClassList.contains(name);
    }

    public boolean isDeveloperClass(String className) {
        return !isJavaNative(className) &&
                !isAndroidNative(className) &&
                !isAndroidSupportClass(className) &&
                !isAndroidUIRelatedClass(className) &&
                !isAndroidv4v7Class(className);
    }

    public boolean isAndroidv4v7Class(String className) {
        className = cleanClassName(className);
        return className.contains("android.support.v4")
                || className.startsWith("android.support.v7")
                || className.startsWith("android.support.v13")
                || className.startsWith("android.support.v14")
                || className.startsWith("android.support.v17")
                || className.startsWith("android.support.graphics")
                || className.startsWith("android.support.design")
                || className.startsWith("android.support.customtabs")
                || className.startsWith("android.support.annotation");
    }

    public boolean isAndroidRclass(String className) {
        return className.endsWith("R$attr")
                || className.endsWith("R")
                || className.endsWith("R$drawable")
                || className.endsWith("R$dimen")
                || className.endsWith("R$integer")
                || className.endsWith("R$mipmap")
                || className.endsWith("R$styleable")
                || className.endsWith("R$id")
                || className.endsWith("R$style")
                || className.endsWith("R$bool")
                || className.endsWith("R$color")
                || className.endsWith("R$anim")
                || className.endsWith("R$string")
                || className.endsWith("R$layout");
    }

    public boolean isAndroidUIRelatedClass(String className) {
        className = cleanClassName(className);
        return className.equals("android.widget.TextView")
                || className.equals("android.app.Activity");
    }

    private String cleanClassName(String name) {
        name = name.replace("/", ".");
        int idx = name.indexOf("$");
        if (idx != -1)
            name = name.substring(0, idx);
        return name;
    }

    public boolean isBuildConfig(String className) {
        className = cleanClassName(className);
        //todo falta comprobar si un developer puede crear una clase con el nombre BuilConfig.java
        return className.endsWith(".BuildConfig");
    }

    public String getPredictedStringClass(String content) {
        String category = null;
        if (category == null)
            return DEFAULT_NAIVE_BAYES_RESULT;
        return category;
    }

    //http://www.graphviz.org/doc/info/colors.html
    public String classifyNodeColor(String fullClassName, String methodName, AbstractAtomNode node) {
        boolean innerClass = fullClassName.contains("$");
        if (isAndroidNative(fullClassName)) {
            //android native class
            if (innerClass) {
                return "gray86";
            } else {
                return "gray58";
            }
        } else {
            String type = node.getType();
            if (type.equals("Developer | InnerClass")) {
                return "gold";
            } else if (
                    type.equals("Developer | Activity")
                            || type.equals("Developer | AppCompatActivity")
                    ) {
                return "chartreuse1";
            } else if (type.equals("Developer | Service")) {
                return "orangered";
            } else if (type.equals("Developer | BroadcastReceiver")) {
                return "firebrick1";
            } else {
                return "deepskyblue";
            }
        }
    }

    //TODO enable when om added
    /*
    public String classifyNode(IAtomMethod method, String fullClassName, String methodName) {

        if (isAndroidNative(fullClassName)) {
            return "Android | SDK";
        }
        boolean innerClass = fullClassName.contains("$");

        if (innerClass) {
            return "Developer | InnerClass";
        } else {

            IAtomClass owner = method.getOwnerClass();
            String sc;
            sc = owner.getSuperClass();

            if (owner.isFake()) {
                //set the type of holding class
                IAtomClass cls = DexClassReader.getInstance().load(owner.getName());
                return nodeTypeResolver(cls.getSuperClass());
            } else {
                return nodeTypeResolver(sc);
            }
        }
    }

    public String getSimpleNodeType(IAtomMethod method) {
        IAtomClass owner = method.getOwnerClass();
        String sc;
        sc = owner.getSuperClass();

        if (owner.isFake()) {
            //set the type of holding class
            IAtomClass cls = DexClassReader.getInstance().load(owner.getName());
            return Util.getClassNameForFullPath(cls.getSuperClass());
        } else {
            return Util.getClassNameForFullPath(sc);
        }
    }
    */
}
