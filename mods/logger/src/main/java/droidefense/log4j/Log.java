package droidefense.log4j;

import java.io.Serializable;
import java.util.HashMap;

public class Log extends HashMap<String, LoggerType> implements Serializable {

    private static Log instance = new Log();
    private static boolean VERBOSE = true;

    private Log() {
        //create and fill the map
        put(LoggerType.TRACE.name(), LoggerType.TRACE);
        put(LoggerType.DEBUG.name(), LoggerType.DEBUG);
        put(LoggerType.INFO.name(), LoggerType.INFO);
        put(LoggerType.WARN.name(), LoggerType.WARN);
        put(LoggerType.ERROR.name(), LoggerType.ERROR);
        put(LoggerType.FATAL.name(), LoggerType.FATAL);
    }

    public static void write(LoggerType type, Object... oarray) {
        boolean alwaysPrintsErrors = type == LoggerType.ERROR || type == LoggerType.FATAL;
        if (isVerboseEnabled() || alwaysPrintsErrors) {
            //verbose enabled, print it
            LoggerType selectedLogger = instance.get(type.name());
            if (selectedLogger != null) {
                for (Object o : oarray)
                    selectedLogger.log(o);
            }
        }
    }

    public static void beVerbose(boolean verbose) {
        VERBOSE = verbose;
    }

    public static boolean isVerboseEnabled() {
        return VERBOSE;
    }

    public static void droidefenseGenericError(Exception e) {
        Log.write(LoggerType.ERROR, "Droidefense error, please report", e.getLocalizedMessage());
    }
}

