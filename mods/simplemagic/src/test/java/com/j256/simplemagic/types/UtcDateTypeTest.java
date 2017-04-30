package com.j256.simplemagic.types;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.entries.MagicFormatter;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;

public class UtcDateTypeTest {

	@Test
	public void testBasic() {
		LocalDateType type = new UtcDateType(EndianType.BIG);
		Object testInfo = type.convertTestString(null, ">0");
		int secs = 1367982937;
		Date date = new Date(secs * 1000L);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateStr = format.format(date);
		Object val = type.extractValueFromBytes(0, integerToBytes(secs));
		val = type.isMatch(testInfo, null, false, val, new MutableOffset(0), null);
		StringBuilder sb = new StringBuilder();
		type.renderValue(sb, val, new MagicFormatter("%s"));
		assertEquals(dateStr, sb.toString());
	}

	private byte[] integerToBytes(int val) {
		byte[] bytes = new byte[4];
		for (int i = 0; i < bytes.length; i++) {
			byte b = (byte) (val % 256);
			bytes[bytes.length - 1 - i] = b;
			val /= 256;
		}
		return bytes;
	}
}
