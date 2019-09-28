package droidefense.axml.axml;

public class EmptyAdapter extends AxmlVisitor {

    public EmptyAdapter() {
        super(null);
    }

    public NodeVisitor first(String ns, String name) {
        return new EmptyNode();
    }

    public static class EmptyNode extends NodeVisitor {

        public EmptyNode() {
            super(null);
        }

        @Override
        public NodeVisitor child(String ns, String name) {
            return new EmptyNode();
        }
    }

}
