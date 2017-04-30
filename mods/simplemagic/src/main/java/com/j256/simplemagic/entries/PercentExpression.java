package com.j256.simplemagic.entries;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of our percent expression used by the {@link MagicFormatter} class.
 * 
 * @author graywatson
 */
public class PercentExpression {

	private final static String ZERO_CHARS = "00000000000000000000000000000000000000000000000000000000000000000000000";
	private final static String SPACE_CHARS = "                                                                      ";

	private final static Pattern FORMAT_PATTERN = Pattern.compile("%([0#+ -]*)([0-9]*)(\\.([0-9]+))?(["
			+ MagicFormatter.PATTERN_MODIFIERS + "]*)([" + MagicFormatter.FINAL_PATTERN_CHARS + "])");

	private final String expression;
	private final boolean justValue;
	private final boolean alternativeForm;
	private final boolean zeroPrefix;
	private final boolean plusPrefix;
	private final boolean spacePrefix;
	private final boolean leftAdjust;
	private final int totalWidth;
	private final int truncateWidth;
	private final char patternChar;
	private final Format decimalFormat;
	/** if we need to choose the shorter of two formats */
	private final Format altDecimalFormat;

	/**
	 * Package permissions because the {@link MagicFormatter} should only be constructing it.
	 */
	PercentExpression(String expression) {
		this.expression = expression;
		Matcher matcher = FORMAT_PATTERN.matcher(expression);
		if (!matcher.matches() || matcher.group(6) == null || matcher.group(6).length() != 1) {
			// may never get here but let's be careful
			this.justValue = true;
			this.alternativeForm = false;
			this.patternChar = 0;
			this.zeroPrefix = false;
			this.plusPrefix = false;
			this.spacePrefix = false;
			this.leftAdjust = false;
			this.totalWidth = -1;
			this.truncateWidth = -1;
			this.decimalFormat = null;
			this.altDecimalFormat = null;
			return;
		}
		this.justValue = false;

		String flags = matcher.group(1);
		this.alternativeForm = readFlag(flags, '#');
		this.zeroPrefix = readFlag(flags, '0');
		this.plusPrefix = readFlag(flags, '+');
		if (this.plusPrefix) {
			// + overrides space
			this.spacePrefix = false;
		} else {
			this.spacePrefix = readFlag(flags, ' ');
		}
		this.leftAdjust = readFlag(flags, '-');
		this.totalWidth = readPrecision(matcher.group(2), -1);
		int dotPrecision = readPrecision(matcher.group(4), -1);
		// 5 is ignored
		this.patternChar = matcher.group(6).charAt(0);
		switch (this.patternChar) {
			case 'e' :
			case 'E' : {
				this.decimalFormat = scientificFormat(dotPrecision);
				this.altDecimalFormat = null;
				break;
			}
			case 'f' :
			case 'F' : {
				this.decimalFormat = decimalFormat(dotPrecision);
				this.altDecimalFormat = null;
				break;
			}
			case 'g' :
			case 'G' : {
				// will take the shorter of the two
				this.decimalFormat = decimalFormat(dotPrecision);
				this.altDecimalFormat = scientificFormat(dotPrecision);
				break;
			}
			default :
				this.decimalFormat = null;
				this.altDecimalFormat = null;
				break;
		}
		if (patternChar == 's' || patternChar == 'b') {
			this.truncateWidth = dotPrecision;
		} else {
			this.truncateWidth = -1;
		}
	}

	public void append(Object extractedValue, StringBuilder sb) {
		if (justValue) {
			// may never get here
			sb.append(extractedValue);
			return;
		}

		// %bcdeEfFgGiosuxX
		switch (patternChar) {
			case 'b' :
			case 's' : {
				// same as s but interpret character escapes in backslash notation
				String strValue = extractedValue.toString();
				if (truncateWidth >= 0 && strValue.length() > truncateWidth) {
					strValue = strValue.substring(0, truncateWidth);
				}
				appendValue(sb, null, null, strValue, false);
				return;
			}
			case 'c' : {
				// character
				String strValue;
				if (extractedValue instanceof Character) {
					strValue = Character.toString((Character) extractedValue);
				} else if (extractedValue instanceof Number) {
					strValue = Character.toString((char) ((Number) extractedValue).shortValue());
				} else if (extractedValue instanceof String) {
					String str = (String) extractedValue;
					if (str.length() == 0) {
						strValue = "";
					} else {
						strValue = str.substring(0, 1);
					}
				} else {
					strValue = "?";
				}
				appendValue(sb, null, null, strValue, false);
				return;
			}
			case 'd' :
			case 'i' :
			case 'u' :
				if (extractedValue instanceof Number) {
					long value = ((Number) extractedValue).longValue();
					String sign = null;
					if (value >= 0) {
						if (plusPrefix) {
							sign = "+";
						} else if (spacePrefix) {
							sign = " ";
						}
					} else {
						sign = "-";
						value = -value;
					}
					String strValue = Long.toString(value);
					appendValue(sb, sign, null, strValue, true);
					return;
				}
				break;
			case 'e' :
			case 'E' :
			case 'f' :
			case 'F' :
			case 'g' :
			case 'G' :
				if (extractedValue instanceof Number) {
					double value = ((Number) extractedValue).doubleValue();
					if (Double.isInfinite(value)) {
						sb.append("inf");
						return;
					} else if (Double.isNaN(value)) {
						sb.append("nan");
						return;
					}
					String sign = null;
					if (value >= 0) {
						if (plusPrefix) {
							sign = "+";
						} else if (spacePrefix) {
							sign = " ";
						}
					} else {
						// XXX: is this right? setting the value to negative and a sign? need to test this.
						sign = "-";
						value = -value;
					}
					String strValue = decimalFormat.format(value);
					if (altDecimalFormat != null) {
						String strValue2 = altDecimalFormat.format(value);
						if (strValue2.length() < strValue.length()) {
							strValue = strValue2;
						}
					}
					appendValue(sb, sign, null, strValue, true);
					return;
				}
				break;
			// case 'i' : same as d above
			case 'o' :
				// octal
				if (extractedValue instanceof Number) {
					long value = ((Number) extractedValue).longValue();
					String sign = null;
					if (value < 0) {
						sign = "-";
						value = -value;
					}
					String prefix = null;
					if (alternativeForm) {
						prefix = "0";
					}
					String strValue = Long.toOctalString(value);
					appendValue(sb, sign, prefix, strValue, true);
					return;
				}
				break;
			// case 's' : same as b above
			// case 'u' : same as d above
			case 'x' :
				if (extractedValue instanceof Number) {
					appendHex(sb, false, extractedValue);
					return;
				}
				break;
			case 'X' :
				if (extractedValue instanceof Number) {
					appendHex(sb, true, extractedValue);
					return;
				}
				break;
			default :
				break;
		}

		// oh well, just dump it out
		sb.append(extractedValue);
	}

	@Override
	public String toString() {
		return expression;
	}

	private static int readPrecision(String string, int defaultVal) {
		if (string == null || string.length() == 0) {
			return defaultVal;
		}
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			// ignored
			return defaultVal;
		}
	}

	private static boolean readFlag(String flags, char flagChar) {
		if (flags != null && flags.indexOf(flagChar) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	private void appendHex(StringBuilder sb, boolean upper, Object extractedValue) {
		long value = ((Number) extractedValue).longValue();
		String sign = null;
		if (value < 0) {
			sign = "-";
			value = -value;
		}
		String prefix = null;
		if (alternativeForm) {
			if (upper) {
				prefix = "0X";
			} else {
				prefix = "0x";
			}
		}
		String strValue = Long.toHexString(value);
		if (upper) {
			strValue = strValue.toUpperCase();
		}
		appendValue(sb, sign, prefix, strValue, true);
	}

	private void appendValue(StringBuilder sb, String sign, String prefix, String value, boolean isNumber) {
		int len = 0;
		if (sign != null) {
			len += sign.length();
		}
		if (prefix != null) {
			len += prefix.length();
		}
		len += value.length();
		int diff = totalWidth - len;
		if (diff < 0) {
			diff = 0;
		}
		if (!leftAdjust) {
			if (isNumber && zeroPrefix) {
				if (sign != null) {
					sb.append(sign);
					sign = null;
				}
				if (prefix != null) {
					// may never get here
					sb.append(prefix);
					prefix = null;
				}
				appendChars(sb, ZERO_CHARS, diff);
			} else {
				appendChars(sb, SPACE_CHARS, diff);
			}
		}
		if (sign != null) {
			sb.append(sign);
		}
		if (prefix != null) {
			sb.append(prefix);
		}
		sb.append(value);
		if (leftAdjust) {
			// always space if left-adjust
			appendChars(sb, SPACE_CHARS, diff);
		}
	}

	private void appendChars(StringBuilder sb, String indentChars, int diff) {
		while (true) {
			if (diff > indentChars.length()) {
				sb.append(indentChars);
				diff -= indentChars.length();
			} else {
				sb.append(indentChars, 0, diff);
				break;
			}
		}
	}

	/**
	 * -d.ddd+-dd style, if no precision then 6 digits, 'inf', nan', if 0 precision then ""
	 */
	private Format decimalFormat(int fractionPrecision) {
		DecimalFormat format;
		if (fractionPrecision == 0) {
			format = new DecimalFormat("###0");
		} else if (fractionPrecision > 0) {
			StringBuilder formatSb = new StringBuilder();
			formatSb.append("###0.");
			appendChars(formatSb, ZERO_CHARS, fractionPrecision);
			format = new DecimalFormat(formatSb.toString());
		} else {
			format = new DecimalFormat("###0.###");
		}
		return format;
	}

	private Format scientificFormat(int fractionPrecision) {
		DecimalFormat format;
		if (fractionPrecision == 0) {
			format = new DecimalFormat("0E0");
		} else if (fractionPrecision > 0) {
			StringBuilder formatSb = new StringBuilder();
			formatSb.append("0.");
			appendChars(formatSb, ZERO_CHARS, fractionPrecision);
			formatSb.append("E0");
			format = new DecimalFormat(formatSb.toString());
		} else {
			format = new DecimalFormat("0.###E0");
		}
		return format;
	}
}
