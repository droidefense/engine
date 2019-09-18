package android.content.res;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author tstrazzere
 */
@RunWith(Enclosed.class)
public class TestIntReader {

    public static class UnitTest {

        InputStream mockStream;

        IntReader underTest;

        @Before
        public void setUp() {
            mockStream = mock(InputStream.class);
            underTest = new IntReader(mockStream, true);
        }

        @Test
        public void testClose() throws IOException {
            underTest.close();

            verify(mockStream, times(1)).close();
        }

        @Test
        public void testReadShort() throws IOException {
            underTest.readShort();

            verify(mockStream, times(2)).read();
        }

        @Test
        public void testReadByte() throws IOException {
            underTest.readByte();

            verify(mockStream, times(1)).read();
        }

        @Test
        public void testReadInt() throws IOException {
            underTest.readInt();

            verify(mockStream, times(4)).read();
        }

        @Test
        public void testCloseDoesntDieWithNull() {
            underTest = new IntReader(null, true);
            underTest.close();
        }

        @Test
        public void testSkips() throws IOException {
            when(mockStream.skip(8)).thenReturn(8L);
            when(mockStream.skip(4)).thenReturn(4L);

            // Nothing happens
            underTest.skip(0);
            // Skip 8
            underTest.skip(8);
            // Skip 4
            underTest.skipInt();

            verify(mockStream, times(1)).skip(8);
            verify(mockStream, times(1)).skip(4);
        }

        @Test
        public void testSkipFails() throws IOException {
            when(mockStream.skip(4)).thenReturn(-1L);

            try {
                underTest.skipInt();
                throw new AssertionError("Excepted exception!");
            } catch (IOException exception) {
                // Good case
            }
        }

        @Test
        public void testReadIntFailsBadParams() {
            try {
                underTest.readInt(-1);
                throw new AssertionError("Excepted exception!");
            } catch (IllegalArgumentException exception) {
                // Good case
            } catch (IOException exception) {
                throw new AssertionError("Unexcepted exception!");
            }

            try {
                underTest.readInt(5);
                throw new IllegalArgumentException("Excepted exception!");
            } catch (IllegalArgumentException exception) {
                // Good case
            } catch (IOException exception) {
                throw new AssertionError("Unexcepted exception!");
            }
        }

        @Test
        public void testReadIntFailsEOF() throws IOException {
            when(mockStream.read()).thenReturn(-1);
            try {
                underTest.readInt(2);
                throw new AssertionError("Excepted exception!");
            } catch (IOException exception) {
                // Good case
            }
        }

        @Test
        public void testReadIntBigEndian() throws IOException {
            when(mockStream.read()).thenReturn(10).thenReturn(20).thenReturn(30).thenReturn(40);

            assertEquals(10, underTest.readInt(1));

            assertEquals(((20 << 16) | (30 << 8) | (40 << 0)), underTest.readInt(3));
        }

        @Test
        public void testReadIntLittleEndian() throws IOException {
            underTest = new IntReader(mockStream, false);
            when(mockStream.read()).thenReturn(10).thenReturn(20).thenReturn(30).thenReturn(40);

            assertEquals(10, underTest.readInt(1));

            assertEquals(((20 << 0) | (30 << 8) | (40 << 16)), underTest.readInt(3));
        }
    }
}
