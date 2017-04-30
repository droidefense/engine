package com.j256.simplemagic.types;

import java.io.IOException;

import org.junit.Test;

public class PStringTypeTest extends BaseMagicTypeTest {

	@Test
	public void testEqual() throws IOException {
		String magic = "0 pstring =wow match";
		byte[] bytes = byteArraysCombine(hexToBytes("03"), "wow".getBytes());
		testOutput(magic, bytes, "match");

		bytes = byteArraysCombine(hexToBytes("02"), "wow".getBytes());
		testOutput(magic, bytes, null);
	}
}
