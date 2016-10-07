package com.zerjioang.apkr.batch;

import apkr.external.module.batch.base.IBatchTask;
import apkr.external.module.batch.exception.EmptyTaskQueueException;
import apkr.external.module.batch.exception.InvalidTaskException;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import com.zerjioang.apkr.batch.task.WekaCertTask;
import com.zerjioang.apkr.batch.task.WekaFeatureExtractorTask;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by .local on 15/04/2016.
 */
public class BatchManager implements Serializable {

    private final ArrayList<IBatchTask> taskList;

    public BatchManager() {
        this.taskList = new ArrayList<>();
    }

    public static void main(String[] args) throws InvalidTaskException, EmptyTaskQueueException {

        BatchManager manager = new BatchManager();
        IBatchTask task1 = new WekaFeatureExtractorTask(
                new File("/Volumes/Warehouse/MalwareSamples/malware/VirusShare_Android_20130506/small/"),
                new File("/Volumes/Warehouse/out/out-bad"),
                "features_bad",
                "MALWARE"
        );
        //manager.addTask(task1);

        IBatchTask task2 = new WekaFeatureExtractorTask(
                new File("/Volumes/Warehouse/MalwareSamples/goodware"),
                new File("/Volumes/Warehouse/out/out-good"),
                "features_good",
                "GOODWARE"

        );
        //manager.addTask(task2);

        IBatchTask task3 = new WekaFeatureExtractorTask(
                new File("/Volumes/Warehouse/MalwareSamples/malware-test"),
                new File("/Volumes/Warehouse/out/out-test-bad"),
                "features_test",
                "MALWARE"

        );
        //manager.addTask(task3);

        IBatchTask task4 = new WekaFeatureExtractorTask(
                new File("/Volumes/Warehouse/MalwareSamples/goodware-test"),
                new File("/Volumes/Warehouse/out/out-test-good"),
                "features_test",
                "GOODWARE"

        );
        //manager.addTask(task4);

        IBatchTask certTask = new WekaCertTask(
                new File("/Volumes/Warehouse/MalwareSamples/bad"),
                new File("/Volumes/Warehouse/out/cert3"),
                "certificate_malware",
                "WEIRD"
        );
        //manager.addTask(certTask);

        IBatchTask certTask2 = new WekaCertTask(
                new File("/Volumes/Warehouse/MalwareSamples/good"),
                new File("/Volumes/Warehouse/out/cert2"),
                "certificate_goodware",
                "NORMAL"
        );
        //manager.addTask(certTask2);
        manager.start();

    }

    private void start() throws EmptyTaskQueueException {
        if (taskList.isEmpty())
            throw new EmptyTaskQueueException("There are no jobs queued.");

        Log.write(LoggerType.TRACE, "Batch jobs queued: " + taskList.size());
        //start task in order of appearence.
        for (IBatchTask task : taskList) {
            long start = System.currentTimeMillis();
            task.beforeTask();
            task.onTask();
            task.afterTask();
            long end = System.currentTimeMillis();
            Log.write(LoggerType.TRACE, "Task done in " + (end - start) / 1000 + " s");
        }
        Log.write(LoggerType.TRACE, "Batch task finished!");
    }

    private void addTask(IBatchTask task) throws InvalidTaskException {
        if (task == null) {
            throw new InvalidTaskException("Task to be added is not constructed! Null object task found");
        }
        this.taskList.add(task);
    }


}
