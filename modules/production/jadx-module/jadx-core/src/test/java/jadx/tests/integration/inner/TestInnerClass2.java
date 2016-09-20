package jadx.tests.integration.inner;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestInnerClass2 extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("new Timer().schedule(new TerminateTask(), 1000);"));
        assertThat(code, not(containsString("synthetic")));
        assertThat(code, not(containsString("this")));
        assertThat(code, not(containsString("null")));
        assertThat(code, not(containsString("AnonymousClass")));
    }

    public static class TestCls {
        public void test() {
            new Timer().schedule(new TerminateTask(), 1000);
        }

        private static class TerminateTask extends TimerTask {
            @Override
            public void run() {
                System.err.println("Test timed out");
            }
        }
    }
}
