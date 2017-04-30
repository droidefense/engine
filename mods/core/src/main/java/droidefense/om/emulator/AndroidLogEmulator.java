package droidefense.om.emulator;

import droidefense.helpers.log4j.Log;
import droidefense.helpers.log4j.LoggerType;
import droidefense.om.emulator.base.AbstractAndroidEmulator;

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
            actions.put(ERROR, new ILog() {

                @Override
                public void logIt(Object[] args) {
                    Log.write(LoggerType.ERROR, args);
                }
            });
            actions.put(INFO, new ILog() {

                @Override
                public void logIt(Object[] args) {
                    Log.write(LoggerType.INFO, args);
                }
            });
            actions.put(DEBUG, new ILog() {

                @Override
                public void logIt(Object[] args) {
                    Log.write(LoggerType.DEBUG, args);
                }
            });
            actions.put(WARNING, new ILog() {

                @Override
                public void logIt(Object[] args) {
                    Log.write(LoggerType.WARN, args);
                }
            });
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
