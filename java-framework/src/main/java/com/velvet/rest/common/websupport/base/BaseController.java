/*
 * @author Mohit
 */
package com.velvet.rest.common.websupport.base;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.common.utils.ContextUtils;
import com.velvet.rest.common.utils.DatabaseUtils;
import com.velvet.rest.common.utils.SessionUtils;
import com.velvet.rest.services.common.mapper.BaseMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Interface BaseController.
 *
 * @author Mohit
 * @version $Id: $Id
 */

public interface BaseController {

	AppLogger _LOGGER = LoggingManager.getLogger(BaseController.class);

	default BindingResult getSpringErrors(Object paramValue) {
		final DataBinder dataBinder = new DataBinder(paramValue, paramValue.getClass().getSimpleName());
		dataBinder.addValidators(new SpringValidatorAdapter(ContextUtils.getBean(LocalValidatorFactoryBean.class)));
		dataBinder.validate(new Object[] {});
		return dataBinder.getBindingResult();
	}

	public BaseMapper<?, ?> getMapper();

	default void checkError(org.springframework.validation.Errors errors) {
		if (errors.hasErrors()) {
			throw new ValidationException(CommonConstants.VALIDATION_ERROR_MSG, errors);
		}
	}

	default String removeCountryCode(String phone, String source) {
		if (phone != null && phone.startsWith(CommonConstants.DEFAULT_COUNTRY_CODE)) {
			_LOGGER.info(String.format("Base Controller : removeCountryCode from source: %s", source));
			return phone.substring(3);
		}
		return phone;
	}

	default Map<String, Object> getSearchParams(HttpServletRequest request, Class<?> entityType) {
		final Map<String, Object> params = new HashMap<>();
		try {
			final Map<String, String[]> paramsMap = request.getParameterMap();
			for (final String param : paramsMap.keySet()) {
				if (nonIgnoringParams(param)) {
					params.put(param, DatabaseUtils.getTypedValue(entityType, param,
							paramsMap.get(param) != null && paramsMap.get(param).length > 0 ? paramsMap.get(param)[0] : null));
				}
			}
		} catch (final ValidationException e) {
			throw new ValidationException(ErrorBean.withError(Errors.ILLEGAL_PARAM, e.getField()));
		} catch (final Exception e) {
			throw new ValidationException(ErrorBean.withError(Errors.ILLEGAL_PARAM));
		}
		return params;
	}

	default boolean nonIgnoringParams(String param) {
		return !param.equalsIgnoreCase("resolve") && !param.equalsIgnoreCase("pageNo") && !param.equalsIgnoreCase("pageSize")
				&& !param.equalsIgnoreCase("sortBy") && !param.equalsIgnoreCase("sortDirection");
	}

	default Map<String, SortDirection> buildSortMap(String sortBy, SortDirection sortDirection) {
		Map<String, SortDirection> sort = new HashMap<>();
		if (sortDirection == null) {
			sortDirection = SortDirection.ASC;
		}
		sort.put(sortBy, sortDirection);
		return sort;
	}

	default <E, T> PageAndSortResult<T> prepareResponsePageData(PageAndSortResult<E> sourceEntity, Class<T> clazz) {
		PageAndSortResult<T> result = new PageAndSortResult<T>();
		result.setPageNo(sourceEntity.getPageNo());
		result.setPages(sourceEntity.getPages());
		result.setPageSize(sourceEntity.getPageSize());
		result.setTotal(sourceEntity.getTotal());
		List<T> resultList = getMapper().mapAsList(sourceEntity.getData(), clazz);
		result.setData(resultList);
		return result;
	}
}
