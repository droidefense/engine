package jadx.tests.integration.generics;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TestGenerics extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("mthWildcard(List<?> list)"));
        assertThat(code, containsString("mthExtends(List<? extends A> list)"));
        assertThat(code, containsString("mthSuper(List<? super A> list)"));
    }

    public static class TestCls {
        public static void mthWildcard(List<?> list) {
        }

        public static void mthExtends(List<? extends A> list) {
        }

        public static void mthSuper(List<? super A> list) {
        }

        class A {
        }
    }
}
