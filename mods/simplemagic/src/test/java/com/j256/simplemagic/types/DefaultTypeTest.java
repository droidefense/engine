package com.j256.simplemagic.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.j256.simplemagic.entries.MagicFormatter;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;

public class DefaultTypeTest {

	@Test
	public void testConverage() {
		DefaultType type = new DefaultType();
		type.convertTestString(null, null);
		type.extractValueFromBytes(0, null, true);
		assertTrue(type.isMatch(null, null, false, null, new MutableOffset(0), null) != null);
		String str = "weofjwepfj";
		StringBuilder sb = new StringBuilder();
		type.renderValue(sb, null, new MagicFormatter(str));
		assertEquals(str, sb.toString());
	}
}
