package droidefense.batch.base;

public interface IBatchTask {

    void beforeTask();

    void onTask();

    void afterTask();

    String getTaskName();

    String getTaskIdName();
}
