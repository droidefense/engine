package com.j256.simplemagic.types;

import com.j256.simplemagic.endian.EndianType;

/**
 * A four-byte integer value which often handles the "long" types when the spec was written.
 * 
 * @author graywatson
 */
public class IntegerType extends BaseLongType {

	private static final int BYTES_PER_INTEGER = 4;

	public IntegerType(EndianType endianType) {
		super(endianType);
	}

	@Override
	public int getBytesPerType() {
		return BYTES_PER_INTEGER;
	}

	@Override
	public long maskValue(long value) {
		return value & 0xFFFFFFFFL;
	}

	@Override
	public int compare(boolean unsignedType, Number extractedValue, Number testValue) {
		if (unsignedType) {
			return LongType.staticCompare(extractedValue, testValue);
		}
		int extractedInt = extractedValue.intValue();
		int testInt = testValue.intValue();
		if (extractedInt > testInt) {
			return 1;
		} else if (extractedInt < testInt) {
			return -1;
		} else {
			return 0;
		}
	}
}
