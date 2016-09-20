package jadx.tests.integration.trycatch;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestTryCatch4 extends IntegrationTest {

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("try {"));
        assertThat(code, containsString("output = new FileOutputStream(new File(\"f\"));"));
        assertThat(code, containsString("return new Object();"));
        assertThat(code, containsString("} catch (FileNotFoundException e) {"));
        assertThat(code, containsString("System.out.println(\"Exception\");"));
        assertThat(code, containsString("return null;"));
        assertThat(code, not(containsString("output = output;")));
    }

    public static class TestCls {
        private Object test(Object obj) {
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(new File("f"));
                return new Object();
            } catch (FileNotFoundException e) {
                System.out.println("Exception");
                return null;
            }
        }
    }
}
