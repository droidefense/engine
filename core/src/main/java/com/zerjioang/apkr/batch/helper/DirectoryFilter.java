package com.zerjioang.apkr.batch.helper;

import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by sergio on 4/9/16.
 */
public enum DirectoryFilter {

    JAVA_SDK_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(ApkrConstants.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<ApkrFile> files) {
            for (ApkrFile r : files) {
                if (r.getThisFile().getName().endsWith(ApkrConstants.JAVA_EXTENSION)) {
                    int value = r.getAbsolutePath().lastIndexOf(File.separator);
                    String name = r.getAbsolutePath().substring(value + 1);
                    name = name.replace(ApkrConstants.JAVA_EXTENSION, ApkrConstants.NONE);
                    name = name.replace(File.separator, ".");
                    set.add(name);
                }
            }
            return set;
        }

        @Override
        public String getResultName() {
            return ApkrConstants.JAVA_SDK_CLASS_HASHSET_NAME;
        }
    },

    ANDROID_SDK_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(ApkrConstants.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<ApkrFile> files) {
            for (ApkrFile r : files) {
                int value = r.getAbsolutePath().lastIndexOf(File.separator);
                String name = r.getAbsolutePath().substring(value + 1);
                name = name.replace(ApkrConstants.JAVA_EXTENSION, ApkrConstants.NONE);
                name = name.replace(File.separator, ".");
                set.add(name);
            }
            return set;
        }

        @Override
        public String getResultName() {
            return ApkrConstants.ANDROID_SDK_CLASS_HASHSET_NAME;
        }
    },

    ANDROID_SUPPORT_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(ApkrConstants.JAVA_EXTENSION) && f.getAbsolutePath().contains("/extras/android/support/v");
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<ApkrFile> files) {
            for (ApkrFile r : files) {
                int value = r.getAbsolutePath().lastIndexOf(File.separator);
                String name = r.getAbsolutePath().substring(value + 1);
                name = name.replace(ApkrConstants.JAVA_EXTENSION, ApkrConstants.NONE);
                name = name.replace(File.separator, ".");
                //clean str init
                int idx = name.indexOf("android.support");
                if (idx != -1)
                    name = name.substring(idx);
                set.add(name);
            }
            return set;
        }

        @Override
        public String getResultName() {
            return ApkrConstants.ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME;
        }
    },

    PATH_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(ApkrConstants.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<ApkrFile> files) {
            for (ApkrFile r : files) {
                if (r.getThisFile().getName().endsWith(ApkrConstants.JAVA_EXTENSION)) {
                    set.add(r.getThisFile().getAbsolutePath());
                }
            }
            return set;
        }

        @Override
        public String getResultName() {
            return "method-names.map";
        }
    };

    public abstract boolean filterCondition(File f);

    public abstract HashSet<String> saveCondition(HashSet<String> set, ArrayList<ApkrFile> files);

    public abstract String getResultName();
}
