package com.velvet.rest.common.dbsupport.pagination;

import java.io.Serializable;
import java.util.Map;

/**
 * The Class PageAndSortRequest.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class PageAndSortRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5886958224439063396L;
	private static ThreadLocal<String> ADDITIONAL_QUERY_TOKEN = new ThreadLocal<>();
	private static ThreadLocal<String> ADDITIONAL_GROUP_TOKEN = new ThreadLocal<>();
	private static ThreadLocal<String> ADDITIONAL_HAVING_TOKEN = new ThreadLocal<>();

	/**
	 * The Enum SortDirection.
	 */
	public enum SortDirection {

		/** The asc. */
		ASC,
		/** The desc. */
		DESC;
	}

	/** The page no. */
	private Integer pageNo;

	/** The page size. */
	private Integer pageSize;

	private Map<String, SortCriteria> sort;
	private Map<String, FilterCriteria> filters;

	/**
	 * Instantiates a new page and sort request.
	 */
	public PageAndSortRequest() {
	}

	public PageAndSortRequest(Integer pageNo, Integer pageSize, Map<String, SortCriteria> sort, Map<String, FilterCriteria> filters) {
		super();
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		this.sort = sort;
		this.filters = filters;
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
	 * @param pageNo
	 *            the new page no
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
	 * @param pageSize
	 *            the new page size
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Map<String, SortCriteria> getSort() {
		return sort;
	}

	public void setSort(Map<String, SortCriteria> sort) {
		this.sort = sort;
	}

	public Map<String, FilterCriteria> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, FilterCriteria> filters) {
		this.filters = filters;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageAndSortRequest [pageNo=").append(pageNo).append(", pageSize=").append(pageSize).append(", sort=").append("")
				.append(", filters=").append("").append("]");
		return builder.toString();
	}

	public static void setAdditionalQueryToken(String token) {
		ADDITIONAL_QUERY_TOKEN.set(token);
	}

	public static String getAdditionalQueryToken() {
		return ADDITIONAL_QUERY_TOKEN.get();
	}

	public static void clearToken() {
		ADDITIONAL_QUERY_TOKEN.remove();
	}

	public static void setGroupToken(String token) {
		ADDITIONAL_GROUP_TOKEN.set(token);
	}

	public static String getGroupToken() {
		return ADDITIONAL_GROUP_TOKEN.get();
	}

	public static void clearGroupToken() {
		ADDITIONAL_GROUP_TOKEN.remove();
	}

	public static void setHavingToken(String token) {
		ADDITIONAL_HAVING_TOKEN.set(token);
	}

	public static String getHavingToken() {
		return ADDITIONAL_HAVING_TOKEN.get();
	}

	public static void clearHavingToken() {
		ADDITIONAL_HAVING_TOKEN.remove();
	}

}
