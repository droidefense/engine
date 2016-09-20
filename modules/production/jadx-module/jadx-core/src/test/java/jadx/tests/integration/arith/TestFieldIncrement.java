package jadx.tests.integration.arith;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TestFieldIncrement extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("instanceField++;"));
        assertThat(code, containsString("staticField--;"));
        assertThat(code, containsString("result += s + '_';"));
    }

    public static class TestCls {
        public static int staticField = 1;
        public static String result = "";
        public int instanceField = 1;

        public void method() {
            instanceField++;
        }

        public void method2() {
            staticField--;
        }

        public void method3(String s) {
            result += s + '_';
        }
    }
}
