package axmlparser.android.content.res;

import java.io.IOException;

public class StringBlock {

    private static final int CHUNK_TYPE = 1835009;
    private int[] m_stringOffsets;
    private int[] m_strings;
    private int[] m_styleOffsets;
    private int[] m_styles;


    public static StringBlock read(IntReader reader) throws IOException {
        ChunkUtil.readCheckType(reader, 1835009);
        int chunkSize = reader.readInt();
        int stringCount = reader.readInt();
        int styleOffsetCount = reader.readInt();
        reader.readInt();
        int stringsOffset = reader.readInt();
        int stylesOffset = reader.readInt();
        StringBlock block = new StringBlock();
        block.m_stringOffsets = reader.readIntArray(stringCount);
        if (styleOffsetCount != 0) {
            block.m_styleOffsets = reader.readIntArray(styleOffsetCount);
        }

        int size = (stylesOffset == 0 ? chunkSize : stylesOffset) - stringsOffset;
        if (size % 4 != 0) {
            throw new IOException("String data size is not multiple of 4 (" + size + ").");
        } else {
            block.m_strings = reader.readIntArray(size / 4);
            if (stylesOffset != 0) {
                size = chunkSize - stylesOffset;
                if (size % 4 != 0) {
                    throw new IOException("Style data size is not multiple of 4 (" + size + ").");
                }

                block.m_styles = reader.readIntArray(size / 4);
            }

            return block;
        }
    }

    private static int getShort(int[] array, int offset) {
        int value = array[offset / 4];
        return offset % 4 / 2 == 0 ? value & '\uffff' : value >>> 16;
    }

    public int getCount() {
        return this.m_stringOffsets != null ? this.m_stringOffsets.length : 0;
    }

    public String getString(int index) {
        if (index >= 0 && this.m_stringOffsets != null && index < this.m_stringOffsets.length) {
            int offset = this.m_stringOffsets[index];
            int length = getShort(this.m_strings, offset);

            StringBuilder result;
            for (result = new StringBuilder(length); length != 0; --length) {
                offset += 2;

                try {
                    result.append((char) getShort(this.m_strings, offset));
                } catch (Exception var6) {
                    var6.printStackTrace();
                    break;
                }
            }

            return result.toString();
        } else {
            return null;
        }
    }

    public CharSequence get(int index) {
        return this.getString(index);
    }

    public String getHTML(int index) {
        String raw = this.getString(index);
        if (raw == null) {
            return null;
        } else {
            int[] style = this.getStyle(index);
            if (style == null) {
                return raw;
            } else {
                StringBuilder html = new StringBuilder(raw.length() + 32);
                int offset = 0;

                while (true) {
                    int i = -1;

                    int start;
                    for (start = 0; start != style.length; start += 3) {
                        if (style[start + 1] != -1 && (i == -1 || style[i + 1] > style[start + 1])) {
                            i = start;
                        }
                    }

                    start = i != -1 ? style[i + 1] : raw.length();

                    for (int j = 0; j != style.length; j += 3) {
                        int end = style[j + 2];
                        if (end != -1 && end < start) {
                            if (offset <= end) {
                                html.append(raw, offset, end + 1);
                                offset = end + 1;
                            }

                            style[j + 2] = -1;
                            html.append('<');
                            html.append('/');
                            html.append(this.getString(style[j]));
                            html.append('>');
                        }
                    }

                    if (offset < start) {
                        html.append(raw, offset, start);
                        offset = start;
                    }

                    if (i == -1) {
                        return html.toString();
                    }

                    html.append('<');
                    html.append(this.getString(style[i]));
                    html.append('>');
                    style[i + 1] = -1;
                }
            }
        }
    }

    public int find(String string) {
        if (string == null) {
            return -1;
        } else {
            for (int i = 0; i != this.m_stringOffsets.length; ++i) {
                int offset = this.m_stringOffsets[i];
                int length = getShort(this.m_strings, offset);
                if (length == string.length()) {
                    int j;
                    for (j = 0; j != length; ++j) {
                        offset += 2;
                        if (string.charAt(j) != getShort(this.m_strings, offset)) {
                            break;
                        }
                    }

                    if (j == length) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

    private int[] getStyle(int index) {
        if (this.m_styleOffsets != null && this.m_styles != null && index < this.m_styleOffsets.length) {
            int offset = this.m_styleOffsets[index] / 4;
            int i = 0;

            int j;
            for (j = offset; j < this.m_styles.length && this.m_styles[j] != -1; ++j) {
                ++i;
            }

            if (i != 0 && i % 3 == 0) {
                int[] style = new int[i];
                i = offset;

                for (j = 0; i < this.m_styles.length && this.m_styles[i] != -1; style[j++] = this.m_styles[i++]) {
                    ;
                }

                return style;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
