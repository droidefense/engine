package com.j256.simplemagic.types;

import com.j256.simplemagic.ContentInfoUtil;
import com.j256.simplemagic.entries.MagicFormatter;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class StringTypeTest {

    @Test
    public void testBasicMatch() {
        StringType type = new StringType();
        Object info = type.convertTestString("string", "hello");
        byte[] bytes = new byte[]{'h', 'e', 'l', 'l', 'o', '2'};
        Object extract = type.isMatch(info, null, false, null, new MutableOffset(0), bytes);
        assertNotNull(extract);
        bytes = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testBasicNoMatch() {
        StringType type = new StringType();
        Object info = type.convertTestString("string", "hello");
        byte[] bytes = new byte[]{'h', 'e', 'l', 'l'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', 'e', 'l', 'l', 'p'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testOffset() {
        StringType type = new StringType();
        Object info = type.convertTestString("string", "hello");
        byte[] bytes = new byte[]{'w', 'o', 'w', 'h', 'e', 'l', 'l', 'o', '2', '3'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(3), bytes));
    }

    @Test
    public void testCaseInsensitive() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/c", "hello");
        byte[] bytes = new byte[]{'h', 'e', 'l', 'l', 'o'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', 'E', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));

        info = type.convertTestString("string/c", "Hello");
        bytes = new byte[]{'H', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'H', 'E', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testOptionalWhitespace() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/b", "hello");
        byte[] bytes = new byte[]{'h', ' ', 'e', ' ', 'l', ' ', 'l', ' ', 'o'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'n', 'e', 'l', 'l', 'o'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testCompactWhitespace() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/B", "h ello");
        byte[] bytes = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', 'e', ' ', 'l', ' ', 'l', ' ', 'o'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        info = type.convertTestString("string/B", "h e llo");
        bytes = new byte[]{'h', ' ', ' ', 'e', ' ', ' ', ' ', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', ' ', 'b', ' ', ' ', ' ', 'l', 'l', 'o'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testCompactWhitespacePlusCaseInsensitive() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/Bc", "h ello");
        byte[] bytes = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'H', ' ', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'H', ' ', ' ', 'E', 'L', 'L', 'O'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testCompactPlusOptionalWhitespace() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/Bb", "h ello");
        byte[] bytes = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
        Object extract = type.extractValueFromBytes(0, bytes, true);
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', ' ', 'e', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', 'e', ' ', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', 'e', ' ', 'l', 'l', 'o'};
        assertNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
        bytes = new byte[]{'h', ' ', ' ', ' ', 'e', ' ', 'l', 'l', 'o'};
        assertNotNull(type.isMatch(info, null, false, extract, new MutableOffset(0), bytes));
    }

    @Test
    public void testRenderValue() {
        StringType type = new StringType();
        Object info = type.convertTestString("string/Bb", "h ello");
        byte[] bytes = new byte[]{'h', ' ', 'e', 'l', 'l', 'o'};
        Object extract = type.isMatch(info, null, false, null, new MutableOffset(0), bytes);
        assertNotNull(extract);
        StringBuilder sb = new StringBuilder();
        type.renderValue(sb, extract, new MagicFormatter("%s"));
        assertEquals("h ello", sb.toString());

        bytes = new byte[]{'h', ' ', ' ', ' ', 'e', 'l', 'l', 'o'};
        extract = type.isMatch(info, null, false, null, new MutableOffset(0), bytes);
        assertNotNull(extract);
        sb.setLength(0);
        type.renderValue(sb, extract, new MagicFormatter("%s"));
        assertEquals("h   ello", sb.toString());
    }

    @Test
    public void testEquals() {
        StringType type = new StringType();
        Object info = type.convertTestString("string", "=foo");
        byte[] bytes = new byte[]{'f', 'o', 'o'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", "=f");
        bytes = new byte[]{'f'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", "=f");
        bytes = new byte[]{'F'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string/c", "=f");
        bytes = new byte[]{'F'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testGreaterThan() {
        StringType type = new StringType();
        Object info = type.convertTestString("string", ">\0");
        // really any string
        byte[] bytes = new byte[]{'\001'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", ">foo");
        bytes = new byte[]{'f', 'o', 'o', 'l',};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", ">foo\0");
        bytes = new byte[]{'f', 'o', 'o', 'l',};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", ">f");
        bytes = new byte[]{'g'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", ">f");
        bytes = new byte[]{'f'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testLessThan() {

        StringType type = new StringType();
        Object info = type.convertTestString("string", "<fop");
        byte[] bytes = new byte[]{'f', 'o', 'o'};
        assertNotNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));

        type = new StringType();
        info = type.convertTestString("string", "<f");
        bytes = new byte[]{'f'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testGetStartingBytesInvalid() throws IOException {
        StringReader reader = new StringReader("0  string/b  x\n");
        // getting the starting bytes failed in this example
        new ContentInfoUtil(reader);
    }

}
