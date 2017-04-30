package com.j256.simplemagic.endian;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class BigEndianConverterTest {

	@Test
	public void testStuff() {
		BigEndianConverter converter = new BigEndianConverter();
		byte[] bytes = new byte[] { 10, 127, -100, 0, -128, 1, 62, -62 };
		Long result = converter.convertNumber(0, bytes, 8);
		byte[] outBytes = converter.convertToByteArray(result, 8);
		assertTrue(Arrays.equals(bytes, outBytes));
	}
}
