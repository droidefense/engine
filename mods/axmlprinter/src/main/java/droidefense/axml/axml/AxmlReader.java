package droidefense.axml.axml;

import java.io.IOException;
import java.util.Stack;

public class AxmlReader {
    public static final NodeVisitor EMPTY_VISITOR = new NodeVisitor() {

        @Override
        public NodeVisitor child(String ns, String name) {
            return this;
        }

    };
    final AxmlParser parser;

    public AxmlReader(byte[] data) {
        super();
        this.parser = new AxmlParser(data);
    }

    public void accept(final AxmlVisitor av) throws IOException {
        Stack<NodeVisitor> nvs = new Stack<NodeVisitor>();
        NodeVisitor tos = av;
        while (true) {
            int type = parser.next();
            switch (type) {
                case AxmlParser.START_FILE:
                    break;
                case AxmlParser.START_TAG:
                    nvs.push(tos);
                    tos = tos.child(parser.getNamespaceUri(), parser.getName());
                    if (tos != null) {
                        if (tos != EMPTY_VISITOR) {
                            tos.line(parser.getLineNumber());
                            for (int i = 0; i < parser.getAttrCount(); i++) {
                                tos.attr(parser.getAttrNs(i), parser.getAttrName(i), parser.getAttrResId(i),
                                        parser.getAttrType(i), parser.getAttrValue(i));
                            }
                        }
                    } else {
                        tos = EMPTY_VISITOR;
                    }
                    break;
                case AxmlParser.END_TAG:
                    tos.end();
                    tos = nvs.pop();
                    break;
                case AxmlParser.START_NS:
                    av.ns(parser.getNamespacePrefix(), parser.getNamespaceUri(), parser.getLineNumber());
                    break;
                case AxmlParser.END_NS:
                    break;
                case AxmlParser.TEXT:
                    tos.text(parser.getLineNumber(), parser.getText());
                    break;
                case AxmlParser.END_FILE:
                    return;
                default:
                    System.err.println("AxmlReader: Unsupported tag: " + type);
            }
        }
    }
}
