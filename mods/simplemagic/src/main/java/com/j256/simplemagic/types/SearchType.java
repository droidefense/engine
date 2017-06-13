package com.j256.simplemagic.types;

/**
 * From the magic(5) man page: A literal string search starting at the given line offset. The same modifier flags can be
 * used as for string patterns. The modifier flags (if any) must be followed by /number range, that is, the number of
 * positions at which the match will be attempted, starting from the start offset. This is suitable for searching larger
 * binary expressions with variable offsets, using \ escapes for special characters. The offset works as for regex.
 * 
 * <p>
 * <b>NOTE:</b> in our experience, the /number is _before_ the flags in 99% of the lines so that is how we implemented
 * it.
 * </p>
 * 
 * @author graywatson
 */
public class SearchType extends StringType {

	@Override
	public Object isMatch(Object testValue, Long andValue, boolean unsignedType, Object extractedValue,
			MutableOffset mutableOffset, byte[] bytes) {
		TestInfo info = (TestInfo) testValue;
		int maxOffset = info.maxOffset;
		if (info.optionalWhiteSpace) {
			// we have to look at all of the bytes unfortunately
			maxOffset = bytes.length;
		}
		// if offset is 1 then we need to pre-read 1 char
		int end = mutableOffset.offset + maxOffset + info.pattern.length();
		if (end > bytes.length) {
			end = bytes.length;
		}
		for (int offset = mutableOffset.offset; offset < end; offset++) {
			String match = findOffsetMatch(info, offset, mutableOffset, bytes, null, bytes.length);
			if (match != null) {
				return match;
			}
		}
		return null;
	}
}
