package com.velvet.rest.services.fleet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.fleet.beans.VehicleBean;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.fleet.errors.AppErrors;
import com.velvet.rest.services.fleet.repository.VehicleCategoryRepository;
import com.velvet.rest.services.fleet.repository.VehicleRepository;

/**
 * Service for Vehicle operations.
 */
@Service
public class VehicleService implements BaseService<VehicleEntity> {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private VehicleCategoryRepository vehicleCategoryRepository;

	
	public VehicleEntity getById(Long id) {
		return findRecordById(id);
	}

	public List<VehicleEntity> getByCategory(Long categoryId) {
		return vehicleRepository.findByCategoryId(categoryId);
	}

	public PageAndSortResult<VehicleEntity> findAllByPage(Integer pageSize, Integer pageNo, Map<String, Object> filters, Map<String, SortDirection> sort) {
		if (CollectionUtils.isEmpty(filters)) {
			filters = new HashMap<>();
		}
		try {
			return findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleEntity createVehicle(VehicleBean vehicleBean) {
		VehicleCategoryEntity vehicleCategory = vehicleCategoryRepository.findById(vehicleBean.getCategoryId())
				.orElseThrow(() -> new ValidationException(
						ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Vehicle category not found", "categoryId")));
		if (vehicleCategory.getActive() != 1) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.CATEGORY_INACTIVE, "Vehicle category is inactive", "categoryId"));
		}
		return vehicleRepository.save(VehicleEntity.newInstance().createNewVehicle(vehicleBean, vehicleCategory));
	}

	@Override
	public Class<VehicleEntity> getEntity() {
		return VehicleEntity.class;
	}

	@Override
	public BaseCrudRepository<VehicleEntity, ?> getRepository() {
		return vehicleRepository;
	}
}
