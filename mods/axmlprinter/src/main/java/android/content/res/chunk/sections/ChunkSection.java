/* 
 * Copyright 2015 Red Naga
 * 
 * Licensedimport java.io.IOException;

import android.content.res.IntReader;
import android.content.res.chunk.types.Chunk;
e License.
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
import android.content.res.chunk.types.Chunk;

import java.io.IOException;

/**
 * Interface for Chunk which is a section type
 *
 * @author tstrazzere
 */
public interface ChunkSection extends Chunk {

    /**
     * Read the 'header' part of the section.
     */
    public void readHeader(IntReader inputReader) throws IOException;

    /**
     * Read the
     *
     * @param inputReader
     * @throws IOException
     */
    public void readSection(IntReader inputReader) throws IOException;

}
