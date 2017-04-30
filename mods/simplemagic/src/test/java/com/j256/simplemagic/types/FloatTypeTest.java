package com.j256.simplemagic.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

public class FloatTypeTest extends BaseMagicTypeTest {

	@Test
	public void testBigEndian() throws IOException {
		String magic = "0 befloat >86400000000000 match";

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putFloat(Float.parseFloat("87200000000000"));
		bb.flip();
		byte[] bytes = bb.array();
		testOutput(magic, bytes, "match");

		bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putFloat(Float.parseFloat("8.2e+13"));
		bb.flip();
		bytes = bb.array();
		testOutput(magic, bytes, null);
	}

	@Test
	public void testLittleEndian() throws IOException {
		String magic = "0 lefloat >86400000000000 match";

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putFloat(Float.parseFloat("87200000000000"));
		bb.flip();
		byte[] bytes = bb.array();
		testOutput(magic, bytes, "match");

		bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putFloat(Float.parseFloat("82000000000000"));
		bb.flip();
		bytes = bb.array();
		testOutput(magic, bytes, null);
	}
}
