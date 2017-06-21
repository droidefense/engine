package com.j256.simplemagic.types;

import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LittleEndianString16TypeTest {

    @Test
    public void testStuff() {
        LittleEndianString16Type type = new LittleEndianString16Type();
        byte[] bytes = new byte[]{1, 'a', 2, 'b'};
        char[] value = (char[]) type.extractValueFromBytes(0, bytes, true);
        System.out.println((int) value[0]);
        assertEquals("愁戂", new String(value));
    }

    @Test
    public void testMatch() {
        LittleEndianString16Type type = new LittleEndianString16Type();
        byte[] bytes = new byte[]{1, 'a', 2, 'b'};
        char[] value = (char[]) type.extractValueFromBytes(0, bytes, true);
        Object info = type.convertTestString("", "愁戂");
        assertNotNull(type.isMatch(info, null, false, value, new MutableOffset(0), bytes));
    }
}
