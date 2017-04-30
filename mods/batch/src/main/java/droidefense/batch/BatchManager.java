package apkr.external.module.batch;


import apkr.external.module.batch.base.IBatchTask;
import apkr.external.module.batch.exception.EmptyTaskQueueException;
import apkr.external.module.batch.exception.InvalidTaskException;

import java.io.Serializable;
import java.util.ArrayList;

public class BatchManager implements Serializable {

    private final ArrayList<IBatchTask> taskList;

    public BatchManager() {
        this.taskList = new ArrayList<>();
    }

    private void start() throws EmptyTaskQueueException {
        if (taskList.isEmpty())
            throw new EmptyTaskQueueException("There are no jobs queued.");

        //Log.write(LoggerType.TRACE, "Batch jobs queued: " + taskList.size());
        //start task in order of appearence.
        for (IBatchTask task : taskList) {
            long start = System.currentTimeMillis();
            task.beforeTask();
            task.onTask();
            task.afterTask();
            long end = System.currentTimeMillis();
            //Log.write(LoggerType.TRACE, "Task done in " + (end - start) / 1000 + " s");
        }
        //Log.write(LoggerType.TRACE, "Batch task finished!");
    }

    private void addTask(IBatchTask task) throws InvalidTaskException {
        if (task == null) {
            throw new InvalidTaskException("Task to be added is not constructed! Null object task found");
        }
        this.taskList.add(task);
    }


}
