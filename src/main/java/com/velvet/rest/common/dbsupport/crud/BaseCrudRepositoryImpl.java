package com.velvet.rest.common.dbsupport.crud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.velvet.rest.common.dbsupport.constants.Operation;
import com.velvet.rest.common.dbsupport.pagination.FilterCriteria;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.DatabaseUtils;

public class BaseCrudRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseCrudRepository<T, ID> {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(BaseCrudRepositoryImpl.class);

	/** The em. */
	private EntityManager em;

	public static int MAX_RECORDS_LIMIT = 5000;

	public static int MAX_PAGE_SIZE = 500;

	public static final int FIND_BATCH_SIZE = 500;

	/**
	 * Instantiates a new base crud repository impl.
	 *
	 * @param entityInformation the entity information
	 * @param entityManager     the entity manager
	 */
	public BaseCrudRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.em = entityManager;
	}

	/** {@inheritDoc} */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<T> findAll() {
		return super.findAll();
	}

	@Override
	public List<T> findAll(Map<String, Object> filters, Map<String, SortDirection> sort, int limit, int active) {
		if (limit < 0) {
			limit = MAX_RECORDS_LIMIT;
		}

		if (active > 1) {
			active = 1;
		}

		if (CollectionUtils.isEmpty(filters)) {
			return findAll(sort, limit, active);
		}

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
		final Root<T> queryRoot = criteriaQuery.from(getDomainClass());

		final List<Predicate> restrictions = new ArrayList<>();

		if (active >= 0 && hasActive()) {
			restrictions.add(criteriaBuilder.equal(queryRoot.get("active"), active));
		}

		filters.keySet().forEach(k -> restrictions.add(getRestriction(criteriaBuilder, queryRoot, k, filters.get(k), false)));

		if (!CollectionUtils.isEmpty(restrictions)) {
			criteriaQuery.where(criteriaBuilder.and(restrictions.toArray(new Predicate[] {})));
		}

		applySort(criteriaBuilder, criteriaQuery, queryRoot, sort);

		return em.createQuery(criteriaQuery).setMaxResults(limit).getResultList();
	}

	@Override
	public List<T> findAll(Map<String, SortDirection> sort, int limit, int active) {
		if (limit < 0) {
			limit = MAX_RECORDS_LIMIT;
		}

		if (active > 1) {
			active = 1;
		}
		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
		final Root<T> queryRoot = criteriaQuery.from(getDomainClass());
		if (active < 0) {
			criteriaQuery.select(queryRoot);
		} else {
			if (hasActive()) {
				criteriaQuery.select(queryRoot).where(criteriaBuilder.equal(queryRoot.get("active"), active));
			}
		}

		applySort(criteriaBuilder, criteriaQuery, queryRoot, sort);

		return em.createQuery(criteriaQuery).setMaxResults(limit).getResultList();
	}

	/** {@inheritDoc} */
	@Override
	public T getById(ID id) {
		try {

			final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
			final Root<T> queryRoot = criteriaQuery.from(getDomainClass());
			criteriaQuery.select(queryRoot);
			final List<Predicate> predicates = new ArrayList<>();
			if (hasActive()) {
				predicates.add(criteriaBuilder.equal(queryRoot.get("active"), 1));
			}
			if (id != null) {
				predicates.add(criteriaBuilder.equal(queryRoot.get(id()), id));
			}
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

			return createQuery(criteriaQuery).getSingleResult();
		} catch (final Exception e) {
			_LOGGER.error("Error while fetching data by id : " + e.getMessage() + " :: Error : " + e);
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getByIds(List<ID> ids) {
		try {

			final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
			final Root<T> queryRoot = criteriaQuery.from(getDomainClass());
			criteriaQuery.select(queryRoot);
			final List<Predicate> predicates = new ArrayList<>();
			if (hasActive()) {
				predicates.add(criteriaBuilder.equal(queryRoot.get("active"), 1));
			}
			if (ids != null && !ids.isEmpty()) {
				predicates.add(criteriaBuilder.in(queryRoot.get(id())).value(ids));
			}
			criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

			return createQuery(criteriaQuery).getResultList();
		} catch (final Exception e) {
			_LOGGER.error("Error while fetching data by id : " + e.getMessage() + " :: Error : " + e);
			return null;
		}
	}

	private String id() {
		return DatabaseUtils.entityId(getDomainClass());
	}

	private TypedQuery<T> createQuery(CriteriaQuery<T> criteria) {
		final TypedQuery<T> query = em.createQuery(criteria);
		//		JPALoggingAspect.setQueryString(query.unwrap(org.hibernate.query.Query.class).getQueryString());
		return query;
	}

	/**
	 * Checks for active.
	 */
	private boolean hasActive() {
		return DatabaseUtils.hasActive(getDomainClass());
	}

	private void applySort(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<T> queryRoot, Map<String, SortDirection> sort) {
		if (!CollectionUtils.isEmpty(sort)) {
			final List<Order> order = sort.keySet().stream().map(t -> t != null && sort.get(t) != null && sort.get(t).equals(SortDirection.DESC) ?
					criteriaBuilder.desc(queryRoot.get(t)) :
					criteriaBuilder.asc(queryRoot.get(t))).collect(Collectors.toList());
			criteriaQuery.orderBy(order);
		}
	}

	private Predicate getRestriction(CriteriaBuilder criteriaBuilder, Root<T> queryRoot, String fieldName, Object value, boolean matchCase) {

		final String valueString = String.valueOf(value);

		if (value == null || "null".equals(valueString)) {
			return criteriaBuilder.isNull(queryRoot.get(fieldName));
		}

		if (value instanceof String) {
			if (valueString.equalsIgnoreCase("criteriaFilter:isNotNull")) {
				return criteriaBuilder.isNotNull(queryRoot.get(fieldName));
			}
			if (valueString.equalsIgnoreCase("criteriaFilter:isEmpty") || org.springframework.util.StringUtils.isEmpty(valueString)) {
				return criteriaBuilder.isEmpty(queryRoot.get(fieldName));
			}
		}

		final String dataType = (value instanceof FilterCriteria && ((FilterCriteria) value).getColumn() != null) ?
				DatabaseUtils.getDataType(getDomainClass(), ((FilterCriteria) value).getColumn()) :
				DatabaseUtils.getDataType(getDomainClass(), fieldName);

		Predicate predicate;
		if (checkFilterCriteria(value)) {
			predicate = getPredicateByFilterCriteria(criteriaBuilder, queryRoot, (FilterCriteria) value);
		} else if (dataType != null && dataType.equalsIgnoreCase("String") && !matchCase && !(value instanceof Collection)) {
			predicate = criteriaBuilder.equal(criteriaBuilder.lower(queryRoot.get(fieldName)),
					value instanceof String ? ((String) value).toLowerCase() : value);
		} else if (dataType != null && dataType.equalsIgnoreCase("Date")) {
			predicate = criteriaBuilder.equal(queryRoot.get(fieldName), value);
		} else if (dataType != null && value instanceof Collection) {
			final Collection<?> valueCollection = (Collection<?>) value;
			final Stream<?> batch = batches(valueCollection, FIND_BATCH_SIZE);
			final List<Predicate> predicateList = batch.map(lst -> queryRoot.get(fieldName).in((Collection<?>) lst)).collect(Collectors.toList());
			predicate = criteriaBuilder.or(predicateList.toArray(new Predicate[] {}));
		} else {
			predicate = criteriaBuilder.equal(queryRoot.get(fieldName), value);
		}

		return predicate;

	}

	private boolean checkFilterCriteria(Object value) {
		return (value instanceof FilterCriteria) && ((FilterCriteria) value).getValue() != null && ((FilterCriteria) value).getOperation() != null && ((FilterCriteria) value).getColumn() != null;
	}

	private Predicate getPredicateByFilterCriteria(CriteriaBuilder criteriaBuilder, Root<T> queryRoot, FilterCriteria filterCriteria) {
		Predicate predicate = null;
		if (filterCriteria.getOperation() == Operation.EQUALS) {
			predicate = criteriaBuilder.equal(queryRoot.get(filterCriteria.getColumn()), filterCriteria.getValue());
		} else if (filterCriteria.getOperation() == Operation.NOT_EQUALS) {
			predicate = criteriaBuilder.notEqual(queryRoot.get(filterCriteria.getColumn()), filterCriteria.getValue());
		} else if (filterCriteria.getOperation() == Operation.LIKE) {
			predicate = criteriaBuilder.like(queryRoot.get(filterCriteria.getColumn()), (String) filterCriteria.getValue());
		} else if (filterCriteria.getValue() instanceof Date || filterCriteria.getValue() instanceof Number) {
			Comparable<?> comparable = (Comparable<?>) filterCriteria.getValue();
			if (filterCriteria.getOperation() == Operation.GTE) {
				predicate = criteriaBuilder.greaterThanOrEqualTo(queryRoot.get(filterCriteria.getColumn()),
						criteriaBuilder.literal(filterCriteria.getValue()).as(comparable.getClass()));
			} else if (filterCriteria.getOperation() == Operation.LTE) {
				predicate = criteriaBuilder.lessThanOrEqualTo(queryRoot.get(filterCriteria.getColumn()),
						criteriaBuilder.literal(filterCriteria.getValue()).as(comparable.getClass()));
			} else if (filterCriteria.getOperation() == Operation.GT) {
				predicate = criteriaBuilder.greaterThan(queryRoot.get(filterCriteria.getColumn()),
						criteriaBuilder.literal(filterCriteria.getValue()).as(comparable.getClass()));
			} else if (filterCriteria.getOperation() == Operation.LT) {
				predicate = criteriaBuilder.lessThan(queryRoot.get(filterCriteria.getColumn()),
						criteriaBuilder.literal(filterCriteria.getValue()).as(comparable.getClass()));
			}
		} else {
			predicate = criteriaBuilder.equal(queryRoot.get(filterCriteria.getColumn()), filterCriteria.getValue());
		}
		return predicate;
	}

	private static <T> Stream<List<T>> batches(Collection<T> source, int length) {
		final int size = source.size();
		if (size <= 0) {
			return Stream.empty();
		}
		final List<T> listWithoutDuplicates = source.stream().distinct().collect(Collectors.toList());
		final int actSize = listWithoutDuplicates.size();
		final int fullChunks = (actSize - 1) / length;
		return IntStream.range(0, fullChunks + 1).mapToObj(n -> listWithoutDuplicates.subList(n * length, n == fullChunks ? actSize : (n + 1) * length));
	}

	@Override
	public PageAndSortResult<T> findAll(Map<String, Object> filters, Map<String, SortDirection> sort, int limit, int offset, int active) {
		if (limit < 0) {
			limit = MAX_RECORDS_LIMIT;
		}

		if (active > 1) {
			active = 1;
		}

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(getDomainClass());
		final Root<T> queryRoot = criteriaQuery.from(getDomainClass());

		final List<Predicate> restrictions = new ArrayList<>();

		if (active >= 0 && hasActive()) {
			restrictions.add(criteriaBuilder.equal(queryRoot.get("active"), active));
		}

		if (CollectionUtils.isNotEmpty(filters)) {
			filters.keySet().forEach(k -> restrictions.add(getRestriction(criteriaBuilder, queryRoot, k, filters.get(k), false)));
		}
		if (!CollectionUtils.isEmpty(restrictions)) {
			criteriaQuery.where(criteriaBuilder.and(restrictions.toArray(new Predicate[] {})));
		}

		applySort(criteriaBuilder, criteriaQuery, queryRoot, sort);

		return preparePageData(em, queryRoot, criteriaQuery, getDomainClass(), limit, offset);
	}

	/**
	 * <p>
	 * preparePageData.
	 * </p>
	 *
	 * @param em         a {@link javax.persistence.EntityManager} object.
	 * @param queryRoot  a {@link javax.persistence.criteria.Root} object.
	 * @param query      a {@link javax.persistence.criteria.CriteriaQuery} object.
	 * @param entityType a {@link java.lang.Class} object.
	 * @param request    a
	 *                   {@link com.milkbasket.core.framework.dbsupport.jdbc.pagination.PageAndSortRequest}
	 *                   object.
	 * @return a
	 *         {@link com.milkbasket.core.framework.dbsupport.jdbc.pagination.PageAndSortResult}
	 *         object.
	 */
	public PageAndSortResult<T> preparePageData(EntityManager em, Root<T> queryRoot, CriteriaQuery<T> query, Class<T> entityType, Integer pageSize,
			Integer pageNo) {

		final int maxPageSize = MAX_PAGE_SIZE;

		Assert.isTrue(!(pageNo != null && pageNo.intValue() < 1), "PageNo is null or less than 1. Minimum Value for pageNo is 1");
		Assert.isTrue(!(pageSize != null && pageSize.intValue() < 0), "PageSize is null or less than 1. Minimum value for pageSize is 0");
		Assert.isTrue(pageSize <= maxPageSize, "Page size can not be greater than " + maxPageSize + ". Found : " + pageSize);

		final int offset = (pageNo - 1) * pageSize;
		final List<T> dataList = em.createQuery(query).setFirstResult(offset).setMaxResults(pageSize).getResultList();

		final Long count = count(em, queryRoot, query.getRestriction(), entityType);

		final PageAndSortResult<T> result = new PageAndSortResult<>();
		result.setPageNo(pageNo);
		result.setPageSize(pageSize);

		result.setPages(pageSize > 0 ? count % pageSize > 0 ? count.intValue() / pageSize + 1 : count.intValue() / pageSize : 0);
		result.setTotal(count.intValue());
		result.setData(dataList);

		return result;
	}

	public Long count(EntityManager em, Root<T> root, Predicate predicate, Class<T> classType) {

		Assert.notNull(root.getAlias(), "Root Alias must be set");

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		final Root<T> queryRoot = criteriaQuery.from(classType);
		queryRoot.alias(root.getAlias());

		criteriaQuery.select(criteriaBuilder.count(queryRoot));

		if (predicate != null) {
			criteriaQuery.where(predicate);
		}
		return em.createQuery(criteriaQuery).getSingleResult();
	}

	@Override
	public Long count(final int active) {
		return count(null, active);
	}

	@Override
	public Long count(final Map<String, Object> filters) {
		return count(filters, 1);
	}

	@Override
	public Long count(final Map<String, Object> filters, final int active) {

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		final Root<T> queryRoot = criteriaQuery.from(getDomainClass());

		criteriaQuery.select(criteriaBuilder.count(queryRoot));

		final List<Predicate> restrictions = new ArrayList<>();
		if (active >= 0 && hasActive()) {
			restrictions.add(criteriaBuilder.equal(queryRoot.get("active"), active));
		}
		if (!CollectionUtils.isEmpty(filters)) {
			filters.keySet().forEach(k -> restrictions.add(getRestriction(criteriaBuilder, queryRoot, k, filters.get(k), false)));
		}
		if (!CollectionUtils.isEmpty(restrictions)) {
			criteriaQuery.where(criteriaBuilder.and(restrictions.toArray(new Predicate[] {})));
		}
		return em.createQuery(criteriaQuery).getSingleResult();
	}
}
