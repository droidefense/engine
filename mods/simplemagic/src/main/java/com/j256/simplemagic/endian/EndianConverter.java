package com.j256.simplemagic.endian;

/**
 * Class which converts from a particular machine byte representation into values appropriate for Java.
 * 
 * @author graywatson
 */
public interface EndianConverter {

	/**
	 * Convert a number of bytes starting at an offset into a long integer.
	 * 
	 * @return The long or null if not enough bytes.
	 */
	public Long convertNumber(int offset, byte[] bytes, int size);

	/**
	 * Convert a number of bytes starting at an offset into a long integer where the high-bit in each byte is always 0.
	 * 
	 * @return The long or null if not enough bytes.
	 */
	public Long convertId3(int offset, byte[] bytes, int size);

	/**
	 * Translate a number into an array of bytes.
	 */
	public byte[] convertToByteArray(long value, int size);
}
