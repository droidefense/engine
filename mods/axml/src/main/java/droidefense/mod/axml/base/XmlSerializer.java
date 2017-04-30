package droidefense.mod.axml.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface XmlSerializer {

    void setFeature(String key, boolean value) throws IllegalArgumentException, IllegalStateException;

    boolean getFeature(String key);

    void setProperty(String key, Object value) throws IllegalArgumentException, IllegalStateException;

    Object getProperty(String key);

    void setOutput(OutputStream key, String value) throws IOException, IllegalArgumentException, IllegalStateException;

    void setOutput(Writer key) throws IOException, IllegalArgumentException, IllegalStateException;

    void startDocument(String key, Boolean value) throws IOException, IllegalArgumentException, IllegalStateException;

    void endDocument() throws IOException, IllegalArgumentException, IllegalStateException;

    void setPrefix(String key, String value) throws IOException, IllegalArgumentException, IllegalStateException;

    String getPrefix(String key, boolean value) throws IllegalArgumentException;

    int getDepth();

    String getNamespace();

    String getName();

    XmlSerializer startTag(String key, String value) throws IOException, IllegalArgumentException, IllegalStateException;

    XmlSerializer attribute(String key, String value, String var3) throws IOException, IllegalArgumentException, IllegalStateException;

    XmlSerializer endTag(String key, String value) throws IOException, IllegalArgumentException, IllegalStateException;

    XmlSerializer text(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    XmlSerializer text(char[] key, int value, int var3) throws IOException, IllegalArgumentException, IllegalStateException;

    void cdsect(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void entityRef(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void processingInstruction(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void comment(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void docdecl(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void ignorableWhitespace(String key) throws IOException, IllegalArgumentException, IllegalStateException;

    void flush() throws IOException;
}
