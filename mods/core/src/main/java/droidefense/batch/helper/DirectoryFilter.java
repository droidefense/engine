package droidefense.batch.helper;

import droidefense.exception.ConfigFileNotFoundException;
import droidefense.sdk.helpers.DroidDefenseEnvironmentConfig;
import droidefense.sdk.helpers.InternalConstant;
import com.droidefense.log4j.Log;
import com.droidefense.log4j.LoggerType;
import droidefense.sdk.model.io.AbstractHashedFile;

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
            return f.getName().endsWith(InternalConstant.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<AbstractHashedFile> files) {
            for (AbstractHashedFile r : files) {
                if (r.getName().endsWith(InternalConstant.JAVA_EXTENSION)) {
                    int value = r.getAbsolutePath().lastIndexOf(File.separator);
                    String name = r.getAbsolutePath().substring(value + 1);
                    name = name.replace(InternalConstant.JAVA_EXTENSION, InternalConstant.NONE);
                    name = name.replace(File.separator, ".");
                    set.add(name);
                }
            }
            return set;
        }

        @Override
        public String getResultName() {
            try {
                return DroidDefenseEnvironmentConfig.getInstance().JAVA_SDK_CLASS_HASHSET_NAME;
            } catch (ConfigFileNotFoundException e) {
                Log.write(LoggerType.FATAL, "Could not retrieve JAVA_SDK_CLASS_HASHSET_NAME  from external config file", e.getLocalizedMessage());
                return null;
            }
        }
    },

    ANDROID_SDK_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(InternalConstant.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<AbstractHashedFile> files) {
            for (AbstractHashedFile r : files) {
                int value = r.getAbsolutePath().lastIndexOf(File.separator);
                String name = r.getAbsolutePath().substring(value + 1);
                name = name.replace(InternalConstant.JAVA_EXTENSION, InternalConstant.NONE);
                name = name.replace(File.separator, ".");
                set.add(name);
            }
            return set;
        }

        @Override
        public String getResultName() {
            try {
                return DroidDefenseEnvironmentConfig.getInstance().ANDROID_SDK_CLASS_HASHSET_NAME;
            } catch (ConfigFileNotFoundException e) {
                Log.write(LoggerType.FATAL, "Could not retrieve ANDROID_SDK_CLASS_HASHSET_NAME  from external config file", e.getLocalizedMessage());
                return null;
            }
        }
    },

    ANDROID_SUPPORT_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(InternalConstant.JAVA_EXTENSION) && f.getAbsolutePath().contains("/extras/android/support/v");
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<AbstractHashedFile> files) {
            for (AbstractHashedFile r : files) {
                int value = r.getAbsolutePath().lastIndexOf(File.separator);
                String name = r.getAbsolutePath().substring(value + 1);
                name = name.replace(InternalConstant.JAVA_EXTENSION, InternalConstant.NONE);
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
            try {
                return DroidDefenseEnvironmentConfig.getInstance().ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME;
            } catch (ConfigFileNotFoundException e) {
                Log.write(LoggerType.FATAL, "Could not retrieve ANDROID_SDK_SUPPORT_CLASS_HASHSET_NAME  from external config file", e.getLocalizedMessage());
                return null;
            }
        }
    },

    PATH_FILTER {
        @Override
        public boolean filterCondition(File f) {
            return f.getName().endsWith(InternalConstant.JAVA_EXTENSION);
        }

        @Override
        public HashSet<String> saveCondition(HashSet<String> set, ArrayList<AbstractHashedFile> files) {
            for (AbstractHashedFile r : files) {
                if (r.getName().endsWith(InternalConstant.JAVA_EXTENSION)) {
                    set.add(r.getAbsolutePath());
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

    public abstract HashSet<String> saveCondition(HashSet<String> set, ArrayList<AbstractHashedFile> files);

    public abstract String getResultName();
}
