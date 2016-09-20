package jadx.tests.integration.invoke;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TestConstructorInvoke extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConstructorInvoke.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("new ViewHolder(root, name);"));
    }

    public class TestCls {
        void test(String root, String name) {
            ViewHolder viewHolder = new ViewHolder(root, name);
        }

        private final class ViewHolder {
            private final String mRoot;
            private int mElements = 0;
            private String mName;

            private ViewHolder(String root, String name) {
                this.mRoot = root;
                this.mName = name;
            }
        }
    }
}
