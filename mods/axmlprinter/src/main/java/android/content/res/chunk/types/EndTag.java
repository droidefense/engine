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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Specific chunk for ending sections and/or namespaces
 *
 * @author tstrazzere
 */
public class EndTag extends GenericChunk implements Chunk {

    private int lineNumber;
    private int commentIndex;
    private int namespaceUri;
    private int name;

    public EndTag(ChunkType chunkType, IntReader inputReader) {
        super(chunkType, inputReader);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#readHeader(android.content.res.IntReader)
     */
    @Override
    public void readHeader(IntReader inputReader) throws IOException {
        lineNumber = inputReader.readInt();
        commentIndex = inputReader.readInt();
        namespaceUri = inputReader.readInt();
        name = inputReader.readInt();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toXML(android.content.res.chunk.sections.StringSection,
     * android.content.res.chunk.sections.ResourceSection, int)
     */
    @Override
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
        return indent(indent) + "</" + stringSection.getString(name) + ">";
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toBytes()
     */
    @Override
    public byte[] toBytes() {
        byte[] header = super.toBytes();

        byte[] body = ByteBuffer.allocate(4 * 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(lineNumber)
                .putInt(commentIndex)
                .putInt(namespaceUri)
                .putInt(name)
                .array();

        return ByteBuffer.allocate(header.length + body.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(header)
                .put(body)
                .array();
    }
}
