package com.j256.simplemagic.types;

import java.io.IOException;

import org.junit.Test;

public class ShortTypeTest extends BaseMagicTypeTest {

	@Test
	public void testAnd() throws IOException {
		String magic = "0 beshort &0xFF match";
		byte[] bytes = hexToBytes("FFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FFF4");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testXorShort() throws IOException {
		String magic = "0 beshort ^0x24 match";
		byte[] bytes = hexToBytes("FFCA");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FFCE");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testNegate() throws IOException {
		String magic = "0 beshort ~3 match";
		byte[] bytes = hexToBytes("FFFC");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FFFE");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testMask() throws IOException {
		String magic = "0 beshort&0xF >7 match";
		byte[] bytes = hexToBytes("FFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("000F");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0009");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0008");
		testOutput(magic, bytes, "match");

		// fail bc not gt 7
		bytes = hexToBytes("0007");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("FFF3");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("FFF0");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testUnsignedGreaterThan() throws IOException {
		String magic = "0 ubeshort >0xF000 match";
		byte[] bytes = hexToBytes("FFFF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("F001");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("EFFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("AAAA");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("8000");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("7FFF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("0000");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testSignedGreaterThan() throws IOException {
		String magic = "0 beshort >0xF000 match";

		// higher always
		byte[] bytes = hexToBytes("F001");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("FFFF");
		testOutput(magic, bytes, "match");

		// Higher bc of two's complement
		bytes = hexToBytes("0000");
		testOutput(magic, bytes, "match");
		bytes = hexToBytes("7FFF");
		testOutput(magic, bytes, "match");

		// lower always
		bytes = hexToBytes("EFFF");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("AAAA");
		testOutput(magic, bytes, null);
		bytes = hexToBytes("8000");
		testOutput(magic, bytes, null);
	}
}
