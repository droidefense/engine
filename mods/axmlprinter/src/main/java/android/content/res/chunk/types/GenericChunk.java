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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Abstract class for the generic lifting required by all Chunks
 *
 * @author tstrazzere
 */
public abstract class GenericChunk implements Chunk {

    protected int size;
    private int startPosition;
    private ChunkType type;

    public GenericChunk(ChunkType chunkType, IntReader reader) {
        startPosition = reader.getBytesRead() - 4;
        type = chunkType;
        try {
            size = reader.readInt();
            readHeader(reader);
        } catch (IOException exception) {
            // TODO : Handle this better
            exception.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#getChunkType()
     */
    public ChunkType getChunkType() {
        return type;
    }

    /*
     *` (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#getSize()
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the int position inside of the file where the Chunk starts
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * @param indents
     * @return a number of indents needed for properly formatting XML
     */
    protected String indent(int indents) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < indents; i++) {
            buffer.append("\t");
        }
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toBytes()
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(type.getIntType())
                .putInt(getSize()).array();
    }
}
