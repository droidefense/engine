package com.j256.simplemagic.types;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.entries.MagicMatcher.MutableOffset;

public class IntegerTypeTest extends BaseMagicTypeTest {

	@Test
	public void testLittleEndianNumber() {
		IntegerType longType = new IntegerType(EndianType.LITTLE);
		String hexBytes = "0x03cbc6c5";
		Object testString = longType.convertTestString("lelong", hexBytes);
		Object value = longType.extractValueFromBytes(0, hexToBytes("c5c6cb03"), true);
		assertNotNull(longType.isMatch(testString, null, false, value, new MutableOffset(0), null /* unused */));
	}

	@Test
	public void testBigEndianNumber() {
		IntegerType longType = new IntegerType(EndianType.BIG);
		String hexBytes = "0x03c7b3a1";
		Object testString = longType.convertTestString("lelong", hexBytes);
		Object value = longType.extractValueFromBytes(0, hexToBytes("03c7b3a1"), true);
		assertNotNull(longType.isMatch(testString, null, false, value, new MutableOffset(0), null /* unused */));
	}

	@Test
	public void testUnsignedGreaterThan() throws IOException {
		String magic = "0 ubelong >0xF0000000 match";
		byte[] bytes = hexToBytes("FFFFFFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("F0000001");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("EFFFFFFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("AAAAAAAA");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("80000000");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("00000000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testSignedGreaterThan1() throws IOException {
		String magic = "0 belong >0xF0000000 match";

		byte[] bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, "match");
	}
	
	@Test
	public void testSignedGreaterThan() throws IOException {
		String magic = "0 belong >0xF0000000 match";

		// higher always
		byte[] bytes = hexToBytes("F0000001");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("FFFFFFFF");
		testOutput(magic, bytes, "match");

		// Higher bc of two's complement
		bytes = hexToBytes("00000000");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("7FFFFFFF");
		testOutput(magic, bytes, "match");

		// lower always
		bytes = hexToBytes("EFFFFFFF");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("AAAAAAAA");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("80000000");
		testOutput(magic, bytes, null);
	}
}
