package com.velvet.rest.common.dbsupport.crud;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

	@Transactional(propagation = Propagation.SUPPORTS)
	List<T> findAll(Map<String, SortDirection> sort, int limit, int active);

	@Transactional(propagation = Propagation.SUPPORTS)
	List<T> findAll(Map<String, Object> filters, Map<String, SortDirection> sort, int limit, int active);

	@Transactional(propagation = Propagation.SUPPORTS)
	PageAndSortResult<T> findAll(Map<String, Object> filters, Map<String, SortDirection> sort, int limit, int offset, int active);

	@Transactional(propagation = Propagation.SUPPORTS)
	T getById(ID id);

	@Transactional(propagation = Propagation.SUPPORTS)
	List<T> getByIds(List<ID> ids);

	@Transactional(propagation = Propagation.SUPPORTS)
	Long count(Map<String, Object> filters, int active);

	@Transactional(propagation = Propagation.SUPPORTS)
	Long count(Map<String, Object> filters);

	@Transactional(propagation = Propagation.SUPPORTS)
	Long count(int active);
}
