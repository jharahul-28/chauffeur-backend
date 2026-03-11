package com.velvet.rest.services.fleet.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
import com.velvet.rest.services.fleet.errors.AppErrors;
import com.velvet.rest.services.fleet.repository.VehicleCategoryRepository;

/**
 * Service for VehicleCategory operations.
 */
@Service
public class VehicleCategoryService implements BaseService<VehicleCategoryEntity> {

	@Autowired
	private VehicleCategoryRepository vehicleCategoryRepository;

	@Transactional(readOnly = true)
	public List<VehicleCategoryEntity> getAllCategories() {
		return vehicleCategoryRepository.findAllActive();
	}

	@Transactional(readOnly = true)
	public VehicleCategoryEntity getById(Long id) {
		return findRecordById(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity createCategory(VehicleCategoryEntity category) {
		return vehicleCategoryRepository.save(category);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity updateBasePrice(Long id, BigDecimal basePrice) {
		VehicleCategoryEntity category = findRecordById(id);
		category.setBasePrice(basePrice);
		return vehicleCategoryRepository.save(category);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity toggleCategoryStatus(Long id) {
		VehicleCategoryEntity category = vehicleCategoryRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Category not found", "id")));
		category.setActive(category.getActive() == 1 ? 0 : 1);
		return vehicleCategoryRepository.save(category);
	}

	@Override
	public Class<VehicleCategoryEntity> getEntity() {
		return VehicleCategoryEntity.class;
	}

	@Override
	public BaseCrudRepository<VehicleCategoryEntity, ?> getRepository() {
		return vehicleCategoryRepository;
	}
}
