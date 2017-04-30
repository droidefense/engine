package com.j256.simplemagic.types;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.entries.MagicFormatter;

/**
 * A 4-byte value interpreted as a UNIX-style date, but interpreted as local time rather than UTC.
 * 
 * @author graywatson
 */
public class LocalDateType extends IntegerType {

	protected final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		}
	};

	public LocalDateType(EndianType endianType) {
		super(endianType);
	}

	@Override
	public void renderValue(StringBuilder sb, Object extractedValue, MagicFormatter formatter) {
		long val = (Long) extractedValue;
		Date date = dateFromExtractedValue(val);
		SimpleDateFormat format = dateFormat.get();
		assisgnTimeZone(format);
		formatter.format(sb, format.format(date));
	}

	protected Date dateFromExtractedValue(long val) {
		val *= 1000;
		return new Date(val);
	}

	protected void assisgnTimeZone(SimpleDateFormat format) {
		// noop for local time-zone
	}
}
