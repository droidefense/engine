package com.j256.simplemagic.types;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
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

	@Test
	public void negativeLength() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// first byte is length and we need to make sure that it is processed as an unsigned
		int length = 255;
		baos.write(new byte[] { (byte)length });
		assertTrue(baos.toByteArray()[0] < 0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			baos.write('a');
			sb.append('a');
		}
		String match = "matched";
		String magic = "0 pstring =" + sb.toString() + " " + match;
		testOutput(magic, baos.toByteArray(), match);
	}
}
