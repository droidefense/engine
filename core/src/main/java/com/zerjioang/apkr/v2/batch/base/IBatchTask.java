package com.zerjioang.apkr.v2.batch.base;

/**
 * Created by .local on 15/04/2016.
 */
public interface IBatchTask {

    void beforeTask();

    void onTask();

    void afterTask();

    String getTaskName();

    String getTaskIdName();
}
