package com.j256.simplemagic.types;

import java.io.IOException;

import org.junit.Test;

public class ByteTypeTest extends BaseMagicTypeTest {

	@Test
	public void testEqual() throws IOException {
		String magic = "0 byte =0 match";
		byte[] bytes = hexToBytes("00");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("01");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testNotEqual() throws IOException {
		String magic = "0 byte !0 match";
		byte[] bytes = hexToBytes("0106");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0006");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testGreaterThan() throws IOException {
		String magic = "0 byte >5 match";
		byte[] bytes = hexToBytes("0606");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0306");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testLessThan() throws IOException {
		String magic = "0 byte <5 match";
		byte[] bytes = hexToBytes("0206");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0806");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testAnd() throws IOException {
		String magic = "0 byte &3 match";
		byte[] bytes = hexToBytes("0706");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("0806");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testXor() throws IOException {
		String magic = "0 byte ^0x24 match";
		byte[] bytes = hexToBytes("CA");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("CE");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testNegate() throws IOException {
		String magic = "0 byte ~3 match";
		byte[] bytes = hexToBytes("FC");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("FF");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testUnsignedGreaterThan() throws IOException {
		String magic = "0 ubyte >0xF0 match";
		byte[] bytes = hexToBytes("FF");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("F1");
		testOutput(magic, bytes, "match");

		bytes = hexToBytes("EF");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("AA");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("80");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("7F");
		testOutput(magic, bytes, null);

		bytes = hexToBytes("00");
		testOutput(magic, bytes, null);
	}

	@Test
	public void testSignedGreaterThan() throws IOException {
		String magicFile = "0 byte >0xF0 match";

		// higher always
		byte[] bytes = hexToBytes("F1");
		testOutput(magicFile, bytes, "match");
		bytes = hexToBytes("FF");
		testOutput(magicFile, bytes, "match");

		// Higher bc of two's complement
		bytes = hexToBytes("00");
		testOutput(magicFile, bytes, "match");
		bytes = hexToBytes("7F");
		testOutput(magicFile, bytes, "match");

		// lower always
		bytes = hexToBytes("EF");
		testOutput(magicFile, bytes, null);
		bytes = hexToBytes("AA");
		testOutput(magicFile, bytes, null);
		bytes = hexToBytes("80");
		testOutput(magicFile, bytes, null);
	}
}
