package com.j256.simplemagic.types;

/**
 * A Pascal-style string where the first byte is interpreted as the an unsigned length. The string is not '\0'
 * terminated.
 * 
 * @author graywatson
 */
public class PStringType extends StringType {

	/**
	 * Extracted value is the length of the string which is then validated in the match below.
	 */
	@Override
	public Object extractValueFromBytes(int offset, byte[] bytes) {
		int len = 0;
		if (offset < bytes.length) {
			len = bytes[offset];
			int left = bytes.length - offset;
			if (len > left) {
				len = left;
			}
		}
		char[] chars = new char[len];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) bytes[offset + 1 + i];
		}
		/*
		 * NOTE: we need to make a new string because it might be returned if we don't match below.
		 */
		return new String(chars);
	}

	/**
	 * The extracted value is the first byte as an Integer length which is then checked after the
	 * {@link StringType#isMatch} is applied.
	 */
	@Override
	public Object isMatch(Object testValue, Long andValue, boolean unsignedType, Object extractedValue,
			MutableOffset mutableOffset, byte[] bytes) {

		/*
		 * We find the match in the array of bytes that were extracted instead of from the bytes passed in. This means
		 * that we start at the starting offset of 0.
		 */
		int initialStart = mutableOffset.offset;
		String result = findOffsetMatch((TestInfo) testValue, 0, mutableOffset, ((String) extractedValue).getBytes());
		/*
		 * When we come back the mutable offset was set to the position in the extract bytes which is from 0. We need to
		 * adjust it to make it from the initial start that was passed in.
		 */
		mutableOffset.offset = initialStart + mutableOffset.offset;
		return result;
	}
}
