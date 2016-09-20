package jadx.samples;

public class TestGenerics extends AbstractTest {

    public static Box<Integer> integerBox = new Box<Integer>();
    public List<String> strings;
    public Class<?>[] classes;
    Pair<String, Integer> p1 = new OrderedPair<String, Integer>("8", 8);
    OrderedPair<String, Box<Integer>> p = new OrderedPair<String, Box<Integer>>("primes", new Box<Integer>());

    public static boolean use() {
        Pair<Integer, String> p1 = new OrderedPair<Integer, String>(1, "str1");
        Pair<Integer, String> p2 = new OrderedPair<Integer, String>(2, "str2");
        boolean same = Util.<Integer, String>compare(p1, p2);
        return same;
    }

    public static <T extends Comparable<T>> int countGreaterThan(T[] anArray, T elem) {
        int count = 0;
        for (T e : anArray) {
            if (e.compareTo(elem) > 0) {
                ++count;
            }
        }
        return count;
    }

    public static void process(List<? extends A> list) {
    }

    public static void printList(List<?> list) {
        for (Object elem : list) {
            System.out.print(elem + " ");
        }
        System.out.println();
    }

    public static void addNumbers(List<? super Integer> list) {
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }
    }

    public static void main(String[] args) throws Exception {
        new TestGenerics().testRun();
    }

    public Enumeration<String> testThis() {
        return new TestConstructor(this);
    }

    private List<String> test1(Map<String, String> map) {
        List<String> list = new ArrayList<String>();
        String str = map.get("key");
        list.add(str);
        return list;
    }

    public void test2(Map<String, String> map, List<Object> list) {
        String str = map.get("key");
        list.add(str);
    }

    public void test3(List<Object> list, int a, float[] b, String[] c, String[][][] d) {

    }

    @Override
    public boolean testRun() throws Exception {
        assertTrue(test1(new HashMap<String, String>()) != null);
        // TODO: add other checks
        return true;
    }

    public interface MyComparable<T> {
        public int compareTo(T o);
    }

    public interface Pair<K, LongGenericType> {
        public K getKey();

        public LongGenericType getValue();
    }

    interface B {
    }

    interface C {
    }

    public static class GenericClass implements MyComparable<String> {
        @Override
        public int compareTo(String o) {
            return 0;
        }
    }

    public static class Box<T> {
        private T t;

        public void set(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }
    }

    public static class OrderedPair<K, V> implements Pair<K, V> {
        private final K key;
        private final V value;

        public OrderedPair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }
    }

    public static class Util {
        // Generic static method
        public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
            return p1.getKey().equals(p2.getKey()) &&
                    p1.getValue().equals(p2.getValue());
        }
    }

    public class NaturalNumber<T extends Integer> {
        private final T n;

        public NaturalNumber(T n) {
            this.n = n;
        }

        public boolean isEven() {
            return n.intValue() % 2 == 0;
        }
    }

    class A {
    }

    class D<T extends A & B & C> {
    }

    public class Node<T extends Comparable<T>> {
        private final T data;
        private final Node<T> next;

        public Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }

        public T getData() {
            return data;
        }
    }

    private class TestConstructor implements Enumeration<String> {
        private final TestGenerics a;

        TestConstructor(TestGenerics a) {
            this.a = a;
        }

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public String nextElement() {
            return null;
        }
    }
}
