package jadx.tests.integration.inner;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.junit.Assert.assertThat;

public class TestAnonymousClass12 extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsOne("outer = new BasicAbstract() {"));
        assertThat(code, containsOne("inner = new BasicAbstract() {"));
        assertThat(code, containsOne("inner = null;"));
    }

    public static class TestCls {

        private BasicAbstract outer;
        private BasicAbstract inner;

        public void test() {
            outer = new BasicAbstract() {
                @Override
                public void doSomething() {
                    inner = new BasicAbstract() {
                        @Override
                        public void doSomething() {
                            inner = null;
                        }
                    };
                }
            };
        }

        public abstract static class BasicAbstract {
            public abstract void doSomething();
        }
    }
}
