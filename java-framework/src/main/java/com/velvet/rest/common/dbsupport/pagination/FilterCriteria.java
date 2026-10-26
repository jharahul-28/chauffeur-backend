package com.velvet.rest.common.dbsupport.pagination;

import java.io.Serializable;

import com.velvet.rest.common.dbsupport.constants.Operation;

public class FilterCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7641980620468903918L;
	private String column;
	private Object value;
	private Operation operation = Operation.EQUALS;

	public FilterCriteria() {

	}

	public FilterCriteria(String column, Object value, Operation operation) {
		super();
		this.column = column;
		this.value = value;
		this.operation = operation;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

}
