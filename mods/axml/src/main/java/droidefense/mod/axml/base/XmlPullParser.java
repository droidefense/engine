package droidefense.mod.axml.base;

import droidefense.mod.axml.exception.XmlPullParserException;

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


    void setFeature(String key, boolean value) throws XmlPullParserException;

    boolean getFeature(String key);

    void setProperty(String key, Object value) throws XmlPullParserException;

    Object getProperty(String key);

    void setInput(Reader key) throws XmlPullParserException;

    void setInput(InputStream key, String value) throws XmlPullParserException;

    String getInputEncoding();

    void defineEntityReplacementText(String key, String value) throws XmlPullParserException;

    int getNamespaceCount(int key) throws XmlPullParserException;

    String getNamespacePrefix(int key) throws XmlPullParserException;

    String getNamespaceUri(int key) throws XmlPullParserException;

    String getNamespace(String key);

    int getDepth();

    String getPositionDescription();

    int getLineNumber();

    int getColumnNumber();

    boolean isWhitespace() throws XmlPullParserException;

    String getText();

    char[] getTextCharacters(int[] key);

    String getNamespace();

    String getName();

    String getPrefix();

    boolean isEmptyElementTag() throws XmlPullParserException;

    int getAttributeCount();

    String getAttributeNamespace(int key);

    String getAttributeName(int key);

    String getAttributePrefix(int key);

    String getAttributeType(int key);

    boolean isAttributeDefault(int key);

    String getAttributeValue(int key);

    String getAttributeValue(String key, String value);

    int getEventType() throws XmlPullParserException;

    int next() throws XmlPullParserException, IOException;

    int nextToken() throws XmlPullParserException, IOException;

    void require(int key, String value, String var3) throws XmlPullParserException, IOException;

    String nextText() throws XmlPullParserException, IOException;

    int nextTag() throws XmlPullParserException, IOException;

}
