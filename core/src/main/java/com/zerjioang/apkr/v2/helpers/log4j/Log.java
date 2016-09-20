package com.zerjioang.apkr.v2.helpers.log4j;

import java.io.Serializable;
import java.util.HashMap;

public class Log extends HashMap<String, LoggerType> implements Serializable {

    private static Log instance = new Log();

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
        LoggerType selectedLogger = instance.get(type.name());
        if (selectedLogger != null) {
            for (Object o : oarray)
                selectedLogger.log(o);
        }
    }
}

