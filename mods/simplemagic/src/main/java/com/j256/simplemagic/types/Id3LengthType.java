package com.j256.simplemagic.types;

import com.j256.simplemagic.endian.EndianType;

/**
 * A four-byte integer value where the high bit of each byte is ignored.
 * 
 * @author graywatson
 */
public class Id3LengthType extends IntegerType {

	public Id3LengthType(EndianType endianType) {
		super(endianType);
	}

	@Override
	public Object extractValueFromBytes(int offset, byte[] bytes, boolean required) {
		// because we only use the lower 7-bits of each byte, we need to copy into a local byte array
		int bytesPerType = getBytesPerType();
		byte[] sevenBitBytes = new byte[bytesPerType];
		for (int i = 0; i < bytesPerType; i++) {
			sevenBitBytes[i] = (byte) (bytes[offset + i] & 0x7F);
		}
		// because we've copied into a local array, we use the 0 offset
		return endianConverter.convertNumber(0, sevenBitBytes, bytesPerType);
	}
}
