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
import android.content.res.chunk.PoolItem;
import android.content.res.chunk.types.Chunk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class StringSection extends GenericChunkSection implements Chunk, ChunkSection {

    // This specific tag appears unused but might need to be implemented? or used as an unknown?
    @SuppressWarnings("unused")
    private final int SORTED_FLAG = 1 << 0;
    private final int UTF8_FLAG = 1 << 8;

    private int stringChunkCount;
    private int styleChunkCount;
    private int stringChunkFlags;
    private int stringChunkPoolOffset;
    private int styleChunkPoolOffset;

    // FIXME:
    // This likely could just be an ordered array of Strings if the Integer is just ordered and the key..
    private ArrayList<PoolItem> stringChunkPool;
    private ArrayList<PoolItem> styleChunkPool;

    public StringSection(ChunkType chunkType, IntReader inputReader) {
        super(chunkType, inputReader);
    }

    @Override
    public void readHeader(IntReader inputReader) throws IOException {
        stringChunkCount = inputReader.readInt();
        styleChunkCount = inputReader.readInt();
        stringChunkFlags = inputReader.readInt();

        stringChunkPoolOffset = inputReader.readInt();
        stringChunkPool = new ArrayList<PoolItem>();

        styleChunkPoolOffset = inputReader.readInt();
        styleChunkPool = new ArrayList<PoolItem>();
    }

    @Override
    public void readSection(IntReader inputReader) throws IOException {
        for (int i = 0; i < stringChunkCount; i++) {
            stringChunkPool.add(new PoolItem(inputReader.readInt(), null));
        }

        if (!stringChunkPool.isEmpty()) {
            readPool(stringChunkPool, stringChunkFlags, inputReader);
        }

        // TODO : Does this need the flags?
        // FIXME: This is potentially wrong
        for (int i = 0; i < styleChunkCount; i++) {
            styleChunkPool.add(new PoolItem(inputReader.readInt(), null));
        }

        if (!styleChunkPool.isEmpty()) {
            readPool(styleChunkPool, stringChunkFlags, inputReader);
        }
    }

    // TODO : Ensure we goto the proper offset in the case it isn't in proper order
    private void readPool(ArrayList<PoolItem> pool, int flags, IntReader inputReader) throws IOException {
        int offset = 0;
        for (PoolItem item : pool) {
            // TODO: This assumes that the pool is ordered...
            inputReader.skip(item.getOffset() - offset);
            offset = item.getOffset();

            int length = 0;
            if ((flags & UTF8_FLAG) != 0) {
                length = inputReader.readByte();
                offset += 1;
            } else {
                length = inputReader.readShort();
                offset += 2;
            }

            StringBuilder result = new StringBuilder(length);
            for (; length != 0; length -= 1) {
                if ((flags & UTF8_FLAG) != 0) {
                    result.append((char) inputReader.readByte());
                    offset += 1;
                } else {
                    result.append((char) inputReader.readShort());
                    offset += 2;
                }
            }

            item.setString(result.toString());
        }
    }

    public int getStringIndex(String string) {
        if (string != null) {
            for (PoolItem item : stringChunkPool) {
                if (item.getString().equals(string)) {
                    return stringChunkPool.indexOf(item);
                }
            }
        }

        return -1;
    }

    public int putStringIndex(String string) {
        int currentPosition = getStringIndex(string);
        if (currentPosition != -1) {
            return currentPosition;
        }

        stringChunkPool.add(new PoolItem(-1, string));

        return getStringIndex(string);
    }

    public String getString(int index) {
        if ((index > -1) && (index < stringChunkPool.size())) {
            return stringChunkPool.get(index).getString();
        }

        return "";
    }

    public String getStyle(int index) {
        return styleChunkPool.get(index).getString();
    }

    @Override
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent) {
        return null;
    }

    @Override
    public int getSize() {
        int stringDataSize = 0;
        int previousSize;
        for (PoolItem item : stringChunkPool) {
            previousSize = stringDataSize;
            // TODO: This is potentially wrong
            // length identifier
            stringDataSize += ((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1;
            // actual string data
            stringDataSize += item.getString().length() * (((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1);
            // buffer
            int bufferSize = 4 - (stringDataSize - previousSize) % 4;
            if (bufferSize > 0 && bufferSize < 4) {
                stringDataSize += bufferSize;
            }
        }

        int styleDataSize = 0;
        for (PoolItem item : styleChunkPool) {
            styleDataSize += item.getString().length() * (((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1);
        }

        return (2 * 4) + // Header
                (5 * 4) + // static sections
                (stringChunkPool.size() * 4) + // string table offset size
                stringDataSize +
                (styleChunkPool.size() * 4) + // style table offset size
                styleDataSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.res.chunk.types.Chunk#toBytes()
     */
    @Override
    public byte[] toBytes() {
        byte[] header = super.toBytes();

        // TODO : We need to ensure these are already "sorted"
        ByteBuffer offsetBuffer = ByteBuffer.allocate(stringChunkPool.size() * 4)
                .order(ByteOrder.LITTLE_ENDIAN);
        int offset = 0;
        int previousOffset;
        ArrayList<byte[]> stringData = new ArrayList<>();
        for (PoolItem item : stringChunkPool) {
            offsetBuffer.putInt(offset);
            previousOffset = offset;

            // TODO : Ensure this is properly handled, potentially a ULEB128?
            // Add string length bytes
            if (item.getString().length() > 255) {
                System.err.println("Error, string length is greater than the current expected lengths!");
            }
            offset += ((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1;

            // Add length of string based on if UTF-8 flag is enabled
            offset += item.getString().length() * (((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1);

            // Add buffer
            int bufferSize = 4 - ((offset - previousOffset) % 4);
            if (bufferSize > 0 && bufferSize < 4) {
                offset += bufferSize;
            }

            // Append actual length + data
            ByteBuffer length;
            if ((stringChunkFlags & UTF8_FLAG) == 0) {
                length = ByteBuffer.allocate(2)
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .putShort((short) item.getString().length());
            } else {
                length = ByteBuffer.allocate(1)
                        .put((byte) item.getString().length());
            }

            ByteBuffer string = ByteBuffer.allocate(item.getString().length() * (((stringChunkFlags & UTF8_FLAG) == 0) ? 2 : 1))
                    .order(ByteOrder.LITTLE_ENDIAN);
            for (byte character : item.getString().getBytes()) {
                if ((stringChunkFlags & UTF8_FLAG) == 0) {
                    string.putShort(character);
                } else {
                    string.put(character);
                }
            }

            ByteBuffer stringDataBuffer = ByteBuffer.allocate(offset - previousOffset)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .put(length.array())
                    .put(string.array());

            if (bufferSize > 0 && bufferSize < 4) {
                // TODO : fix this
                byte[] buffer = new byte[bufferSize];
                Arrays.fill(buffer, (byte) 0x00);
                stringDataBuffer.put(buffer);
            }

            stringData.add(stringDataBuffer.array());
        }

        // Combine strings into one buffer
        ByteBuffer stringsBuffer = ByteBuffer.allocate(offsetBuffer.capacity() + offset)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(offsetBuffer.array());
        for (byte[] data : stringData) {
            stringsBuffer.put(data);
        }
        byte[] strings = stringsBuffer.array();

//        byte[] styles = new byte[]{0x00};

        int newStringChunkOffset = 0;
        if (!stringChunkPool.isEmpty()) {
            newStringChunkOffset = (5 * 4) /* header + 3 other ints above it */
                    + stringChunkPool.size() * 4 /* index table size */
                    + 8 /* (this space and the style chunk offset */;
        }

        int newStyleChunkOffset = 0;
        if (!styleChunkPool.isEmpty()) {
            newStyleChunkOffset = (6 * 4) /* header + 4 other ints above it */
                    + styleChunkPool.size() * 4 /* index table size */
                    + 8 /* (this space and the style chunk offset */;
        }

        byte[] body = ByteBuffer.allocate(5 * 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt(stringChunkPool.size())
                .putInt(styleChunkPool.size())
                .putInt(stringChunkFlags)
                .putInt(newStringChunkOffset)
                .putInt(newStyleChunkOffset)
                .array();

        return ByteBuffer.allocate(header.length + body.length + strings.length /*+ styles.length*/)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(header)
                .put(body)
                .put(strings)
//                .put(styles)
                .array();
    }
}
