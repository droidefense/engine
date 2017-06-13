package com.j256.simplemagic.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.j256.simplemagic.entries.MagicFormatter;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;

public class RegexTypeTest {

	@Test
	public void testBasic() {
		RegexType type = new RegexType();
		Object test = type.convertTestString("regex", "hello[abc]");
		byte[] bytes = "some line with helloa in it".getBytes();
		Object extracted = type.isMatch(test, null, false, null, new MutableOffset(0), bytes);
		assertNotNull(extracted);
		assertEquals("helloa", renderValue(extracted, type, new MagicFormatter("%s")));
	}

	@Test
	public void testCaseInsensitive() {
		RegexType type = new RegexType();
		Object test = type.convertTestString("regex/c", "hello[ABC]");
		byte[] bytes = "some line with helloa in it".getBytes();
		Object extracted = type.isMatch(test, null, false, null, new MutableOffset(0), bytes);
		assertNotNull(extracted);
		assertEquals("helloa", renderValue(extracted, type, new MagicFormatter("%s")));
	}

	@Test
	public void testExtractValueFromBytes() {
		new RegexType().extractValueFromBytes(0, null, true);
	}

	private String renderValue(Object extracted, RegexType type, MagicFormatter formatter) {
		StringBuilder sb = new StringBuilder();
		type.renderValue(sb, extracted, formatter);
		return sb.toString();
	}
}
