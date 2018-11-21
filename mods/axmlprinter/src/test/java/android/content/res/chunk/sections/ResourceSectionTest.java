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
package android.content.res.chunk.sections;

import android.content.res.IntReader;
import android.content.res.chunk.ChunkType;
import android.content.res.chunk.types.GenericChunk;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author tstrazzere
 */
public class ResourceSectionTest extends TestCase {
    // Implement a new type of Generic chunk just to test specific functionality
    protected class TestResourceSection extends ResourceSection {
        public TestResourceSection(ChunkType chunkType, IntReader inputReader) {
            super(chunkType, inputReader);
        }

        @Override
        public void readSection(IntReader inputReader) throws IOException {
            addResource(0);
            addResource(1);
            addResource(2);
            addResource(3);
            addResource(4);
        }
    }

    private GenericChunk underTest;

    private IntReader mockReader;
    private ChunkType mockChunkType;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the size read -- however this should /not/ be the output!
        // We expect that the section truncates the to proper length since it's assumed to have been
        // modified since the size differs
        when(mockReader.readInt()).thenReturn(0xBB60);

        mockChunkType = mock(ChunkType.class);

        underTest = new TestResourceSection(mockChunkType, mockReader);
    }

    public void testToBytes() throws Exception {
        // (chunk type) RESOURCE_SECTION + (size) 0xBB60 + (int[]) resourceIDs
        byte[] expected = {
                // RESOURCE_SECTION
                (byte) 0x80, (byte) 0x01, (byte) 0x08, (byte) 0x00,
                // (size) 4 (tag) + 4 (size) + 5 * 4 (resource ids)
                (byte) 0x1C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // (int) 0
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // (int) 1
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // (int) 2
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // (int) 3
                (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // (int) 4
                (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        when(mockChunkType.getIntType()).thenReturn(ChunkType.RESOURCE_SECTION.getIntType());

        byte[] actual = underTest.toBytes();

        Assert.assertArrayEquals(expected, actual);
    }
}