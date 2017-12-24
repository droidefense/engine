package droidefense.emulator.featured;

import droidefense.emulator.featured.base.AbstractAndroidEmulator;
import droidefense.log4j.Log;
import droidefense.log4j.LoggerType;

import java.util.Arrays;
import java.util.HashMap;

public class AndroidLogEmulator extends AbstractAndroidEmulator {

    public static final String ANDROID_LOG_CLASS = "android/util/Log";
    private static final HashMap<String, ILog> actions = new HashMap<>();
    private static final String ERROR = "e";
    private static final String INFO = "i";
    private static final String DEBUG = "d";
    private static final String WARNING = "w";

    private String methodName;
    private Object[] args;

    public AndroidLogEmulator(String methodName, Object[] args) {

        this.methodName = methodName;
        this.args = args;

        if (actions.size() == 0) {
            actions.put(ERROR, args1 -> Log.write(LoggerType.ERROR, args1));
            actions.put(INFO, args12 -> Log.write(LoggerType.INFO, args12));
            actions.put(DEBUG, args13 -> Log.write(LoggerType.DEBUG, args13));
            actions.put(WARNING, args14 -> Log.write(LoggerType.WARN, args14));
        }
    }

    public void emulate() {
        increaseCounter();
        actions.get(methodName).logIt(args);
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "AndroidLogEmulator{" +
                "methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    interface ILog {
        void logIt(Object[] args);
    }
}
