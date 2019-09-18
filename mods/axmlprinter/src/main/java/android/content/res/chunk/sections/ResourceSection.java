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
import android.content.res.chunk.types.Chunk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Concrete class for the section which is specifically for the resource ids.
 *
 * @author tstrazzere
 */
public class ResourceSection extends GenericChunkSection implements Chunk, ChunkSection {

    // TODO : Make this an ArrayList so it's easier to add/remove
    protected ArrayList<Integer> resourceIDs;

    public ResourceSection(ChunkType chunkType, IntReader reader) {
        super(chunkType, reader);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#readHeader(android.content.res.IntReader)
     */
    @Override
    public void readHeader(IntReader inputReader) throws IOException {
        // Initialize this variable here
        resourceIDs = new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.sections.ChunkSection#readSection(android.content.res.IntReader)
     */
    @Override
    public void readSection(IntReader inputReader) throws IOException {
        for (int i = 0; i < ((size / 4) - 2); i++) {
            addResource(inputReader.readInt());
        }
    }

    public void addResource(int value) {
        resourceIDs.add(value);
    }

    @Override
    public int getSize() {
        // Tag + Size + resourceIds
        return 4 + 4 + (resourceIDs.size() * 4);
    }

    public int getResourceID(int index) {
        return resourceIDs.get(index);
    }

    public int getResourceCount() {
        return resourceIDs.size();
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
        byte[] header = super.toBytes();

        ByteBuffer offsetBuffer = ByteBuffer.allocate(resourceIDs.size() * 4).order(ByteOrder.LITTLE_ENDIAN);

        for (int id : resourceIDs) {
            offsetBuffer.putInt(id);
        }
        byte[] body = offsetBuffer.array();

        return ByteBuffer.allocate(header.length + body.length)
                .put(header)
                .put(body)
                .array();
    }
}
