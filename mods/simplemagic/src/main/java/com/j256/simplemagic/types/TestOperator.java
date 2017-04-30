package com.j256.simplemagic.types;

/**
 * Operators for tests. If no operator character then equals is assumed.
 */
public enum TestOperator {

	EQUALS('=') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			return (numberType.compare(unsignedType, extractedValue, testValue) == 0);
		}
	},
	NOT_EQUALS('!') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			return (numberType.compare(unsignedType, extractedValue, testValue) != 0);
		}
	},
	GREATER_THAN('>') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			return (numberType.compare(unsignedType, extractedValue, testValue) > 0);
		}
	},
	LESS_THAN('<') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			return (numberType.compare(unsignedType, extractedValue, testValue) < 0);
		}
	},
	AND_ALL_SET('&') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			// NOTE: we assume that we are dealing with decimal numbers here
			long testValueLong = testValue.longValue();
			return ((extractedValue.longValue() & testValueLong) == testValueLong);
		}
	},
	AND_ALL_CLEARED('^') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			// NOTE: we assume that we are dealing with decimal numbers here
			return ((extractedValue.longValue() & testValue.longValue()) == 0);
		}
	},
	NEGATE('~') {
		@Override
		public boolean doTest(boolean unsignedType, Number extractedValue, Number testValue, NumberType numberType) {
			// we need the mask because we are using bit negation but testing only a portion of the long
			// NOTE: we assume that we are dealing with decimal numbers here
			long negatedValue = numberType.maskValue(~testValue.longValue());
			return (extractedValue.longValue() == negatedValue);
		}
	},
	// end
	;

	/**
	 * Default operator which should be used if {@link #fromTest(String)} returns null;
	 */
	public static final TestOperator DEFAULT_OPERATOR = EQUALS;

	private final char prefixChar;

	private TestOperator(char prefixChar) {
		this.prefixChar = prefixChar;
	}

	/**
	 * Perform the test using the operator.
	 */
	public abstract boolean doTest(boolean unsignedType, Number extractedValue, Number testValue,
			NumberType numberType);

	/**
	 * Returns the operator if the first character is an operator. Otherwise this returns null and you should use the
	 * {@link #DEFAULT_OPERATOR}.
	 * 
	 * <p>
	 * <b>NOTE:</b> We _don't_ return the default operator here because the caller needs to know if the prefix was
	 * supplied or not.
	 * </p>
	 */
	public static TestOperator fromTest(String testStr) {
		if (testStr.length() == 0) {
			return null;
		}
		char first = testStr.charAt(0);
		for (TestOperator operator : values()) {
			if (operator.prefixChar == first) {
				return operator;
			}
		}
		return null;
	}
}
