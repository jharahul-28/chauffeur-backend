/*
 * @author Sanjeev
 * Copyright milkbasket.com
 */
package com.velvet.rest.services.common.upload.csv;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * CSVBulkRequest class.
 * </p>
 *
 * @author Sanjeev
 * @version $Id: $Id
 */
public class CSVBulkRequest<T extends CSVMapping> implements Serializable {

	private static final long serialVersionUID = 8489807765977502063L;

	private List<T> data;

	public CSVBulkRequest() {
		super();
	}

	public CSVBulkRequest(List<T> data) {
		this.data = data;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
