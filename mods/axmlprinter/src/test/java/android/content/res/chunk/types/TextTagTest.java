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
public class TextTagTest extends TestCase {

    private TextTag underTest;

    private IntReader mockReader;
    private ChunkType mockChunkType;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the text tag data
        when(mockReader.readInt()).thenReturn(7 * 4, 0x17, 0xFFFFFFFF, 0x1C, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);

        mockChunkType = mock(ChunkType.class);
        when(mockChunkType.getIntType()).thenReturn(ChunkType.TEXT_TAG.getIntType());

        underTest = new TextTag(mockChunkType, mockReader);
    }

    public void testToBytes() throws Exception {
        byte[] expected = {
                // TEXT_TAG
                (byte) 0x04, (byte) 0x01, (byte) 0x10, (byte) 0x00,
                // size
                (byte) (7 * 4), (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // line number
                (byte) 0x17, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // unknown
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                // name
                (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // unknown2
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                // unknown3
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };

        byte[] actual = underTest.toBytes();
        Assert.assertArrayEquals(expected, actual);
    }
}