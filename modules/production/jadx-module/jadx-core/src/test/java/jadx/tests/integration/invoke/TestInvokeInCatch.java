package jadx.tests.integration.invoke;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.io.IOException;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestInvokeInCatch extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsOne("try {"));
        assertThat(code, containsOne("exc();"));
        assertThat(code, not(containsString("return;")));
        assertThat(code, containsOne("} catch (IOException e) {"));
        assertThat(code, containsOne("if (b == 1) {"));
        assertThat(code, containsOne("log(TAG, \"Error: {}\", e.getMessage());"));
    }

    public static class TestCls {
        private static final String TAG = "TAG";

        private static void log(String tag, String str, String... args) {
        }

        private void test(int[] a, int b) {
            try {
                exc();
            } catch (IOException e) {
                if (b == 1) {
                    log(TAG, "Error: {}", e.getMessage());
                }
            }
        }

        private void exc() throws IOException {
            throw new IOException();
        }
    }
}
