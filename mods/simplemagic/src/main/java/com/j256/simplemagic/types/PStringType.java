package com.j256.simplemagic.types;

/**
 * A Pascal-style string where the first byte is interpreted as the an unsigned length. The string is not '\0'
 * terminated.
 * 
 * @author graywatson
 */
public class PStringType extends StringType {

	/**
	 * Extracted value is the extracted string using the first byte as the length.
	 */
	@Override
	public Object extractValueFromBytes(int offset, byte[] bytes, boolean required) {
		// we don't need to extract the value if all we are doing is matching
		if (!required) {
			return EMPTY;
		}
		if (offset >= bytes.length) {
			return null;
		}
		// length is from the first byte of the string
		int len = (bytes[offset] & 0xFF);
		int left = bytes.length - offset - 1;
		if (len > left) {
			len = left;
		}
		char[] chars = new char[len];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) (bytes[offset + 1 + i] & 0xFF);
		}
		/*
		 * NOTE: we need to make a new string because it might be returned if we don't match below.
		 */
		return new String(chars);
	}

	@Override
	public Object isMatch(Object testValue, Long andValue, boolean unsignedType, Object extractedValue,
			MutableOffset mutableOffset, byte[] bytes) {

		if (mutableOffset.offset >= bytes.length) {
			return null;
		}
		// our maximum position is +1 to move past the length byte and then add in the length
		int len = (bytes[mutableOffset.offset] & 0xFF);
		int maxPos = 1 + len;
		if (maxPos > bytes.length) {
			maxPos = bytes.length;
		}

		// we start matching past the length byte so the starting offset is +1
		return findOffsetMatch((TestInfo) testValue, mutableOffset.offset + 1, mutableOffset, bytes, null, maxPos);
	}
}
