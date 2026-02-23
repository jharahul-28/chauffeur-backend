package com.velvet.rest.common.dbsupport.pagination;

import java.util.List;

/**
 * The Class PageAndSortResult.
 *
 * @param <T> the generic type
 * @author Mohit
 * @version $Id: $Id
 */
public class PageAndSortResult<T> {

	/** The page no. */
	private Integer pageNo;

	/** The page size. */
	private Integer pageSize;

	/** The total pages. */
	private Integer pages;

	/** The total records. */
	private Integer total;

	/** The data. */
	private List<T> data;

	/**
	 * <p>
	 * Constructor for PageAndSortResult.
	 * </p>
	 */
	public PageAndSortResult() {}

	/**
	 * <p>
	 * Constructor for PageAndSortResult.
	 * </p>
	 *
	 * @param pageNo   a {@link java.lang.Integer} object.
	 * @param pageSize a {@link java.lang.Integer} object.
	 * @param pages    a {@link java.lang.Integer} object.
	 * @param total    a {@link java.lang.Integer} object.
	 * @param data     a {@link java.util.List} object.
	 */
	public PageAndSortResult(Integer pageNo, Integer pageSize, Integer pages, Integer total, List<T> data) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.pages = pages;
		this.total = total;
		this.data = data;
	}

	/**
	 * Gets the total pages.
	 *
	 * @return the total pages
	 */
	public Integer getPages() {
		return pages;
	}

	/**
	 * Sets the total pages.
	 *
	 * @param pages a {@link java.lang.Integer} object.
	 */
	public void setPages(Integer pages) {
		this.pages = pages;
	}

	/**
	 * Gets the page no.
	 *
	 * @return the page no
	 */
	public Integer getPageNo() {
		return pageNo;
	}

	/**
	 * Sets the page no.
	 *
	 * @param pageNo the new page no
	 */
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * Gets the total records.
	 *
	 * @return the total records
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * Sets the total records.
	 *
	 * @param total a {@link java.lang.Integer} object.
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
}
