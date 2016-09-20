package com.zerjioang.apkr.v2.helpers.enums;

import java.io.Serializable;

/**
 * Created by sergio on 6/3/16.
 */
public enum ProcessStatus implements Serializable {

    WAITING {
        @Override
        public String getStatusName() {
            return "WAITING";
        }

        @Override
        public int getStatusIndex() {
            return 0;
        }
    },
    STARTED {
        @Override
        public String getStatusName() {
            return "STARTED";
        }

        @Override
        public int getStatusIndex() {
            return 1;
        }
    },
    PAUSED {
        @Override
        public String getStatusName() {
            return "PAUSED";
        }

        @Override
        public int getStatusIndex() {
            return 2;
        }
    },
    ON_ERROR {
        @Override
        public String getStatusName() {
            return "ON_ERROR";
        }

        @Override
        public int getStatusIndex() {
            return 3;
        }
    },
    EXECUTING {
        @Override
        public String getStatusName() {
            return "EXECUTING";
        }

        @Override
        public int getStatusIndex() {
            return 4;
        }
    },
    STOPPED {
        @Override
        public String getStatusName() {
            return "STOPPED";
        }

        @Override
        public int getStatusIndex() {
            return 5;
        }
    },
    FINISHED {
        @Override
        public String getStatusName() {
            return "FINISHED";
        }

        @Override
        public int getStatusIndex() {
            return 6;
        }
    };

    public abstract String getStatusName();

    public abstract int getStatusIndex();

}