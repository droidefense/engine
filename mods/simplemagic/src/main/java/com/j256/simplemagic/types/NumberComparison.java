package com.j256.simplemagic.types;

/**
 * Internal class that compares a number from the bytes with the value from the magic rule.
 */
public class NumberComparison {

	private final NumberType numberType;
	private final TestOperator operator;
	private final Number value;

	/**
	 * Pre-process the test string into an operator and a value.
	 */
	public NumberComparison(NumberType numberType, String testStr) {
		this.numberType = numberType;
		TestOperator op = TestOperator.fromTest(testStr);
		String valueStr;
		if (op == null) {
			op = TestOperator.DEFAULT_OPERATOR;
			valueStr = testStr;
		} else {
			valueStr = testStr.substring(1).trim();
		}
		this.operator = op;
		try {
			this.value = numberType.decodeValueString(valueStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Could not parse number from: '" + valueStr + "'");
		}
	}

	public boolean isMatch(Long andValue, boolean unsignedType, Number extractedValue) {
		if (andValue != null) {
			extractedValue = extractedValue.longValue() & andValue;
		}
		return operator.doTest(unsignedType, extractedValue, value, numberType);
	}

	public Number getValue() {
		return value;
	}

	@Override
	public String toString() {
		return operator + ", value " + value;
	}
}
