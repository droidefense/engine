package droidefense.axml.axml;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is example usage of AXMLParser class.
 * <p>
 * Prints xml document from Android's binary xml file.
 */
public class AXMLPrinter {
    private static final String DEFAULT_XML = "AndroidManifest.xml";
    private static final float RADIX_MULTS[] = {
            0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F
    };
    private static final String DIMENSION_UNITS[] = {
            "px", "dip", "sp", "pt", "in", "mm", "", ""
    };
    private static final String FRACTION_UNITS[] = {
            "%", "%p", "", "", "", "", "", ""
    };

    public static void main(String[] arguments) {
        if (arguments.length < 1) {
            System.out.println("Usage: AXMLPrinter <APK FILE PATH>");
            return;
        }
//		String apkPath = "/tmp/path/to/file.apk";
        String apkPath = arguments[0];
        String result = null;
        try {
            result = GetManifestXMLFromAPK(apkPath);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public static String GetManifestXMLFromBytes(byte[] raw) throws IOException, XmlPullParserException {
        AXmlResourceParser parser = new AXmlResourceParser();
        InputStream strm = new ByteArrayInputStream(raw);
        parser.open(strm);

        return processStream(parser);
    }

    public static String GetManifestXMLFromAPK(String apkPath) throws IOException, XmlPullParserException {
        ZipFile file = null;
        File apkFile = new File(apkPath);
        file = new ZipFile(apkFile, ZipFile.OPEN_READ);
        ZipEntry entry = file.getEntry(DEFAULT_XML);

        AXmlResourceParser parser = new AXmlResourceParser();
        InputStream strm = file.getInputStream(entry);
        parser.open(strm);

        return processStream(parser);
    }

    private static String processStream(AXmlResourceParser parser) throws IOException, XmlPullParserException {
        StringBuilder xmlSb = new StringBuilder(100);
        StringBuilder sb = new StringBuilder(10);
        final String indentStep = "	";

        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_DOCUMENT: {
                    log(xmlSb, "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
                    break;
                }
                case XmlPullParser.START_TAG: {
                    log(false, xmlSb, "%s<%s%s", sb,
                            getNamespacePrefix(parser.getPrefix()), parser.getName());
                    sb.append(indentStep);

                    int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                    int namespaceCount = parser.getNamespaceCount(parser.getDepth());

                    for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                        log(xmlSb, "%sxmlns:%s=\"%s\"",
                                i == namespaceCountBefore ? "  " : sb,
                                parser.getNamespacePrefix(i),
                                parser.getNamespaceUri(i));
                    }

                    for (int i = 0, size = parser.getAttributeCount(); i != size; ++i) {
                        log(false, xmlSb, "%s%s%s=\"%s\"", " ",
                                getNamespacePrefix(parser.getAttributePrefix(i)),
                                parser.getAttributeName(i),
                                getAttributeValue(parser, i));
                    }
//						log("%s>",sb);
                    log(xmlSb, ">");
                    break;
                }
                case XmlPullParser.END_TAG: {
                    sb.setLength(sb.length() - indentStep.length());
                    log(xmlSb, "%s</%s%s>", sb,
                            getNamespacePrefix(parser.getPrefix()),
                            parser.getName());
                    break;
                }
                case XmlPullParser.TEXT: {
                    log(xmlSb, "%s%s", sb, parser.getText());
                    break;
                }
            }
        }
        parser.close();
        return "";
    }

    private static String getNamespacePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }
        return prefix + ":";
    }

    public static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data)) +
                    DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data)) +
                    FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }


    /////////////////////////////////// ILLEGAL STUFF, DONT LOOK :)

    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    private static void log(StringBuilder xmlSb, String format, Object... arguments) {
        log(true, xmlSb, format, arguments);
    }

    private static void log(boolean newLine, StringBuilder xmlSb, String format, Object... arguments) {
//		System.out.printf(format,arguments);
//		if(newLine) System.out.println();
        xmlSb.append(String.format(format, arguments));
        if (newLine) xmlSb.append("\n");
    }

    public static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }
}
