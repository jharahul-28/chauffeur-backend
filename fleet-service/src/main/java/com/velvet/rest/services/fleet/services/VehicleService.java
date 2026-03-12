package com.velvet.rest.services.fleet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.ValidationException;
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

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleEntity createVehicle(VehicleBean vehicleBean) {
		VehicleCategoryEntity vehicleCategory = vehicleCategoryRepository.findById(vehicleBean.getCategoryId())
				.orElseThrow(() -> new ValidationException(
						ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Vehicle category not found", "categoryId")));
		if (category.getActive() != 1) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.CATEGORY_INACTIVE, "Vehicle category is inactive", "categoryId"));
		}
		return vehicleRepository.save(VehicleEntity.newInstance().createNewVehicle(bean, category));
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
