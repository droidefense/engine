package com.j256.simplemagic.types;

import com.j256.simplemagic.endian.EndianType;

/**
 * A 32-bit single precision IEEE floating point number in this machine's native byte order.
 * 
 * @author graywatson
 */
public class FloatType extends DoubleType {

	private static final int BYTES_PER_FLOAT = 4;

	public FloatType(EndianType endianType) {
		super(endianType);
	}

	@Override
	public Number decodeValueString(String valueStr) throws NumberFormatException {
		return Float.parseFloat(valueStr);
	}

	@Override
	public int compare(boolean unsignedType, Number extractedValue, Number testValue) {
		float extractedFloat = extractedValue.floatValue();
		float testFloat = testValue.floatValue();
		if (extractedFloat > testFloat) {
			return 1;
		} else if (extractedFloat < testFloat) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	protected Object longToObject(Long value) {
		return Float.intBitsToFloat(value.intValue());
	}

	@Override
	public int getBytesPerType() {
		return BYTES_PER_FLOAT;
	}
}
