package axmlparser.android.content.res;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class IntReader {

    private InputStream m_stream;
    private boolean m_bigEndian;
    private int m_position;


    public IntReader() {
    }

    public IntReader(InputStream stream, boolean bigEndian) {
        this.reset(stream, bigEndian);
    }

    public final void reset(InputStream stream, boolean bigEndian) {
        this.m_stream = stream;
        this.m_bigEndian = bigEndian;
        this.m_position = 0;
    }

    public final void close() {
        if (this.m_stream != null) {
            try {
                this.m_stream.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }

            this.reset((InputStream) null, false);
        }
    }

    public final InputStream getStream() {
        return this.m_stream;
    }

    public final boolean isBigEndian() {
        return this.m_bigEndian;
    }

    public final void setBigEndian(boolean bigEndian) {
        this.m_bigEndian = bigEndian;
    }

    public final int readByte() throws IOException {
        return this.readInt(1);
    }

    public final int readShort() throws IOException {
        return this.readInt(2);
    }

    public final int readInt() throws IOException {
        return this.readInt(4);
    }

    public final int readInt(int length) throws IOException {
        if (length >= 0 && length <= 4) {
            int result = 0;
            int i;
            int b;
            if (this.m_bigEndian) {
                for (i = (length - 1) * 8; i >= 0; i -= 8) {
                    b = this.m_stream.read();
                    if (b == -1) {
                        throw new EOFException();
                    }

                    ++this.m_position;
                    result |= b << i;
                }
            } else {
                length *= 8;

                for (i = 0; i != length; i += 8) {
                    b = this.m_stream.read();
                    if (b == -1) {
                        throw new EOFException();
                    }

                    ++this.m_position;
                    result |= b << i;
                }
            }

            return result;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public final int[] readIntArray(int length) throws IOException {
        int[] array = new int[length];
        this.readIntArray(array, 0, length);
        return array;
    }

    public final void readIntArray(int[] array, int offset, int length) throws IOException {
        while (length > 0) {
            array[offset++] = this.readInt();
            --length;
        }

    }

    public final byte[] readByteArray(int length) throws IOException {
        byte[] array = new byte[length];
        int read = this.m_stream.read(array);
        this.m_position += read;
        if (read != length) {
            throw new EOFException();
        } else {
            return array;
        }
    }

    public final void skip(int bytes) throws IOException {
        if (bytes > 0) {
            long skipped = this.m_stream.skip((long) bytes);
            this.m_position = (int) ((long) this.m_position + skipped);
            if (skipped != (long) bytes) {
                throw new EOFException();
            }
        }
    }

    public final void skipInt() throws IOException {
        this.skip(4);
    }

    public final int available() throws IOException {
        return this.m_stream.available();
    }

    public final int getPosition() {
        return this.m_position;
    }
}
