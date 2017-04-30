package com.j256.simplemagic.types;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;

public class LongTypeTest extends BaseMagicTypeTest {

	@Test
	public void testLittleEndianNumber() {
		LongType longType = new LongType(EndianType.LITTLE);
		String hexBytes = "0xc3cbc6c5c7b3a1";
		Object testString = longType.convertTestString("lelong", hexBytes);
		Object value = longType.extractValueFromBytes(0, new byte[] { hexToByte("0xa1"), hexToByte("0xb3"),
				hexToByte("0xc7"), hexToByte("0xc5"), hexToByte("0xc6"), hexToByte("0xcb"), hexToByte("0xc3"), 0 });
		assertNotNull(longType.isMatch(testString, null, false, value, new MutableOffset(0), null /* unused */));
	}

	@Test
	public void testBigEndianNumber() {
		LongType longType = new LongType(EndianType.BIG);
		String hexBytes = "0xc3cbc6c5c7b3a1";
		Object testString = longType.convertTestString("lelong", hexBytes);
		Object value = longType.extractValueFromBytes(0, new byte[] { 0, hexToByte("0xc3"), hexToByte("0xcb"),
				hexToByte("0xc6"), hexToByte("0xc5"), hexToByte("0xc7"), hexToByte("0xb3"), hexToByte("0xa1") });
		assertNotNull(longType.isMatch(testString, null, false, value, new MutableOffset(0), null /* unused */));
	}

	@Test
	public void testEqual() throws IOException {
		String magic = "0 bequad =0 match";
		byte[] bytes = hexToBytes("0000000000000000");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FFFFFFFFFFFFFFFF");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testNotEqual() throws IOException {
		String magic = "0 bequad !0 match";
		byte[] bytes = hexToBytes("FFFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0000000000000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testGreaterThan() throws IOException {
		String magic = "0 bequad >0x7FFFFFFFFFFF0000 match";
		byte[] bytes = hexToBytes("7FFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("8000000000000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testGreaterThanUnsigned() throws IOException {
		String magic = "0 ubequad >0x7FFFFFFFFFFF0000 match";
		byte[] bytes = hexToBytes("7FFFFFFFFFFFFFFF");
		testOutput(magic, bytes, "match");
	}

	private byte hexToByte(String hex) {
		return Integer.decode(hex).byteValue();
	}
}
