package com.j256.simplemagic.types;

import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BigEndianString16TypeTest {

    @Test
    public void testStuff() {
        BigEndianString16Type type = new BigEndianString16Type();
        byte[] bytes = new byte[]{1, 'a', 2, 'b'};
        char[] value = (char[]) type.extractValueFromBytes(0, bytes, true);
        assertEquals("šɢ", new String(value));
    }

    @Test
    public void testMatch() {
        BigEndianString16Type type = new BigEndianString16Type();
        byte[] bytes = new byte[]{1, 'a', 2, 'b'};
        char[] value = (char[]) type.extractValueFromBytes(0, bytes, true);
        Object info = type.convertTestString("", "šɢ");
        assertNotNull(type.isMatch(info, null, false, value, new MutableOffset(0), bytes));
    }
}
