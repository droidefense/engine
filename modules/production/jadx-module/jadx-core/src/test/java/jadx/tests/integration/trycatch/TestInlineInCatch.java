package jadx.tests.integration.trycatch;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.io.File;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.junit.Assert.assertThat;

public class TestInlineInCatch extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsOne("File output = null;"));
        assertThat(code, containsOne("output = File.createTempFile(\"f\", \"a\", "));
        assertThat(code, containsOne("return 0;"));
        assertThat(code, containsOne("} catch (Exception e) {"));
        assertThat(code, containsOne("if (output != null) {"));
        assertThat(code, containsOne("output.delete();"));
        assertThat(code, containsOne("return 2;"));
    }

    public static class TestCls {
        private File dir;

        public int test() {
            File output = null;
            try {
                output = File.createTempFile("f", "a", dir);
                return 0;
            } catch (Exception e) {
                if (output != null) {
                    output.delete();
                }
                return 2;
            }
        }
    }
}
