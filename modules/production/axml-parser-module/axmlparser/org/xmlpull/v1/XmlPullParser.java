package axmlparser.org.xmlpull.v1;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface XmlPullParser {

    String NO_NAMESPACE = "";
    int START_DOCUMENT = 0;
    int END_DOCUMENT = 1;
    int START_TAG = 2;
    int END_TAG = 3;
    int TEXT = 4;
    int CDSECT = 5;
    int ENTITY_REF = 6;
    int IGNORABLE_WHITESPACE = 7;
    int PROCESSING_INSTRUCTION = 8;
    int COMMENT = 9;
    int DOCDECL = 10;
    String[] TYPES = new String[]{"START_DOCUMENT", "END_DOCUMENT", "START_TAG", "END_TAG", "TEXT", "CDSECT", "ENTITY_REF", "IGNORABLE_WHITESPACE", "PROCESSING_INSTRUCTION", "COMMENT", "DOCDECL"};
    String FEATURE_PROCESS_NAMESPACES = "http://xmlpull.org/v1/doc/features.html#process-namespaces";
    String FEATURE_REPORT_NAMESPACE_ATTRIBUTES = "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes";
    String FEATURE_PROCESS_DOCDECL = "http://xmlpull.org/v1/doc/features.html#process-docdecl";
    String FEATURE_VALIDATION = "http://xmlpull.org/v1/doc/features.html#validation";


    void setFeature(String var1, boolean var2) throws XmlPullParserException;

    boolean getFeature(String var1);

    void setProperty(String var1, Object var2) throws XmlPullParserException;

    Object getProperty(String var1);

    void setInput(Reader var1) throws XmlPullParserException;

    void setInput(InputStream var1, String var2) throws XmlPullParserException;

    String getInputEncoding();

    void defineEntityReplacementText(String var1, String var2) throws XmlPullParserException;

    int getNamespaceCount(int var1) throws XmlPullParserException;

    String getNamespacePrefix(int var1) throws XmlPullParserException;

    String getNamespaceUri(int var1) throws XmlPullParserException;

    String getNamespace(String var1);

    int getDepth();

    String getPositionDescription();

    int getLineNumber();

    int getColumnNumber();

    boolean isWhitespace() throws XmlPullParserException;

    String getText();

    char[] getTextCharacters(int[] var1);

    String getNamespace();

    String getName();

    String getPrefix();

    boolean isEmptyElementTag() throws XmlPullParserException;

    int getAttributeCount();

    String getAttributeNamespace(int var1);

    String getAttributeName(int var1);

    String getAttributePrefix(int var1);

    String getAttributeType(int var1);

    boolean isAttributeDefault(int var1);

    String getAttributeValue(int var1);

    String getAttributeValue(String var1, String var2);

    int getEventType() throws XmlPullParserException;

    int next() throws XmlPullParserException, IOException;

    int nextToken() throws XmlPullParserException, IOException;

    void require(int var1, String var2, String var3) throws XmlPullParserException, IOException;

    String nextText() throws XmlPullParserException, IOException;

    int nextTag() throws XmlPullParserException, IOException;

}
