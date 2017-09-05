package droidefense;
import java.io.File;

import droidefense.batch.base.IBatchTask;
import droidefense.batch.base.ICSVGenerator;
import droidefense.batch.base.IWekaGenerator;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskTest {


    @Test
    public void t00_task_creation() {

        IBatchTask task = new IBatchTask() {
            @Override
            public void beforeTask() {

            }

            @Override
            public void onTask() {

            }

            @Override
            public void afterTask() {

            }

            @Override
            public String getTaskName() {
                return null;
            }

            @Override
            public String getTaskIdName() {
                return null;
            }
        };
        Assert.assertNotNull(task);
    }

    @Test
    public void t01_csv_creation() {

        ICSVGenerator task = new ICSVGenerator() {
            @Override
            public String toCSV() {
                return null;
            }
        };
        Assert.assertNotNull(task);
    }

    @Test
    public void t02_weka_creation() {

        IWekaGenerator task = new IWekaGenerator() {
            @Override
            public String toWekaData() {
                return null;
            }
        };
        Assert.assertNotNull(task);
    }

}
