package com.j256.simplemagic.types;

import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SearchTypeTest {

    @Test
    public void testBasicMatch() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/5", str);
        byte[] bytes = new byte[]{'h', 'e', 'l', 'l', 'o', '2'};
        assertEquals(str, type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
        bytes = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testHitMaxOffset() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/4", str);
        byte[] bytes = new byte[]{'1', 'h', 'e', 'l', 'l', 'o', '2'};
        assertEquals(str, type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
        bytes = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testSubLineMatch() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/7", str);
        byte[] bytes = new byte[]{'1', '2', 'h', 'e', 'l', 'l', 'o', '2', '4'};
        assertEquals(str, type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
        bytes = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
    }

    @Test
    public void testSubLineOffsetInfoMatch() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/7", str);
        byte[] bytes = new byte[]{'1', '2', 'h', 'e', 'l', 'l', 'o', '2', '4'};
        assertEquals(str, type.isMatch(info, null, false, null, new MutableOffset(2), bytes));
        bytes = new byte[]{' ', 'e', 'l', 'l', 'o', '2'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(2), bytes));
    }

    @Test
    public void testNoMatch() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/10", str);
        byte[] bytes = new byte[]{'1', '2', 'h', 'e', 'l', 'l', '2', '4'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(0), bytes));
        // no match after offset
        info = type.convertTestString("search/10", str);
        bytes = new byte[]{'1', '2', 'h', 'e', '\n', 'l', 'l', '2', '4'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(1), bytes));
        // EOF before offset reached
        info = type.convertTestString("search/10", str);
        bytes = new byte[]{'1', '2', 'h', 'e', '\n', 'l', 'l', '2', '4'};
        assertNull(type.isMatch(info, null, false, null, new MutableOffset(10), bytes));
    }

    @Test
    public void testCoverage() {
        new SearchType().extractValueFromBytes(0, null, true);
    }

    @Test
    public void testOptionalWhitespace() {
        SearchType type = new SearchType();
        String str = "hello";
        Object info = type.convertTestString("search/10/b", str);
        byte[] bytes = new byte[]{'1', '2', 'h', 'e', 'l', ' ', 'l', ' ', 'o', ' ', '2', '4'};
        // match on the line started at offset 1
        info = type.convertTestString("search/9/b", str);
        assertEquals("hel l o", type.isMatch(info, null, false, null, new MutableOffset(1), bytes));
    }
}
