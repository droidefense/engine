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
 * Namespace Chunk - used for denoting the borders of the XML boundries
 *
 * @author tstrazzere
 */
public class NameSpace extends GenericChunk implements Chunk {

    private int lineNumber;
    private int commentIndex;
    private int prefix;
    private int uri;

    public NameSpace(ChunkType chunkType, IntReader inputReader) {
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
        prefix = inputReader.readInt();
        uri = inputReader.readInt();
    }

    /**
     * @return if the Namespace Chunk is either a START_NAMESPACE or END_NAMESPACE
     */
    public boolean isStart() {
        return (getChunkType() == ChunkType.START_NAMESPACE) ? true : false;
    }

    public int getUri() {
        return uri;
    }

    public int getPrefix() {
        return prefix;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String toString(StringSection stringSection) {
        return "xmlns" + ":" + stringSection.getString(getPrefix()) + "=\"" + stringSection.getString(getUri()) + "\"";
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toXML(android.content.res.chunk.sections.StringSection,
     * android.content.res.chunk.sections.ResourceSection, int)
     */
    @Override
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
        if (isStart()) {
            return indent(indent) + toString(stringSection);
        } else {
            return "";
        }
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
                .putInt(prefix)
                .putInt(uri)
                .array();

        return ByteBuffer.allocate(header.length + body.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(header)
                .put(body)
                .array();
    }
}
