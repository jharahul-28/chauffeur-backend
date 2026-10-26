package com.velvet.rest.services.common.upload.csv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * CsvUploadResult class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class CsvUploadResult<T> implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -4576327511129948175L;

	private Integer total = 0;
	private Integer success = 0;
	private Integer skipped = 0;
	private Integer failed = 0;
	private String key = null;
	private List<T> successRows = new ArrayList<>();
	private List<T> failedRows = new ArrayList<>();
	private List<T> skippedRows = new ArrayList<>();
	private String headerMapping;

	public Integer getTotal() {
		total = getSuccess() + getFailed() + getSkipped();
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSuccess() {
		success = successRows.size();
		return success;
	}

	public void setSuccess(Integer success) {
		this.success = success;
	}

	public Integer getFailed() {
		failed = failedRows.size();
		return failed;
	}

	public void setFailed(Integer failed) {
		this.failed = failed;
	}

	public List<T> getSuccessRows() {
		return successRows;
	}

	public void setSuccessRows(List<T> successRows) {
		this.successRows = successRows;
	}

	public void addSuccessRow(T bean) {
		this.successRows.add(bean);
	}

	public void addFailedRow(T bean) {
		this.failedRows.add(bean);
	}

	public void addSkippedRow(T bean) {
		this.skippedRows.add(bean);
	}

	public List<T> getFailedRows() {
		return failedRows;
	}

	public Integer getSkipped() {
		skipped = skippedRows.size();
		return skipped;
	}

	public void setSkipped(Integer skipped) {
		this.skipped = skipped;
	}

	public List<T> getSkippedRows() {
		return skippedRows;
	}

	public void setSkippedRows(List<T> skippedRows) {
		if (skippedRows == null) {
			this.skippedRows.clear();
		} else {
			this.skippedRows = skippedRows;
		}
	}

	public void setFailedRows(List<T> failedRows) {
		this.failedRows = failedRows;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getHeaderMapping() {
		return headerMapping;
	}

	public void setHeaderMapping(String headerMapping) {
		this.headerMapping = headerMapping;
	}

}
