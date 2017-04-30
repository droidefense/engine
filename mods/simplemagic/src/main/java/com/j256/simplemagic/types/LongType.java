package com.j256.simplemagic.types;

import com.j256.simplemagic.endian.EndianType;

/**
 * An eight-byte value constituted "quad" when the magic file spec was written.
 * 
 * @author graywatson
 */
public class LongType extends BaseLongType {

	static final int BYTES_PER_LONG = 8;

	public LongType(EndianType endianType) {
		super(endianType);
	}

	/**
	 * Return the number of bytes in this type.
	 */
	@Override
	public int getBytesPerType() {
		return BYTES_PER_LONG;
	}

	@Override
	public long maskValue(long value) {
		return value;
	}

	@Override
	public int compare(boolean unsignedType, Number extractedValue, Number testValue) {
		return staticCompare(extractedValue, testValue);
	}

	/**
	 * Static compare of longs which are unsigned or signed.
	 */
	public static int staticCompare(Number extractedValue, Number testValue) {
		long extractedLong = extractedValue.longValue();
		long testLong = testValue.longValue();
		if (extractedLong > testLong) {
			return 1;
		} else if (extractedLong < testLong) {
			return -1;
		} else {
			return 0;
		}
	}
}
