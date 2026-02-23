/*
 * @author Mohit
 */
package com.velvet.rest.common.websupport.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.constants.Limit;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.exceptions.NoDataException;
import com.velvet.rest.common.properties.Errors;

/**
 * The Interface BaseService.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public interface BaseService<E> {

	int ACTIVE = 1;

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	Class<E> getEntity();

	/**
	 * Find record by ids.
	 *
	 * @param ids the ids
	 * @return the list of e
	 */
	default List<E> findRecordByIds(List<?> ids) {
		final List<E> entity = (List<E>) getRepository().findAllById(ids);
		return entity;
	}

	/**
	 * Find record by ids.
	 *
	 * @param ids the ids
	 * @return the list of e
	 */
	default List<E> findAllRecords() {
		return findAllRecords(defaultFilterMap());
	}

	default List<E> findAllRecords(int limit) {
		return findAllRecords(defaultFilterMap(), defaultSortMap(), limit);
	}

	/**
	 * Find records.
	 *
	 * @param filters the filters
	 * @return the list
	 */
	default List<E> findAllRecords(Map<String, Object> filters) {
		return getRepository().findAll(filters, defaultSortMap(), Limit.NO_LIMIT, ACTIVE);
	}

	default List<E> findAllRecords(Map<String, Object> filters, Integer active) {
		return getRepository().findAll(filters, defaultSortMap(), Limit.NO_LIMIT, active);
	}

	default List<E> findAllRecords(Map<String, Object> filters, Map<String, SortDirection> sort) {
		return getRepository().findAll(filters, sort, Limit.NO_LIMIT, ACTIVE);
	}

	default List<E> findAllRecords(Map<String, Object> filters, Map<String, SortDirection> sort, int limit) {
		return getRepository().findAll(filters, sort, limit, ACTIVE);
	}

	default List<E> findAllRecords(Map<String, Object> filters, int limit) {
		return getRepository().findAll(filters, defaultSortMap(), limit, ACTIVE);
	}

	default PageAndSortResult<E> findPagedRecords(Map<String, Object> filters, Map<String, SortDirection> sort, Integer pageSize, Integer pageNo) {
		return getRepository().findAll(filters, sort, pageSize, pageNo, ACTIVE);
	}

	default PageAndSortResult<E> findAllPagedRecords(Map<String, Object> filters, Map<String, SortDirection> sort, Integer pageSize, Integer pageNo) {
		return getRepository().findAll(filters, sort, pageSize, pageNo, -1);
	}

	default PageAndSortResult<E> findAllRecords(Map<String, Object> filters, Integer pageSize, Integer pageNo) {
		return getRepository().findAll(filters, null, pageSize, pageNo, ACTIVE);
	}

	/**
	 * Find record by id.
	 *
	 * @param id the id
	 * @return the e
	 */
	default E findRecordById(Object id) {
		return (E) getRepository().getById(id);
	}

	/**
	 * Find record by id.
	 *
	 * @param id the id
	 * @return the e
	 */
	default List<E> findRecordsByIds(List<?> ids) {
		return getRepository().getByIds(ids);
	}

	default void throwNoDataException(Object id) {
		final String entityName = convertCamelCaseToStatement(getEntity().getSimpleName().replace("Entity", ""));
		throw new NoDataException(entityName.concat(". No Data Found ( " + (id != null ? id : "-") + " )"), ErrorBean.withError(Errors.NO_DATA_FOUND));
	}

	default String convertCamelCaseToStatement(String camelCase) {
		try {
			final StringBuilder builder = new StringBuilder();
			for (final Character c : camelCase.toCharArray()) {
				if (Character.isUpperCase(c)) {
					builder.append(" ").append(c);
				} else {
					builder.append(c);
				}
			}
			return builder.toString();
		} catch (final Exception e) {
			return camelCase;
		}
	}

	/**
	 * Find record by id.
	 *
	 * @param id the id
	 * @return the e
	 */
	default void softDelete(BaseEntity e) {
		Integer active = 0;
		e.setActive(active);
		getRepository().save(e);
	}

	/**
	 * Find record by id.
	 *
	 * @param id the id
	 * @return the e
	 */
	default void delete(BaseEntity e) {
		softDelete(e);
	}

	/**
	 * Delete record by id.
	 *
	 * @param id the id
	 * @return the e
	 */
	default void hardDelete(BaseEntity e) {
		getRepository().delete(e);
	}

	/**
	 * Find record by ids.
	 *
	 * @param ids the ids
	 * @return the list of e
	 */
	default List<E> findAllWithoutActive() {
		final List<E> entity = (List<E>) getRepository().findAll();
		return entity;
	}

	/**
	 * get default sort.
	 *
	 */
	default Map<String, SortDirection> defaultSortMap() {
		Map<String, SortDirection> sort = new LinkedHashMap<>();
		return sort;
	}

	/**
	 * get default sort.
	 *
	 */
	default Map<String, Object> defaultFilterMap() {
		Map<String, Object> filters = new HashMap<>();
		return filters;
	}

	BaseCrudRepository getRepository();

}
