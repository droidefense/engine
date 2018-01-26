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
 * Generic interface for everything that is at minimum a "chunk"
 *
 * @author tstrazzere
 */
public interface Chunk {

    /**
     * Read the header section of the chunk
     *
     * @param reader
     * @throws IOException
     */
    public void readHeader(IntReader reader) throws IOException;

    /**
     * @return the ChunkType for the current Chunk
     */
    public ChunkType getChunkType();

    /**
     * @return the int size of the ChunkType
     */
    public int getSize();

    // XXX: Not sure this needs to exist

    /**
     * @return a String representation of the Chunk
     */
    public String toString();

    /**
     * @param stringSection
     * @param resourceSection
     * @param indent
     * @return a String representation in XML form
     */
    public String toXML(StringSection stringSection, ResourceSection resourceSection, int indent);

    /**
     * Get the a byte[] for the chunk
     *
     * @return
     */
    public byte[] toBytes();

}
