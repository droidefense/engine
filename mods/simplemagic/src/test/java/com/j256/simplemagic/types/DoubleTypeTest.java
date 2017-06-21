package com.j256.simplemagic.types;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DoubleTypeTest extends BaseMagicTypeTest {

    @Test
    public void testBigEndian() throws IOException {
        String magic = "0 bedouble >86400000000000 match";

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putDouble(Double.parseDouble("87200000000000"));
        bb.flip();
        byte[] bytes = bb.array();
        testOutput(magic, bytes, "match");

        bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putDouble(Double.parseDouble("8.2e+13"));
        bb.flip();
        bytes = bb.array();
        testOutput(magic, bytes, null);
    }

    @Test
    public void testLittleEndian() throws IOException {
        String magic = "0 ledouble >86400000000000 match";

        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putDouble(Double.parseDouble("87200000000000"));
        bb.flip();
        byte[] bytes = bb.array();
        testOutput(magic, bytes, "match");

        bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putDouble(Double.parseDouble("82000000000000"));
        bb.flip();
        bytes = bb.array();
        testOutput(magic, bytes, null);
    }
}
