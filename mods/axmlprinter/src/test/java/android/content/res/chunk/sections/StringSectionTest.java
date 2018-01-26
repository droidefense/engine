package android.content.res.chunk.sections;

import android.content.res.IntReader;
import android.content.res.chunk.ChunkType;
import junit.framework.TestCase;
import org.junit.Assert;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by diff on 9/29/15.
 */
public class StringSectionTest extends TestCase {

    private StringSection underTest;

    private IntReader mockReader;
    private ChunkType mockChunkType;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockReader = mock(IntReader.class);
        // Mock the string section data
        when(mockReader.readInt()).thenReturn(
                (12 * 4), // size
                0x02, // string count
                0x00, // style count
                0x100, // string chunk flags
                0x24, // string pool offset
                0x00, // style pool offset
                // string pool
                0x00, // item 1 offset
                0x08); // item 2 offset
        when(mockReader.readByte()).thenReturn(
                0x04, // item 1 length
                0x64, // d
                0x69, // i
                0x66, // f
                0x66, // f
                // 0x00, // buffer
                // 0x00, // buffer
                // 0x00, // buffer
                0x03, // item 2 length
                0x74, // t
                0x69, // i
                0x6D); // m

        mockChunkType = mock(ChunkType.class);
        when(mockChunkType.getIntType()).thenReturn(ChunkType.STRING_SECTION.getIntType());

        underTest = new StringSection(mockChunkType, mockReader);
    }

    public void testToBytes() throws Exception {
        byte[] expected = {
                // STRING_SECTION
                (byte) 0x01, (byte) 0x00, (byte) 0x1C, (byte) 0x00,
                // size
                (byte) (12 * 4), (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string count
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // TODO : Really should test this when I get a good sample
                // style count
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string chunk flags
                (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                // string pool offset
                (byte) 0x24, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // style pool offset
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string pool offsets - item 1
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string pool offsets - item 2
                (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string data - item 1
                // len(diff)           d            i            f
                (byte) 0x04, (byte) 0x64, (byte) 0x69, (byte) 0x66,
                //        f   (buffer ----------------------------)
                (byte) 0x66, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // string data - item 2
                // len(tim)           t            i            m
                (byte) 0x03, (byte) 0x74, (byte) 0x69, (byte) 0x6d
        };

        byte[] actual = underTest.toBytes();
        Assert.assertArrayEquals(expected, actual);
    }
}