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

import java.io.IOException;

/**
 * This "buffer" chunk is currently being used for empty space, though it might not be needed
 * <p>
 * TODO: Verify this is needed
 * <p>
 * TODO: If kept, should potentially alert/warn if it happens
 *
 * @author tstrazzere
 */
public class Buffer implements Chunk {

    public Buffer(ChunkType chunkType, IntReader inputReader) {

    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#readHeader(android.content.res.IntReader)
     */
    @Override
    public void readHeader(IntReader inputReader) throws IOException {
        // No header to read here
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#getChunkType()
     */
    @Override
    public ChunkType getChunkType() {
        return ChunkType.BUFFER;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#getSize()
     */
    @Override
    public int getSize() {
        return 4;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toXML(android.content.res.chunk.sections.StringSection,
     * android.content.res.chunk.sections.ResourceSection, int)
     */
    @Override
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toBytes()
     */
    @Override
    public byte[] toBytes() {
        return new byte[]{
                0x00, 0x00, 0x00, 0x00
        };
    }
}
