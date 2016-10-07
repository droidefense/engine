package com.zerjioang.apkr.sdk.model.base;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by sergio on 16/2/16.
 */
public class AtomTimeStamp implements Serializable {

    private transient boolean started;
    private long start, end, duration;

    private String time;

    public AtomTimeStamp() {
        this.started = false;
        this.start();
        this.started = true;
        this.time = "00:00:00";
        this.duration = 0;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public void start() {
        if (!this.started) {
            this.start = System.currentTimeMillis();
            this.started = true;
        }
    }

    public void stop() {
        this.end = System.currentTimeMillis();
        getDuration();
        getFormattedDuration();
    }

    public long getDuration() {
        duration = getEnd() - getStart();
        return duration;
    }

    public String getFormattedDuration() {
        long millis = getDuration();
        time = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return time;
    }

    @Override
    public String toString() {
        return "AtomTimeStamp{" +
                "start=" + start +
                ", end=" + end +
                ", duration=" + getDuration() +
                ", time=" + getFormattedDuration() +
                '}';
    }
}
