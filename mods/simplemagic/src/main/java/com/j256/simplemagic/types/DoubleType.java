package com.j256.simplemagic.types;

import com.j256.simplemagic.endian.EndianType;

/**
 * A 64-bit double precision IEEE floating point number in this machine's native byte order.
 *
 * @author graywatson
 */
public class DoubleType extends NumberType {

    private static final int BYTES_PER_DOUBLE = 8;

    public DoubleType(EndianType endianType) {
        super(endianType);
    }

    @Override
    public Number decodeValueString(String valueStr) throws NumberFormatException {
        return Double.parseDouble(valueStr);
    }

    @Override
    public Object extractValueFromBytes(int offset, byte[] bytes, boolean required) {
        Long val = endianConverter.convertNumber(offset, bytes, getBytesPerType());
        if (val == null) {
            return null;
        } else {
            return longToObject(val);
        }
    }

    @Override
    public int compare(boolean unsignedType, Number extractedValue, Number testValue) {
        double extractedDouble = extractedValue.doubleValue();
        double testDouble = testValue.doubleValue();
        if (extractedDouble > testDouble) {
            return 1;
        } else if (extractedDouble < testDouble) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public byte[] getStartingBytes(Object testValue) {
        return null;
    }


    /**
     * Convert a long to the type.
     */
    protected Object longToObject(Long value) {
        return Double.longBitsToDouble(value);
    }

    @Override
    public long maskValue(long value) {
        return value;
    }

    /**
     * Return the number of bytes in this type.
     */
    @Override
    public int getBytesPerType() {
        return BYTES_PER_DOUBLE;
    }
}
