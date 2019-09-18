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
import android.content.res.chunk.AttributeType;
import android.content.res.chunk.ChunkType;
import android.content.res.chunk.sections.ResourceSection;
import android.content.res.chunk.sections.StringSection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Specific type of Chunk which contains the metadata
 *
 * @author tstrazzere
 */
public class Attribute implements Chunk {

    private int uri;
    private int name;
    private int stringData;
    // TODO : Refactor to proper enum
    private int attributeType;
    private int data;

    public Attribute(String uri,
                     String name,
                     String stringData,
                     AttributeType type,
                     Object data,
                     StringSection stringSection) {
        this.uri = stringSection.getStringIndex(uri);
        this.name = stringSection.getStringIndex(name);
        this.stringData = stringSection.getStringIndex(stringData);
        this.attributeType = type.getIntType();

        if (attributeType == AttributeType.STRING.getIntType()) {
            if (this.stringData == -1) {
                this.stringData = stringSection.putStringIndex(stringData);
            }
            this.data = -1;
        } else {
            this.data = (int) data;
        }

    }

    public Attribute(IntReader reader) {
        try {
            uri = reader.readInt();
            name = reader.readInt();
            stringData = reader.readInt();
            attributeType = reader.readInt();
            data = reader.readInt();
        } catch (IOException exception) {
            // TODO : Handle this better
            exception.printStackTrace();
        }
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
        return ChunkType.ATTRIBUTE;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#getSize()
     */
    @Override
    public int getSize() {
        return 4 * 5;
    }

    public int getNameIndex() {
        return name;
    }

    public int getStringDataIndex() {
        return stringData;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toXML(android.content.res.chunk.sections.StringSection,
     * android.content.res.chunk.sections.ResourceSection, int)
     */
    @Override
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
        StringBuffer buffer = new StringBuffer();
        if ((uri - 1) > 0) {
            buffer.append(stringSection.getString(uri - 1));
            buffer.append(":");
        }

        buffer.append(stringSection.getString(name));

        buffer.append("=\"");

        // TODO : This should be a switch...
        if (attributeType == AttributeType.STRING.getIntType()) {
            buffer.append(stringSection.getString(stringData));
        } else if (attributeType == AttributeType.INT.getIntType()) {
            buffer.append(data);
        } else if (attributeType == AttributeType.RESOURCE.getIntType()) {
            buffer.append("@");
            buffer.append(Integer.toHexString(data).toUpperCase());
        } else if (attributeType == AttributeType.BOOLEAN.getIntType()) {
            // TODO : Double check this..
            if (data == -1) {
                buffer.append("true");
            } else if (data == 0) {
                buffer.append("false");
            } else {
                buffer.append("ERROR");
            }
        }

        buffer.append("\"");

        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toBytes()
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(getSize())
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(uri)
                .putInt(name)
                .putInt(stringData)
                .putInt(attributeType)
                .putInt(data)
                .array();
    }
}
