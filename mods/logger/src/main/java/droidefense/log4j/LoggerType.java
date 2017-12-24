package droidefense.log4j;

import org.apache.log4j.Logger;

import java.io.Serializable;

public enum LoggerType implements Serializable {

    TRACE() {
        @Override
        public void log(Object o) {
            if (Log.isVerboseEnabled()) {
                Logger.getLogger("TRACE").trace(o);
            }
        }
    }, DEBUG() {
        @Override
        public void log(Object o) {
            if (Log.isVerboseEnabled()) {
                Logger.getLogger("DEBUG").debug(o);
            }
        }
    }, INFO() {
        @Override
        public void log(Object o) {
            if (Log.isVerboseEnabled()) {
                Logger.getLogger("INFO").info(o);
            }
        }
    }, WARN() {
        @Override
        public void log(Object o) {
            if (Log.isVerboseEnabled()) {
                Logger.getLogger("WARN").warn(o);
            }
        }
    }, ERROR() {
        @Override
        public void log(Object o) {
            Logger.getLogger("ERROR").error(o);
        }
    }, FATAL() {
        @Override
        public void log(Object o) {
            Logger.getLogger("FATAL").fatal(o);
        }
    };

    public abstract void log(Object o);
}
