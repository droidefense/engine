package com.j256.simplemagic.types;

/**
 * Internal class that provides information about a particular test.
 */
public enum StringOperator {

	EQUALS('=') {
		@Override
		public boolean doTest(char extractedChar, char testChar, boolean lastChar) {
			return extractedChar == testChar;
		}
	},
	NOT_EQUALS('!') {
		@Override
		public boolean doTest(char extractedChar, char testChar, boolean lastChar) {
			return extractedChar != testChar;
		}
	},
	GREATER_THAN('>') {
		@Override
		public boolean doTest(char extractedChar, char testChar, boolean lastChar) {
			if (lastChar) {
				return extractedChar > testChar;
			} else {
				return extractedChar >= testChar;
			}
		}
	},
	LESS_THAN('<') {
		@Override
		public boolean doTest(char extractedChar, char testChar, boolean lastChar) {
			if (lastChar) {
				return extractedChar < testChar;
			} else {
				return extractedChar <= testChar;
			}
		}
	},
	// end
	;

	/**
	 * Default operator which should be used if {@link #fromTest(String)} returns null;
	 */
	public static final StringOperator DEFAULT_OPERATOR = EQUALS;

	private final char prefixChar;

	private StringOperator(char prefixChar) {
		this.prefixChar = prefixChar;
	}

	/**
	 * Test 2 characters. If this is the last character then the operator might want to be more strict in its testing.
	 * For example, "dogs" > "dog" but 'd', 'o', and 'g' should be tested as >=.
	 */
	public abstract boolean doTest(char extractedChar, char testChar, boolean lastChar);

	/**
	 * Returns the operator if the first character is an operator. Otherwise this returns null and you should use the
	 * {@link #DEFAULT_OPERATOR}.
	 * 
	 * <p>
	 * <b>NOTE:</b> We _don't_ return the default operator here because the caller needs to know if the prefix was
	 * supplied or not.
	 * </p>
	 */
	public static StringOperator fromTest(String testStr) {
		if (testStr.length() == 0) {
			return null;
		}
		char first = testStr.charAt(0);
		for (StringOperator operator : values()) {
			if (operator.prefixChar == first) {
				return operator;
			}
		}
		return null;
	}
}
