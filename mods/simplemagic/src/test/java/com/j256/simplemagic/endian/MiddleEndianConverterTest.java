package com.j256.simplemagic.endian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class MiddleEndianConverterTest {

	@Test
	public void testBasic() {
		EndianConverter converter = EndianType.MIDDLE.getConverter();
		byte[] bytes = new byte[] { 1, 2, 3, 4 };
		long val = converter.convertNumber(0, bytes, 4);
		// BADC: 2*2^24 + 1*2^16 + 4*2^8 + 3
		assertEquals(33620995, val);
		byte[] outBytes = converter.convertToByteArray(val, 4);
		assertTrue(Arrays.equals(bytes, outBytes));
	}

	@Test
	public void testId3() {
		EndianConverter converter = EndianType.MIDDLE.getConverter();
		long val = converter.convertId3(0, new byte[] { 1, 2, 3, 4 }, 4);
		// BADC: 2*2^21 + 1*2^14 + 4*2^7 + 3
		assertEquals(4211203, val);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testInvalidLength() {
		EndianType.MIDDLE.getConverter().convertNumber(0, new byte[0], 2);
	}

	@Test
	public void testOutOfBytes() {
		assertNull(EndianType.MIDDLE.getConverter().convertNumber(0, new byte[0], 4));
	}
}
