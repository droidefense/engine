package jadx.samples;

import java.lang.reflect.Method;

public class TestInner2 extends AbstractTest {

    // value from java.lang.reflect.Modifier
    static final int SYNTHETIC = 0x00001000;
    private static String b;
    private static String d;
    private String a;
    private String c;

    private static void setD(String s) {
        d = s;
    }

    public static void main(String[] args) throws Exception {
        new TestInner2().testRun();
    }

    private void setC(String c) {
        this.c = c;
    }

    @Override
    public boolean testRun() throws Exception {
        assertTrue((new A()).a().equals("a"));
        assertTrue(a.equals("a"));

        assertTrue((new B()).b().equals("b"));
        assertTrue(b.equals("b"));

        assertTrue((new C()).c().equals("c"));
        assertTrue(c.equals("c"));

        assertTrue((new D()).d().equals("d"));
        assertTrue(d.equals("d"));

        Method[] mths = TestInner2.class.getDeclaredMethods();
        for (Method mth : mths) {
            if (mth.getName().startsWith("access$")) {
                int modifiers = mth.getModifiers();
                assertTrue((modifiers & SYNTHETIC) != 0, "Synthetic methods must be removed");
            }
        }
        return true;
    }

    public static class B {
        public B() {
            b = "b";
        }

        public String b() {
            return b;
        }
    }

    public static class D {
        public String d() {
            setD("d");
            return d;
        }
    }

    public class A {
        public A() {
            a = "a";
        }

        public String a() {
            return a;
        }
    }

    public class C {
        public String c() {
            setC("c");
            return c;
        }
    }
}
