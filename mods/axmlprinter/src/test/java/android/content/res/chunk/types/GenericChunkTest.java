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
import android.content.res.chunk.sections.ResourceSection;
import android.content.res.chunk.sections.StringSection;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tstrazzere
 */
public class GenericChunkTest extends TestCase {

    // Implement a new type of Generic chunk just to test specific functionality
    protected class TestChunk extends GenericChunk {

        public TestChunk(ChunkType chunkType, IntReader inputReader) {
            super(chunkType, inputReader);
        }

        @Override
        public void readHeader(IntReader reader) throws IOException {

        }

        @Override
        public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
            return null;
        }

        @Override
        public byte[] toBytes() {
            byte[] chunks = super.toBytes();
            return ByteBuffer.allocate(chunks.length + 1).put(chunks).put((byte) 0xFF).array();
        }
    }

    private GenericChunk underTest;

    private IntReader mockReader;
    private ChunkType mockChunkType;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the size read
        when(mockReader.readInt()).thenReturn(0xBB60);

        mockChunkType = mock(ChunkType.class);
        when(mockChunkType.getIntType()).thenReturn(ChunkType.AXML_HEADER.getIntType());

        underTest = new TestChunk(mockChunkType, mockReader);
    }

    public void testToBytes() throws Exception {
        // (chunk type) AXML_HEADER + (size) 0xBB60 + (TestChunk.toBytes() addition) 0xFF
        byte[] expected = {
                // AXML_HEADER
                (byte) 0x03, (byte) 0x00, (byte) 0x08, (byte) 0x00,
                // (size) 0xBB60
                (byte) 0x60, (byte) 0xBB, (byte) 0x00, (byte) 0x00,
                // (TestChunk.toBytes() addition) 0xFF
                (byte) 0xFF
        };

        byte[] actual = underTest.toBytes();

        Assert.assertArrayEquals(expected, actual);
    }
}