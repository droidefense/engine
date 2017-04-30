package com.j256.simplemagic.entries;

/**
 * Classes which are able to match content according to operations and output description.
 * 
 * @author graywatson
 */
public interface MagicMatcher {

	/**
	 * Converts the test-string from the magic line to be the testValue object to be passed into
	 * {@link #isMatch(Object, Long, boolean, Object, MutableOffset, byte[])} and {@link #getStartingBytes(Object)}.
	 */
	public Object convertTestString(String typeStr, String testStr);

	/**
	 * Extract the value from the bytes.
	 * 
	 * @return The object to be passed to {@link #isMatch(Object, Long, boolean, Object, MutableOffset, byte[])} or null
	 *         if not enough bytes.
	 */
	public Object extractValueFromBytes(int offset, byte[] bytes);

	/**
	 * Matches if the bytes match at a certain offset.
	 * 
	 * @return The extracted-value object, or null if no match.
	 */
	public Object isMatch(Object testValue, Long andValue, boolean unsignedType, Object extractedValue,
			MutableOffset offset, byte[] bytes);

	/**
	 * Returns the string version of the extracted value.
	 */
	public void renderValue(StringBuilder sb, Object extractedValue, MagicFormatter formatter);

	/**
	 * Return the starting bytes of the pattern or null if none.
	 */
	public byte[] getStartingBytes(Object testValue);

	/**
	 * Offset which we can update.
	 */
	public static class MutableOffset {
		public int offset;

		public MutableOffset(int offset) {
			this.offset = offset;
		}
	}
}
