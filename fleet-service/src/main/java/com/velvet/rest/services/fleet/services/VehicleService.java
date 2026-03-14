package com.velvet.rest.services.fleet.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
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
import com.velvet.rest.services.fleet.beans.VehicleUpdateRequest;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.entity.VehicleEntity;
import com.velvet.rest.services.fleet.errors.AppErrors;
import com.velvet.rest.services.fleet.repository.VehicleRepository;

/**
 * Service for Vehicle operations.
 */
@Service
public class VehicleService implements BaseService<VehicleEntity> {

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private VehicleCategoryService vehicleCategoryService;

	
	public VehicleEntity getById(Long id) {
		return findRecordById(id);
	}

	public List<VehicleEntity> getByCategory(Long categoryId) {
		return vehicleRepository.findByCategory_Id(categoryId);
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
	public VehicleEntity updateVehicle(Long id, VehicleUpdateRequest request) {
		VehicleEntity vehicle = vehicleRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.VEHICLE_NOT_FOUND, "Vehicle not found", "id")));
				
		if (request.getCategoryId() != null && ObjectUtils.notEqual(request.getCategoryId(), vehicle.getCategoryId())) {
			VehicleCategoryEntity newCategory = vehicleCategoryService.validateCategory(request.getCategoryId());
			vehicle.setCategory(newCategory);
			vehicle.setCategoryId(newCategory.getId());
		}
		vehicle.setVehicleNumber(request.getVehicleNumber());
		vehicle.setBrand(request.getBrand());
		vehicle.setModel(request.getModel());
		vehicle.setSeatingCapacity(request.getSeatingCapacity());
		vehicle.setFuelType(request.getFuelType());
		vehicle.setTransmission(request.getTransmission());
		if (request.getAvailable() != null) {
			vehicle.setAvailable(request.getAvailable());
		}
		if (request.getMetadata() != null) {
			vehicle.setMetadata(request.getMetadata());
		}
		if (request.getImageUrl() != null) {
			vehicle.setImageUrl(request.getImageUrl());
		}
		return vehicleRepository.save(vehicle);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleEntity createVehicle(VehicleBean vehicleBean) {
		return vehicleRepository.save(VehicleEntity.newInstance().createNewVehicle(vehicleBean, vehicleCategoryService.validateCategory(vehicleBean.getCategoryId())));
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
