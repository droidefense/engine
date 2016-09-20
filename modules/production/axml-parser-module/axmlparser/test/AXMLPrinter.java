package axmlparser.test;

import axmlparser.android.content.res.AXmlResourceParser;
import axmlparser.org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AXMLPrinter {

    private static final float[] RADIX_MULTS = new float[]{0.00390625F, 3.051758E-5F, 1.192093E-7F, 4.656613E-10F};
    private static final String[] DIMENSION_UNITS = new String[]{"px", "dip", "sp", "pt", "in", "mm", "", ""};
    private static final String[] FRACTION_UNITS = new String[]{"%", "%p", "", "", "", "", "", ""};
    private String result;


    public AXMLPrinter(File f) throws XmlPullParserException, IOException {
        this.setResult("");
        AXmlResourceParser parser = new AXmlResourceParser();
        parser.open(new FileInputStream(f));
        StringBuilder indent = new StringBuilder(10);
        String indentStep = "\t";

        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }

            switch (type) {
                case 0:
                    this.log("<?xml version=\"1.0\" encoding=\"utf-8\"?>", new Object[0]);
                case 1:
                default:
                    break;
                case 2:
                    this.log("%s<%s%s", new Object[]{indent, this.getNamespacePrefix(parser.getPrefix()), parser.getName()});
                    indent.append("\t");
                    int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                    int namespaceCount = parser.getNamespaceCount(parser.getDepth());

                    int i;
                    for (i = namespaceCountBefore; i != namespaceCount; ++i) {
                        this.log("%sxmlns:%s=\"%s\"", new Object[]{indent, parser.getNamespacePrefix(i), parser.getNamespaceUri(i)});
                    }

                    for (i = 0; i != parser.getAttributeCount(); ++i) {
                        this.log("%s%s%s=\"%s\"", new Object[]{indent, this.getNamespacePrefix(parser.getAttributePrefix(i)), parser.getAttributeName(i), this.getAttributeValue(parser, i)});
                    }

                    this.log("%s>", new Object[]{indent});
                    break;
                case 3:
                    indent.setLength(indent.length() - "\t".length());
                    this.log("%s</%s%s>", new Object[]{indent, this.getNamespacePrefix(parser.getPrefix()), parser.getName()});
                    break;
                case 4:
                    this.log("%s%s", new Object[]{indent, parser.getText()});
            }
        }
    }

    private static float complexToFloat(int complex) {
        return (float) (complex & -256) * RADIX_MULTS[complex >> 4 & 3];
    }

    private String getNamespacePrefix(String prefix) {
        return prefix != null && prefix.length() != 0 ? prefix + ":" : "";
    }

    private String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        return type == 3 ? parser.getAttributeValue(index) : (type == 2 ? String.format("?%s%08X", new Object[]{this.getPackage(data), Integer.valueOf(data)}) : (type == 1 ? String.format("@%s%08X", new Object[]{this.getPackage(data), Integer.valueOf(data)}) : (type == 4 ? String.valueOf(Float.intBitsToFloat(data)) : (type == 17 ? String.format("0x%08X", new Object[]{Integer.valueOf(data)}) : (type == 18 ? (data != 0 ? "true" : "false") : (type == 5 ? Float.toString(complexToFloat(data)) + DIMENSION_UNITS[data & 15] : (type == 6 ? Float.toString(complexToFloat(data)) + FRACTION_UNITS[data & 15] : (type >= 28 && type <= 31 ? String.format("#%08X", new Object[]{Integer.valueOf(data)}) : (type >= 16 && type <= 31 ? String.valueOf(data) : String.format("<0x%X, type 0x%02X>", new Object[]{Integer.valueOf(data), Integer.valueOf(type)}))))))))));
    }

    private String getPackage(int id) {
        return id >>> 24 == 1 ? "android:" : "";
    }

    private void log(String format, Object... arguments) {
        this.setResult(this.getResult() + String.format(format, arguments) + "\n");
    }

    public String getResult() {
        return this.result;
    }

    void setResult(String result) {
        this.result = result;
    }

}
