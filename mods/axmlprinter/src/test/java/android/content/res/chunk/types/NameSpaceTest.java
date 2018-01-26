/*
 * Copyright 2015 Red Naga
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package android.content.res.chunk.types;

import android.content.res.IntReader;
import android.content.res.chunk.ChunkType;
import junit.framework.TestCase;
import org.junit.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tstrazzere
 */
public class NameSpaceTest extends TestCase {

    private NameSpace underTest;

    private IntReader mockReader;
    private ChunkType mockChunkType;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the namespace data
        when(mockReader.readInt()).thenReturn(0x18, 0x19, 0xFFFFFFFF, 0x5C, 0x12);

        mockChunkType = mock(ChunkType.class);
        when(mockChunkType.getIntType()).thenReturn(ChunkType.START_NAMESPACE.getIntType());

        underTest = new NameSpace(mockChunkType, mockReader);
    }

    public void testToBytes() throws Exception {
        byte[] expected = {
                // END_TAG
                (byte) 0x00, (byte) 0x01, (byte) 0x10, (byte) 0x00,
                // size
                (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // line number
                (byte) 0x19, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // unknown
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                // prefix
                (byte) 0x5C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // uri
                (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        };

        byte[] actual = underTest.toBytes();
        Assert.assertArrayEquals(expected, actual);
    }
}