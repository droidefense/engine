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
import android.content.res.chunk.types.GenericChunk;

import java.io.IOException;

/**
 * Generic ChunkSection class for generalizing the reading and minimizing the repetitive code inside of the specific
 * sections (likely overkill..)
 *
 * @author tstrazzere
 */
public abstract class GenericChunkSection extends GenericChunk implements Chunk, ChunkSection {

    public GenericChunkSection(ChunkType chunkType, IntReader reader) {
        super(chunkType, reader);

        try {
            readSection(reader);

            reader.skip(Math.abs(reader.getBytesRead() - getStartPosition() - size));
        } catch (IOException e) {
            // Catching this here allows us to continue reading
            e.printStackTrace();
        }
    }

}
