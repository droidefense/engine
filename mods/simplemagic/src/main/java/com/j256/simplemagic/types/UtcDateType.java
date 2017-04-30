package com.j256.simplemagic.types;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.j256.simplemagic.endian.EndianType;

/**
 * A 4-byte value interpreted as a UNIX date in UTC timezone.
 * 
 * @author graywatson
 */
public class UtcDateType extends LocalDateType {

	private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

	public UtcDateType(EndianType endianType) {
		super(endianType);
	}

	@Override
	protected Date dateFromExtractedValue(long val) {
		val *= 1000;
		return new Date(val);
	}

	@Override
	protected void assisgnTimeZone(SimpleDateFormat format) {
		format.setTimeZone(UTC_TIME_ZONE);
	}
}
