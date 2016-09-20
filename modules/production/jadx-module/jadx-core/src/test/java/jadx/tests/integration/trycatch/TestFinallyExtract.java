package jadx.tests.integration.trycatch;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.io.IOException;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.junit.Assert.assertThat;

public class TestFinallyExtract extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsOne("success = true;"));
        assertThat(code, containsOne("return value;"));
        assertThat(code, containsOne("try {"));
        assertThat(code, containsOne("} finally {"));
        assertThat(code, containsOne("if (!success) {"));
    }

    public static class TestCls {

        public String test() throws IOException {
            boolean success = false;
            try {
                String value = test();
                success = true;
                return value;
            } finally {
                if (!success) {
                    test();
                }
            }
        }
    }
}
