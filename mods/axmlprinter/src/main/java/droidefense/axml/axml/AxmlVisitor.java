package droidefense.axml.axml;

public class AxmlVisitor extends NodeVisitor {

    public AxmlVisitor() {
        super();

    }

    public AxmlVisitor(NodeVisitor av) {
        super(av);
    }

    /**
     * create a ns
     *
     * @param prefix
     * @param uri
     * @param ln
     */
    public void ns(String prefix, String uri, int ln) {
        if (nv != null && nv instanceof AxmlVisitor) {
            ((AxmlVisitor) nv).ns(prefix, uri, ln);
        }
    }

}
