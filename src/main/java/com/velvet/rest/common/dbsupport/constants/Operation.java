package com.velvet.rest.common.dbsupport.constants;

/**
 * <p>
 * Operation class.
 * </p>
 *
 * @author mohit
 * @version $Id: $Id
 */
public enum Operation {
	EQUALS(" = "), NOT_EQUALS("<>"), GT(">"), LT("<"), GTE(">="), LTE("<="), LIKE("like"), IN("in"), IS_NULL(" is null "), IS_NOT_NULL(" is not null ");

	private final String value;

	Operation(final String newValue) {
		value = newValue;
	}

	/**
	 * <p>
	 * Getter for the field <code>value</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue() {
		return value;
	}
}
