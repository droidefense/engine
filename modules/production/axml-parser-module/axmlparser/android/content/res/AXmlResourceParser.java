package axmlparser.android.content.res;

import axmlparser.org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class AXmlResourceParser implements XmlResourceParser {

    private static final String E_NOT_SUPPORTED = "Method is not supported.";
    private static final int ATTRIBUTE_IX_NAMESPACE_URI = 0;
    private static final int ATTRIBUTE_IX_NAME = 1;
    private static final int ATTRIBUTE_IX_VALUE_STRING = 2;
    private static final int ATTRIBUTE_IX_VALUE_TYPE = 3;
    private static final int ATTRIBUTE_IX_VALUE_DATA = 4;
    private static final int ATTRIBUTE_LENGHT = 5;
    private static final int CHUNK_AXML_FILE = 524291;
    private static final int CHUNK_RESOURCEIDS = 524672;
    private static final int CHUNK_XML_FIRST = 1048832;
    private static final int CHUNK_XML_START_NAMESPACE = 1048832;
    private static final int CHUNK_XML_END_NAMESPACE = 1048833;
    private static final int CHUNK_XML_START_TAG = 1048834;
    private static final int CHUNK_XML_END_TAG = 1048835;
    private static final int CHUNK_XML_TEXT = 1048836;
    private static final int CHUNK_XML_LAST = 1048836;
    private IntReader m_reader;
    private boolean m_operational = false;
    private StringBlock m_strings;
    private int[] m_resourceIDs;
    private AXmlResourceParser.NamespaceStack m_namespaces = new AXmlResourceParser.NamespaceStack();
    private boolean m_decreaseDepth;
    private int m_event;
    private int m_lineNumber;
    private int m_name;
    private int m_namespaceUri;
    private int[] m_attributes;
    private int m_idAttribute;
    private int m_classAttribute;
    private int m_styleAttribute;


    public AXmlResourceParser() {
        this.resetEventInfo();
    }

    public void open(InputStream stream) {
        this.close();
        if (stream != null) {
            this.m_reader = new IntReader(stream, false);
        }

    }

    public void close() {
        if (this.m_operational) {
            this.m_operational = false;
            this.m_reader.close();
            this.m_reader = null;
            this.m_strings = null;
            this.m_resourceIDs = null;
            this.m_namespaces.reset();
            this.resetEventInfo();
        }
    }

    public int next() throws XmlPullParserException, IOException {
        if (this.m_reader == null) {
            throw new XmlPullParserException("Parser is not opened.", this, (Throwable) null);
        } else {
            try {
                this.doNext();
                return this.m_event;
            } catch (IOException var2) {
                this.close();
                throw var2;
            }
        }
    }

    public int nextToken() throws XmlPullParserException, IOException {
        return this.next();
    }

    public int nextTag() throws XmlPullParserException, IOException {
        int eventType = this.next();
        if (eventType == 4 && this.isWhitespace()) {
            eventType = this.next();
        }

        if (eventType != 2 && eventType != 3) {
            throw new XmlPullParserException("Expected start or end tag.", this, (Throwable) null);
        } else {
            return eventType;
        }
    }

    public String nextText() throws XmlPullParserException, IOException {
        if (this.getEventType() != 2) {
            throw new XmlPullParserException("Parser must be on START_TAG to read next text.", this, (Throwable) null);
        } else {
            int eventType = this.next();
            if (eventType == 4) {
                String result = this.getText();
                eventType = this.next();
                if (eventType != 3) {
                    throw new XmlPullParserException("Event TEXT must be immediately followed by END_TAG.", this, (Throwable) null);
                } else {
                    return result;
                }
            } else if (eventType == 3) {
                return "";
            } else {
                throw new XmlPullParserException("Parser must be on START_TAG or TEXT to read text.", this, (Throwable) null);
            }
        }
    }

    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (type != this.getEventType() || namespace != null && !namespace.equals(this.getNamespace()) || name != null && !name.equals(this.getName())) {
            throw new XmlPullParserException(TYPES[type] + " is expected.", this, (Throwable) null);
        }
    }

    public int getDepth() {
        return this.m_namespaces.getDepth() - 1;
    }

    public int getEventType() throws XmlPullParserException {
        return this.m_event;
    }

    public int getLineNumber() {
        return this.m_lineNumber;
    }

    public String getName() {
        return this.m_name != -1 && (this.m_event == 2 || this.m_event == 3) ? this.m_strings.getString(this.m_name) : null;
    }

    public String getText() {
        return this.m_name != -1 && this.m_event == 4 ? this.m_strings.getString(this.m_name) : null;
    }

    public char[] getTextCharacters(int[] holderForStartAndLength) {
        String text = this.getText();
        if (text == null) {
            return null;
        } else {
            holderForStartAndLength[0] = 0;
            holderForStartAndLength[1] = text.length();
            char[] chars = new char[text.length()];
            text.getChars(0, text.length(), chars, 0);
            return chars;
        }
    }

    public String getNamespace() {
        return this.m_strings.getString(this.m_namespaceUri);
    }

    public String getPrefix() {
        int prefix = this.m_namespaces.findPrefix(this.m_namespaceUri);
        return this.m_strings.getString(prefix);
    }

    public String getPositionDescription() {
        return "XML line #" + this.getLineNumber();
    }

    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return this.m_namespaces.getAccumulatedCount(depth);
    }

    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        int prefix = this.m_namespaces.getPrefix(pos);
        return this.m_strings.getString(prefix);
    }

    public String getNamespaceUri(int pos) throws XmlPullParserException {
        int uri = this.m_namespaces.getUri(pos);
        return this.m_strings.getString(uri);
    }

    public String getClassAttribute() {
        if (this.m_classAttribute == -1) {
            return null;
        } else {
            int offset = this.getAttributeOffset(this.m_classAttribute);
            int value = this.m_attributes[offset + 2];
            return this.m_strings.getString(value);
        }
    }

    public String getIdAttribute() {
        if (this.m_idAttribute == -1) {
            return null;
        } else {
            int offset = this.getAttributeOffset(this.m_idAttribute);
            int value = this.m_attributes[offset + 2];
            return this.m_strings.getString(value);
        }
    }

    public int getIdAttributeResourceValue(int defaultValue) {
        if (this.m_idAttribute == -1) {
            return defaultValue;
        } else {
            int offset = this.getAttributeOffset(this.m_idAttribute);
            int valueType = this.m_attributes[offset + 3];
            return valueType != 1 ? defaultValue : this.m_attributes[offset + 4];
        }
    }

    public int getStyleAttribute() {
        if (this.m_styleAttribute == -1) {
            return 0;
        } else {
            int offset = this.getAttributeOffset(this.m_styleAttribute);
            return this.m_attributes[offset + 4];
        }
    }

    public int getAttributeCount() {
        return this.m_event != 2 ? -1 : this.m_attributes.length / 5;
    }

    public String getAttributeNamespace(int index) {
        int offset = this.getAttributeOffset(index);
        int namespace = this.m_attributes[offset + 0];
        return namespace == -1 ? "" : this.m_strings.getString(namespace);
    }

    public String getAttributePrefix(int index) {
        int offset = this.getAttributeOffset(index);
        int uri = this.m_attributes[offset + 0];
        int prefix = this.m_namespaces.findPrefix(uri);
        return prefix == -1 ? "" : this.m_strings.getString(prefix);
    }

    public String getAttributeName(int index) {
        int offset = this.getAttributeOffset(index);
        int name = this.m_attributes[offset + 1];
        return name == -1 ? "" : this.m_strings.getString(name);
    }

    public int getAttributeNameResource(int index) {
        int offset = this.getAttributeOffset(index);
        int name = this.m_attributes[offset + 1];
        return this.m_resourceIDs != null && name >= 0 && name < this.m_resourceIDs.length ? this.m_resourceIDs[name] : 0;
    }

    public int getAttributeValueType(int index) {
        int offset = this.getAttributeOffset(index);
        return this.m_attributes[offset + 3];
    }

    public int getAttributeValueData(int index) {
        int offset = this.getAttributeOffset(index);
        return this.m_attributes[offset + 4];
    }

    public String getAttributeValue(int index) {
        int offset = this.getAttributeOffset(index);
        int valueType = this.m_attributes[offset + 3];
        if (valueType == 3) {
            int valueData = this.m_attributes[offset + 2];
            return this.m_strings.getString(valueData);
        } else {
            int var10000 = this.m_attributes[offset + 4];
            return "";
        }
    }

    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return this.getAttributeIntValue(index, defaultValue ? 1 : 0) != 0;
    }

    public float getAttributeFloatValue(int index, float defaultValue) {
        int offset = this.getAttributeOffset(index);
        int valueType = this.m_attributes[offset + 3];
        if (valueType == 4) {
            int valueData = this.m_attributes[offset + 4];
            return Float.intBitsToFloat(valueData);
        } else {
            return defaultValue;
        }
    }

    public int getAttributeIntValue(int index, int defaultValue) {
        int offset = this.getAttributeOffset(index);
        int valueType = this.m_attributes[offset + 3];
        return valueType >= 16 && valueType <= 31 ? this.m_attributes[offset + 4] : defaultValue;
    }

    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return this.getAttributeIntValue(index, defaultValue);
    }

    public int getAttributeResourceValue(int index, int defaultValue) {
        int offset = this.getAttributeOffset(index);
        int valueType = this.m_attributes[offset + 3];
        return valueType == 1 ? this.m_attributes[offset + 4] : defaultValue;
    }

    public String getAttributeValue(String namespace, String attribute) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? null : this.getAttributeValue(index);
    }

    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? defaultValue : this.getAttributeBooleanValue(index, defaultValue);
    }

    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? defaultValue : this.getAttributeFloatValue(index, defaultValue);
    }

    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? defaultValue : this.getAttributeIntValue(index, defaultValue);
    }

    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? defaultValue : this.getAttributeUnsignedIntValue(index, defaultValue);
    }

    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        int index = this.findAttribute(namespace, attribute);
        return index == -1 ? defaultValue : this.getAttributeResourceValue(index, defaultValue);
    }

    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return 0;
    }

    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return 0;
    }

    public String getAttributeType(int index) {
        return "CDATA";
    }

    public boolean isAttributeDefault(int index) {
        return false;
    }

    public void setInput(InputStream stream, String inputEncoding) throws XmlPullParserException {
        throw new XmlPullParserException("Method is not supported.");
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        throw new XmlPullParserException("Method is not supported.");
    }

    public String getInputEncoding() {
        return null;
    }

    public int getColumnNumber() {
        return -1;
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {
        return false;
    }

    public boolean isWhitespace() throws XmlPullParserException {
        return false;
    }

    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        throw new XmlPullParserException("Method is not supported.");
    }

    public String getNamespace(String prefix) {
        throw new RuntimeException("Method is not supported.");
    }

    public Object getProperty(String name) {
        return null;
    }

    public void setProperty(String name, Object value) throws XmlPullParserException {
        throw new XmlPullParserException("Method is not supported.");
    }

    public boolean getFeature(String feature) {
        return false;
    }

    public void setFeature(String name, boolean value) throws XmlPullParserException {
        throw new XmlPullParserException("Method is not supported.");
    }

    final StringBlock getStrings() {
        return this.m_strings;
    }

    private int getAttributeOffset(int index) {
        if (this.m_event != 2) {
            throw new IndexOutOfBoundsException("Current event is not START_TAG.");
        } else {
            int offset = index * 5;
            if (offset >= this.m_attributes.length) {
                throw new IndexOutOfBoundsException("Invalid attribute index (" + index + ").");
            } else {
                return offset;
            }
        }
    }

    private int findAttribute(String namespace, String attribute) {
        if (this.m_strings != null && attribute != null) {
            int name = this.m_strings.find(attribute);
            if (name == -1) {
                return -1;
            } else {
                int uri = namespace != null ? this.m_strings.find(namespace) : -1;

                for (int o = 0; o != this.m_attributes.length; ++o) {
                    if (name == this.m_attributes[o + 1] && (uri == -1 || uri == this.m_attributes[o + 0])) {
                        return o / 5;
                    }
                }

                return -1;
            }
        } else {
            return -1;
        }
    }

    private void resetEventInfo() {
        this.m_event = -1;
        this.m_lineNumber = -1;
        this.m_name = -1;
        this.m_namespaceUri = -1;
        this.m_attributes = null;
        this.m_idAttribute = -1;
        this.m_classAttribute = -1;
        this.m_styleAttribute = -1;
    }

    private void doNext() throws IOException {
        if (this.m_strings == null) {
            ChunkUtil.readCheckType(this.m_reader, 524291);
            this.m_reader.skipInt();
            this.m_strings = StringBlock.read(this.m_reader);
            this.m_namespaces.increaseDepth();
            this.m_operational = true;
        }

        if (this.m_event != 1) {
            int event = this.m_event;
            this.resetEventInfo();

            while (true) {
                if (this.m_decreaseDepth) {
                    this.m_decreaseDepth = false;
                    this.m_namespaces.decreaseDepth();
                }

                if (event == 3 && this.m_namespaces.getDepth() == 1 && this.m_namespaces.getCurrentCount() == 0) {
                    this.m_event = 1;
                } else {
                    int chunkType;
                    if (event == 0) {
                        chunkType = 1048834;
                    } else {
                        chunkType = this.m_reader.readInt();
                    }

                    int lineNumber;
                    if (chunkType == 524672) {
                        lineNumber = this.m_reader.readInt();
                        if (lineNumber >= 8 && lineNumber % 4 == 0) {
                            this.m_resourceIDs = this.m_reader.readIntArray(lineNumber / 4 - 2);
                            continue;
                        }

                        throw new IOException("Invalid resource ids size (" + lineNumber + ").");
                    }

                    if (chunkType < 1048832 || chunkType > 1048836) {
                        throw new IOException("Invalid chunk type (" + chunkType + ").");
                    }

                    if (chunkType == 1048834 && event == -1) {
                        this.m_event = 0;
                    } else {
                        this.m_reader.skipInt();
                        lineNumber = this.m_reader.readInt();
                        this.m_reader.skipInt();
                        int attributeCount;
                        int i;
                        if (chunkType == 1048832 || chunkType == 1048833) {
                            if (chunkType == 1048832) {
                                attributeCount = this.m_reader.readInt();
                                i = this.m_reader.readInt();
                                this.m_namespaces.push(attributeCount, i);
                            } else {
                                this.m_reader.skipInt();
                                this.m_reader.skipInt();
                                this.m_namespaces.pop();
                            }
                            continue;
                        }

                        this.m_lineNumber = lineNumber;
                        if (chunkType == 1048834) {
                            this.m_namespaceUri = this.m_reader.readInt();
                            this.m_name = this.m_reader.readInt();
                            this.m_reader.skipInt();
                            attributeCount = this.m_reader.readInt();
                            this.m_idAttribute = (attributeCount >>> 16) - 1;
                            attributeCount &= '\uffff';
                            this.m_classAttribute = this.m_reader.readInt();
                            this.m_styleAttribute = (this.m_classAttribute >>> 16) - 1;
                            this.m_classAttribute = (this.m_classAttribute & '\uffff') - 1;
                            this.m_attributes = this.m_reader.readIntArray(attributeCount * 5);

                            for (i = 3; i < this.m_attributes.length; i += 5) {
                                this.m_attributes[i] >>>= 24;
                            }

                            this.m_namespaces.increaseDepth();
                            this.m_event = 2;
                        } else if (chunkType == 1048835) {
                            this.m_namespaceUri = this.m_reader.readInt();
                            this.m_name = this.m_reader.readInt();
                            this.m_event = 3;
                            this.m_decreaseDepth = true;
                        } else {
                            if (chunkType != 1048836) {
                                continue;
                            }

                            this.m_name = this.m_reader.readInt();
                            this.m_reader.skipInt();
                            this.m_reader.skipInt();
                            this.m_event = 4;
                        }
                    }
                }

                return;
            }
        }
    }

    private static final class NamespaceStack {

        private int[] m_data = new int[32];
        private int m_dataLength;
        private int m_count;
        private int m_depth;


        public final void reset() {
            this.m_dataLength = 0;
            this.m_count = 0;
            this.m_depth = 0;
        }

        public final int getTotalCount() {
            return this.m_count;
        }

        public final int getCurrentCount() {
            if (this.m_dataLength == 0) {
                return 0;
            } else {
                int offset = this.m_dataLength - 1;
                return this.m_data[offset];
            }
        }

        public final int getAccumulatedCount(int depth) {
            if (this.m_dataLength != 0 && depth >= 0) {
                if (depth > this.m_depth) {
                    depth = this.m_depth;
                }

                int accumulatedCount = 0;

                for (int offset = 0; depth != 0; --depth) {
                    int count = this.m_data[offset];
                    accumulatedCount += count;
                    offset += 2 + count * 2;
                }

                return accumulatedCount;
            } else {
                return 0;
            }
        }

        public final void push(int prefix, int uri) {
            if (this.m_depth == 0) {
                this.increaseDepth();
            }

            this.ensureDataCapacity(2);
            int offset = this.m_dataLength - 1;
            int count = this.m_data[offset];
            this.m_data[offset - 1 - count * 2] = count + 1;
            this.m_data[offset] = prefix;
            this.m_data[offset + 1] = uri;
            this.m_data[offset + 2] = count + 1;
            this.m_dataLength += 2;
            ++this.m_count;
        }

        public final boolean pop(int prefix, int uri) {
            if (this.m_dataLength == 0) {
                return false;
            } else {
                int offset = this.m_dataLength - 1;
                int count = this.m_data[offset];
                int i = 0;

                for (int o = offset - 2; i != count; o -= 2) {
                    if (this.m_data[o] == prefix && this.m_data[o + 1] == uri) {
                        --count;
                        if (i == 0) {
                            this.m_data[o] = count;
                            o -= 1 + count * 2;
                            this.m_data[o] = count;
                        } else {
                            this.m_data[offset] = count;
                            offset -= 3 + count * 2;
                            this.m_data[offset] = count;
                            System.arraycopy(this.m_data, o + 2, this.m_data, o, this.m_dataLength - o);
                        }

                        this.m_dataLength -= 2;
                        --this.m_count;
                        return true;
                    }

                    ++i;
                }

                return false;
            }
        }

        public final boolean pop() {
            if (this.m_dataLength == 0) {
                return false;
            } else {
                int offset = this.m_dataLength - 1;
                int count = this.m_data[offset];
                if (count == 0) {
                    return false;
                } else {
                    --count;
                    offset -= 2;
                    this.m_data[offset] = count;
                    offset -= 1 + count * 2;
                    this.m_data[offset] = count;
                    this.m_dataLength -= 2;
                    --this.m_count;
                    return true;
                }
            }
        }

        public final int getPrefix(int index) {
            return this.get(index, true);
        }

        public final int getUri(int index) {
            return this.get(index, false);
        }

        public final int findPrefix(int uri) {
            return this.find(uri, false);
        }

        public final int findUri(int prefix) {
            return this.find(prefix, true);
        }

        public final int getDepth() {
            return this.m_depth;
        }

        public final void increaseDepth() {
            this.ensureDataCapacity(2);
            int offset = this.m_dataLength;
            this.m_data[offset] = 0;
            this.m_data[offset + 1] = 0;
            this.m_dataLength += 2;
            ++this.m_depth;
        }

        public final void decreaseDepth() {
            if (this.m_dataLength != 0) {
                int offset = this.m_dataLength - 1;
                int count = this.m_data[offset];
                if (offset - 1 - count * 2 != 0) {
                    this.m_dataLength -= 2 + count * 2;
                    this.m_count -= count;
                    --this.m_depth;
                }
            }
        }

        private void ensureDataCapacity(int capacity) {
            int available = this.m_data.length - this.m_dataLength;
            if (available <= capacity) {
                int newLength = (this.m_data.length + available) * 2;
                int[] newData = new int[newLength];
                System.arraycopy(this.m_data, 0, newData, 0, this.m_dataLength);
                this.m_data = newData;
            }
        }

        private int find(int prefixOrUri, boolean prefix) {
            if (this.m_dataLength == 0) {
                return -1;
            } else {
                int offset = this.m_dataLength - 1;

                for (int i = this.m_depth; i != 0; --i) {
                    int count = this.m_data[offset];

                    for (offset -= 2; count != 0; --count) {
                        if (prefix) {
                            if (this.m_data[offset] == prefixOrUri) {
                                return this.m_data[offset + 1];
                            }
                        } else if (this.m_data[offset + 1] == prefixOrUri) {
                            return this.m_data[offset];
                        }

                        offset -= 2;
                    }
                }

                return -1;
            }
        }

        private int get(int index, boolean prefix) {
            if (this.m_dataLength != 0 && index >= 0) {
                int offset = 0;

                for (int i = this.m_depth; i != 0; --i) {
                    int count = this.m_data[offset];
                    if (index < count) {
                        offset += 1 + index * 2;
                        if (!prefix) {
                            ++offset;
                        }

                        return this.m_data[offset];
                    }

                    index -= count;
                    offset += 2 + count * 2;
                }

                return -1;
            } else {
                return -1;
            }
        }
    }
}
