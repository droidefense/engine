package com.j256.simplemagic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Class which wraps and delegates to another {@link InputStream}. This allows you to read from an input stream and then
 * after you are done, call {@link #findMatch()} to determine the content-type information of the bytes read.
 * 
 * <p>
 * <b> NOTE: </b> This keeps a buffer of the first {@link ContentInfoUtil#DEFAULT_READ_SIZE} (maybe 10k) bytes read or
 * skipped to determine the content-type of the bytes read.
 * </p>
 * 
 * @author graywatson
 */
public class ContentInfoInputStreamWrapper extends InputStream {

	private final InputStream delegate;
	private final ContentInfoUtil contentInfoUtil;
	private final byte[] firstBytes = new byte[ContentInfoUtil.DEFAULT_READ_SIZE];
	private int byteCount;

	private static ContentInfoUtil staticContentInfoUtil;

	/**
	 * Create a stream wrapper while specifying your own ContentInfoUtil.
	 */
	public ContentInfoInputStreamWrapper(InputStream delegate, ContentInfoUtil contentInfoUtil) {
		this.delegate = delegate;
		this.contentInfoUtil = contentInfoUtil;
	}

	/**
	 * Create a stream wrapper while using the internal, static ContentInfoUtil.
	 */
	public ContentInfoInputStreamWrapper(InputStream delegate) {
		this(delegate, getStaticContentInfoUtil());
	}

	/**
	 * Find a match from the bytes that have been read from the stream using {@link ContentInfoUtil#findMatch(byte[])}.
	 */
	public ContentInfo findMatch() {
		byte[] readBytes;
		if (byteCount < firstBytes.length) {
			readBytes = Arrays.copyOf(firstBytes, byteCount);
		} else {
			readBytes = firstBytes;
		}
		return contentInfoUtil.findMatch(readBytes);
	}

	@Override
	public int available() throws IOException {
		return delegate.available();
	}

	@Override
	public int read() throws IOException {
		int b = delegate.read();
		if (byteCount < firstBytes.length) {
			firstBytes[byteCount++] = (byte) b;
		}
		return b;
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		int numRead = delegate.read(b, off, len);
		int left = firstBytes.length - byteCount;
		if (left > numRead) {
			left = numRead;
		}
		for (int i = off; i < off + left; i++) {
			firstBytes[byteCount++] = b[i];
		}
		return numRead;
	}

	@Override
	public long skip(long skipNum) throws IOException {
		// see how many bytes are left to be read in first buffer
		int left = firstBytes.length - byteCount;
		// reduce it if skip-num is less
		if (left > skipNum) {
			left = (int) skipNum;
		}
		int numRead = 0;
		if (left > 0) {
			// read the left bytes into our buffer, this changes the byte-count
			numRead = read(firstBytes, byteCount, left);
			// now we need to skip by less
			skipNum -= numRead;
		}
		// if still need to skip
		if (skipNum > 0) {
			long numSkipped = delegate.skip(skipNum);
			return numRead + numSkipped;
		} else {
			return numRead;
		}
	}

	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public void mark(int readlimit) {
		delegate.mark(readlimit);
	}

	@Override
	public void reset() throws IOException {
		delegate.reset();
	}

	@Override
	public boolean markSupported() {
		return delegate.markSupported();
	}

	private static synchronized ContentInfoUtil getStaticContentInfoUtil() {
		if (staticContentInfoUtil == null) {
			staticContentInfoUtil = new ContentInfoUtil();
		}
		return staticContentInfoUtil;
	}
}
