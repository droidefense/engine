package com.j256.simplemagic.entries;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formatter that handles the C %0.2f type formats appropriately. I would have used the {@link java.util.Formatter} but
 * you can't pre-parse those for some stupid reason. Also, I needed this to be compatible with the printf(3) C formats.
 * 
 * @author graywatson
 */
public class MagicFormatter {

	private final String prefix;
	private final PercentExpression percentExpression;
	private final String suffix;

	public final static String FINAL_PATTERN_CHARS = "%bcdeEfFgGiosuxX";
	public final static String PATTERN_MODIFIERS = "lqh";
	// NOTE: the backspace is taken care of by checking the format string prefix above
	private final static Pattern FORMAT_PATTERN = Pattern.compile("([^%]*)(%[-+0-9# ." + PATTERN_MODIFIERS + "]*["
			+ FINAL_PATTERN_CHARS + "])?(.*)");

	/**
	 * This takes a format string, breaks it up into prefix, %-thang, and suffix.
	 */
	public MagicFormatter(String formatString) {
		Matcher matcher = FORMAT_PATTERN.matcher(formatString);
		if (!matcher.matches()) {
			// may never get here
			prefix = formatString;
			percentExpression = null;
			suffix = null;
			return;
		}

		String prefixMatch = matcher.group(1);
		String percentMatch = matcher.group(2);
		String suffixMatch = matcher.group(3);

		if (percentMatch != null && percentMatch.equals("%%")) {
			// we go recursive trying to find the first true % pattern
			MagicFormatter formatter = new MagicFormatter(suffixMatch);
			StringBuilder sb = new StringBuilder();
			if (prefixMatch != null) {
				sb.append(prefixMatch);
			}
			sb.append('%');
			if (formatter.prefix != null) {
				sb.append(formatter.prefix);
			}
			prefix = sb.toString();
			percentExpression = formatter.percentExpression;
			suffix = formatter.suffix;
			return;
		}

		if (prefixMatch == null || prefixMatch.length() == 0) {
			prefix = null;
		} else {
			prefix = prefixMatch;
		}
		if (percentMatch == null || percentMatch.length() == 0) {
			percentExpression = null;
		} else {
			percentExpression = new PercentExpression(percentMatch);
		}
		if (suffixMatch == null || suffixMatch.length() == 0) {
			suffix = null;
		} else {
			suffix = suffixMatch.replace("%%", "%");
		}
	}

	/**
	 * Formats the extracted value assigned and returns the associated string
	 */
	public void format(StringBuilder sb, Object value) {
		if (prefix != null) {
			sb.append(prefix);
		}
		if (percentExpression != null && value != null) {
			percentExpression.append(value, sb);
		}
		if (suffix != null) {
			sb.append(suffix);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) {
			sb.append(prefix);
		}
		if (percentExpression != null) {
			sb.append(percentExpression);
		}
		if (suffix != null) {
			sb.append(suffix);
		}
		return sb.toString();
	}
}
