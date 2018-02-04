package android.content.res;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import android.content.res.chunk.ChunkType;
import android.content.res.chunk.ChunkUtil;
import android.content.res.chunk.sections.ResourceSection;
import android.content.res.chunk.sections.StringSection;
import android.content.res.chunk.types.AXMLHeader;
import android.content.res.chunk.types.Buffer;
import android.content.res.chunk.types.Chunk;
import android.content.res.chunk.types.EndTag;
import android.content.res.chunk.types.NameSpace;
import android.content.res.chunk.types.StartTag;
import android.content.res.chunk.types.TextTag;

/**
 * @author tstrazzere
 */
@RunWith(Enclosed.class)
public class TestChunkUtil {
    public static class UnitTest {

        @Test
        public void testReadChunkType() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.AXML_HEADER.getIntType());

            assertEquals(ChunkType.AXML_HEADER, ChunkUtil.readChunkType(mockReader));
        }

        @Test
        public void testBadReadChunkType() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(-1);

            try {
                ChunkUtil.readChunkType(mockReader);
                throw new AssertionError("Expected exception!");
            } catch (IOException exception) {
                // Good case
            }
        }

        @Test
        public void testCreateAXMLHeader() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.AXML_HEADER.getIntType(), 0);

            if (!(ChunkUtil.createChunk(mockReader) instanceof AXMLHeader)) {
                throw new AssertionError("Expected AXMLHeader chunk!");
            }
        }

        @Test
        public void testCreateResourceSection() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.RESOURCE_SECTION.getIntType(), 8, 0);

            if (!(ChunkUtil.createChunk(mockReader) instanceof ResourceSection)) {
                throw new AssertionError("Expected AXMLHeader chunk!");
            }
        }

        @Test
        public void testCreateStringSection() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.STRING_SECTION.getIntType(), 0);

            if (!(ChunkUtil.createChunk(mockReader) instanceof StringSection)) {
                throw new AssertionError("Expected AXMLHeader chunk!");
            }
        }

        @Test
        public void testCreateBuffer() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.BUFFER.getIntType(), 0);

            if (!(ChunkUtil.createChunk(mockReader) instanceof Buffer)) {
                throw new AssertionError("Expected AXMLHeader chunk!");
            }
        }

        @Test
        public void testCreateNameSpace() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.START_NAMESPACE.getIntType(), 0);

            Chunk chunk = ChunkUtil.createChunk(mockReader);
            if (!(chunk instanceof NameSpace)) {
                throw new AssertionError("Expected NameSpace chunk!");
            }

            assertEquals(true, ((NameSpace) chunk).isStart());

            when(mockReader.readInt()).thenReturn(ChunkType.END_NAMESPACE.getIntType(), 0);
            chunk = ChunkUtil.createChunk(mockReader);
            if (!(chunk instanceof NameSpace)) {
                throw new AssertionError("Expected NameSpace chunk!");
            }

            assertEquals(false, ((NameSpace) chunk).isStart());

        }

        @Test
        public void testCreateStartTag() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.START_TAG.getIntType(), 0);

            Chunk chunk = ChunkUtil.createChunk(mockReader);
            if (!(chunk instanceof StartTag)) {
                throw new AssertionError("Expected StartTag chunk!");
            }
        }

        @Test
        public void testCreateEndTag() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.END_TAG.getIntType(), 0);

            Chunk chunk = ChunkUtil.createChunk(mockReader);
            if (!(chunk instanceof EndTag)) {
                throw new AssertionError("Expected EndTag chunk!");
            }
        }

        @Test
        public void testCreateTextTag() throws IOException {
            IntReader mockReader = mock(IntReader.class);
            when(mockReader.readInt()).thenReturn(ChunkType.TEXT_TAG.getIntType(), 0);

            Chunk chunk = ChunkUtil.createChunk(mockReader);
            if (!(chunk instanceof TextTag)) {
                throw new AssertionError("Expected TextTag chunk!");
            }
        }
    }
}
