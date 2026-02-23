package com.velvet.rest.common.dbsupport.pagination;

import java.io.Serializable;

import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;

public class SortCriteria implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4241028583581956524L;
	private String column;
	private SortDirection direction = SortDirection.ASC;

	public SortCriteria() {
	}

	public SortCriteria(String column, SortDirection direction) {
		super();
		this.column = column;
		this.direction = direction;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public SortDirection getDirection() {
		return direction;
	}

	public void setDirection(SortDirection direction) {
		this.direction = direction;
	}

}
