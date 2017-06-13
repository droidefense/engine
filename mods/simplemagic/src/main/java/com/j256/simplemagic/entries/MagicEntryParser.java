package com.j256.simplemagic.entries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.j256.simplemagic.ContentInfoUtil.ErrorCallBack;
import com.j256.simplemagic.endian.EndianConverter;
import com.j256.simplemagic.endian.EndianType;
import com.j256.simplemagic.entries.MagicEntry.OffsetInfo;

/**
 * Class which parses a line from the magic (5) format and produces a {@link MagicEntry} class.
 * 
 * @author graywatson
 */
public class MagicEntryParser {

	private static final String UNKNOWN_NAME = "unknown";
	// special lines, others are put into the extensionMap
	private static final String MIME_TYPE_LINE = "!:mime";
	private static final String OPTIONAL_LINE = "!:optional";

	private final static Pattern OFFSET_PATTERN =
			Pattern.compile("\\(([0-9a-fA-Fx]+)\\.?([bsilBSILm]?)([\\*\\+\\-]?)([0-9a-fA-Fx]*)\\)");

	/**
	 * Parse a line from the magic configuration file into an entry.
	 */
	public static MagicEntry parseLine(MagicEntry previous, String line, ErrorCallBack errorCallBack) {
		if (line.startsWith("!:")) {
			if (previous != null) {
				// we ignore it if there is no previous entry to add it to
				handleSpecial(previous, line, errorCallBack);
			}
			return null;
		}

		// 0[ ]string[ ]%PDF-[ ]PDF document
		// !:mime[ ]application/pdf
		// >5[ ]byte[ ]x[ ]\b, version %c
		// >7[ ]byte[ ]x[ ]\b.%c

		// unfortunately, we cannot use split or even regex since the whitespace is not reliable (grumble)
		String[] parts = splitLine(line, errorCallBack);
		if (parts == null) {
			return null;
		}

		// level and offset
		int level;
		int sindex = parts[0].lastIndexOf('>');
		String offsetString;
		if (sindex < 0) {
			level = 0;
			offsetString = parts[0];
		} else {
			level = sindex + 1;
			offsetString = parts[0].substring(sindex + 1);
		}

		int offset;
		OffsetInfo offsetInfo;
		if (offsetString.length() == 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid offset number:" + offsetString, null);
			}
			return null;
		}
		boolean addOffset = false;
		if (offsetString.charAt(0) == '&') {
			addOffset = true;
			offsetString = offsetString.substring(1);
		}
		if (offsetString.charAt(0) == '(') {
			offset = -1;
			offsetInfo = parseOffset(offsetString, line, errorCallBack);
			if (offsetInfo == null) {
				return null;
			}
		} else {
			try {
				offset = Integer.decode(offsetString);
				offsetInfo = null;
			} catch (NumberFormatException e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "invalid offset number:" + offsetString, e);
				}
				return null;
			}
		}

		// process the AND (&) part of the type
		String typeStr = parts[1];
		sindex = typeStr.indexOf('&');
		// we use long because of overlaps
		Long andValue = null;
		if (sindex >= 0) {
			String andStr = typeStr.substring(sindex + 1);
			try {
				andValue = Long.decode(andStr);
			} catch (NumberFormatException e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "invalid type AND-number: " + andStr, e);
				}
				return null;
			}
			typeStr = typeStr.substring(0, sindex);
		}
		if (typeStr.length() == 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "blank type string", null);
			}
			return null;
		}

		// process the type string
		boolean unsignedType = false;
		MagicMatcher matcher = MagicType.matcherfromString(typeStr);
		if (matcher == null) {
			if (typeStr.charAt(0) == 'u') {
				matcher = MagicType.matcherfromString(typeStr.substring(1));
				unsignedType = true;
			} else {
				int index = typeStr.indexOf('/');
				if (index > 0) {
					matcher = MagicType.matcherfromString(typeStr.substring(0, index));
				}
			}
			if (matcher == null) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "unknown magic type string: " + typeStr, null);
				}
				return null;
			}
		}

		// process the test-string
		Object testValue;
		String testStr = parts[2];
		if (testStr.equals("x")) {
			testValue = null;
		} else {
			try {
				testValue = matcher.convertTestString(typeStr, testStr);
			} catch (Exception e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "could not convert magic test string: " + testStr, e);
				}
				return null;
			}
		}

		MagicFormatter formatter;
		String name;
		boolean formatSpacePrefix = true;
		boolean clearFormat = false;
		if (parts.length == 3) {
			formatter = null;
			name = UNKNOWN_NAME;
		} else {
			String format = parts[3];
			// a starting \\b or ^H means don't prepend a space when chaining content details
			if (format.startsWith("\\b")) {
				format = format.substring(2);
				formatSpacePrefix = false;
			} else if (format.startsWith("\010")) {
				// NOTE: sometimes the \b is expressed as a ^H character (grumble)
				format = format.substring(1);
				formatSpacePrefix = false;
			} else if (format.startsWith("\\r")) {
				format = format.substring(2);
				clearFormat = true;
			}
			formatter = new MagicFormatter(format);

			String trimmedFormat = format.trim();
			int spaceIndex = trimmedFormat.indexOf(' ');
			if (spaceIndex < 0) {
				spaceIndex = trimmedFormat.indexOf('\t');
			}
			if (spaceIndex > 0) {
				name = trimmedFormat.substring(0, spaceIndex);
			} else if (trimmedFormat.length() == 0) {
				name = UNKNOWN_NAME;
			} else {
				name = trimmedFormat;
			}
		}
		MagicEntry entry = new MagicEntry(name, level, addOffset, offset, offsetInfo, matcher, andValue, unsignedType,
				testValue, formatSpacePrefix, clearFormat, formatter);
		return entry;
	}

	private static String[] splitLine(String line, ErrorCallBack errorCallBack) {
		// skip opening whitespace if any
		int startPos = findNonWhitespace(line, 0);
		if (startPos < 0) {
			return null;
		}

		// find the level info
		int endPos = findWhitespaceWithoutEscape(line, startPos);
		if (endPos < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid number of whitespace separated fields, must be >= 4", null);
			}
			return null;
		}
		String levelStr = line.substring(startPos, endPos);

		// skip whitespace
		startPos = findNonWhitespace(line, endPos + 1);
		if (startPos < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid number of whitespace separated fields, must be >= 4", null);
			}
			return null;
		}
		// find the type string
		endPos = findWhitespaceWithoutEscape(line, startPos);
		if (endPos < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid number of whitespace separated fields, must be >= 4", null);
			}
			return null;
		}
		String typeStr = line.substring(startPos, endPos);

		// skip whitespace
		startPos = findNonWhitespace(line, endPos + 1);
		if (startPos < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid number of whitespace separated fields, must be >= 4", null);
			}
			return null;
		}
		// find the test string
		endPos = findWhitespaceWithoutEscape(line, startPos);
		if (endPos < 0) {
			endPos = line.length();
		}
		String testStr = line.substring(startPos, endPos);

		// skip any whitespace
		startPos = findNonWhitespace(line, endPos + 1);
		// format is optional, this could return length
		if (startPos < 0) {
			return new String[] { levelStr, typeStr, testStr };
		} else {
			// format is the rest of the line
			return new String[] { levelStr, typeStr, testStr, line.substring(startPos) };
		}
	}

	private static void handleSpecial(MagicEntry previous, String line, ErrorCallBack errorCallBack) {
		if (line.equals(OPTIONAL_LINE)) {
			previous.setOptional(true);
			return;
		}
		int startPos = findNonWhitespace(line, 0);
		int index = findWhitespaceWithoutEscape(line, startPos);
		if (index < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid extension line has less than 2 whitespace separated fields", null);
			}
			return;
		}
		String key = line.substring(startPos, index);
		startPos = findNonWhitespace(line, index);
		if (startPos < 0) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid extension line has less than 2 whitespace separated fields", null);
			}
			return;
		}
		// find whitespace after value, if any
		index = findWhitespaceWithoutEscape(line, startPos);
		if (index < 0) {
			index = line.length();
		}
		String value = line.substring(startPos, index);

		if (key.equals(MIME_TYPE_LINE)) {
			previous.setMimeType(value);
		} else {
			// unknown extension key
		}
	}

	private static int findNonWhitespace(String line, int startPos) {
		for (int pos = startPos; pos < line.length(); pos++) {
			if (!Character.isWhitespace(line.charAt(pos))) {
				return pos;
			}
		}
		return -1;
	}

	private static int findWhitespaceWithoutEscape(String line, int startPos) {
		boolean lastEscape = false;
		for (int pos = startPos; pos < line.length(); pos++) {
			char ch = line.charAt(pos);
			if (ch == ' ') {
				if (!lastEscape) {
					return pos;
				}
				lastEscape = false;
			} else if (Character.isWhitespace(line.charAt(pos))) {
				return pos;
			} else if (ch == '\\') {
				lastEscape = true;
			} else {
				lastEscape = false;
			}
		}
		return -1;
	}

	/**
	 * Copied from the magic(5) man page:
	 * 
	 * <p>
	 * Offsets do not need to be constant, but can also be read from the file being examined. If the first character
	 * following the last '>' is a '(' then the string after the parenthesis is interpreted as an indirect offset. That
	 * means that the number after the parenthesis is used as an offset in the file. The value at that offset is read,
	 * and is used again as an offset in the file. Indirect offsets are of the form: ((x[.[bsilBSILm]][+-]y). The value
	 * of x is used as an offset in the file. A byte, id3 length, short or long is read at that offset depending on the
	 * [bislBISLm] type specifier. The capitalized types interpret the number as a big-endian value, whereas the small
	 * letter versions interpret the number as a little-endian value; the 'm' type interprets the number as a
	 * middle-endian (PDP-11) value. To that number the value of y is added and the result is used as an offset in the
	 * file. The default type if one is not specified is 4-byte long.
	 * </p>
	 */
	private static OffsetInfo parseOffset(String offsetString, String line, ErrorCallBack errorCallBack) {
		// (9.b+19)
		// (0x3c.l)
		// (8.s*16)
		Matcher matcher = OFFSET_PATTERN.matcher(offsetString);
		if (!matcher.matches()) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid offset pattern: " + offsetString, null);
			}
			return null;
		}
		int offset;
		try {
			offset = Integer.decode(matcher.group(1));
		} catch (NumberFormatException e) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid long offset number: " + offsetString, e);
			}
			return null;
		}
		if (matcher.group(2) == null) {
			if (errorCallBack != null) {
				errorCallBack.error(line, "invalid long offset type: " + offsetString, null);
			}
			return null;
		}
		char ch;
		if (matcher.group(2).length() == 1) {
			ch = matcher.group(2).charAt(0);
		} else {
			// it will use the default
			ch = '\0';
		}
		EndianConverter converter = null;
		boolean isId3 = false;
		int size = 0;
		switch (ch) {
			// little-endian byte
			case 'b':
				// endian doesn't really matter for 1 byte
				converter = EndianType.LITTLE.getConverter();
				size = 1;
				break;
			// little-endian short
			case 's':
				converter = EndianType.LITTLE.getConverter();
				size = 2;
				break;
			// little-endian integer
			case 'i':
				converter = EndianType.LITTLE.getConverter();
				size = 4;
				isId3 = true;
				break;
			// little-endian long (4 byte)
			case 'l':
				converter = EndianType.LITTLE.getConverter();
				size = 4;
				break;
			// big-endian byte
			case 'B':
				// endian doesn't really matter for 1 byte
				converter = EndianType.BIG.getConverter();
				size = 1;
				break;
			// big-endian short
			case 'S':
				converter = EndianType.BIG.getConverter();
				size = 2;
				break;
			// big-endian integer
			case 'I':
				converter = EndianType.BIG.getConverter();
				size = 4;
				isId3 = true;
				break;
			// big-endian long (4 byte)
			case 'L':
				converter = EndianType.BIG.getConverter();
				size = 4;
				break;
			// big-endian integer
			case 'm':
				converter = EndianType.MIDDLE.getConverter();
				size = 4;
				break;
			default:
				converter = EndianType.LITTLE.getConverter();
				size = 4;
				break;
		}
		int add = 0;
		// the +# section is optional
		if (matcher.group(4) != null && matcher.group(4).length() > 0) {
			try {
				add = Integer.decode(matcher.group(4));
			} catch (NumberFormatException e) {
				if (errorCallBack != null) {
					errorCallBack.error(line, "invalid long add value: " + matcher.group(4), e);
				}
				return null;
			}
			// decode doesn't work with leading '+', grumble
			String offsetOperator = matcher.group(3);
			if ("-".equals(offsetOperator)) {
				add = -add;
			} else if ("-".equals(offsetOperator)) {
				offset = add;
				add = 0;
			}
		}
		return new OffsetInfo(offset, converter, isId3, size, add);
	}
}
