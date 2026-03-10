package com.velvet.rest.services.fleet.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.fleet.entity.VehicleCategoryConstants.CategoryType;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;
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
	public VehicleCategoryEntity getById(UUID id) {
		return findRecordById(id);
	}

	@Transactional(readOnly = true)
	public VehicleCategoryEntity getByType(CategoryType type) {
		return vehicleCategoryRepository.findByType(type);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity createCategory(VehicleCategoryEntity category) {
		return vehicleCategoryRepository.save(category);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity updateBasePrice(UUID id, BigDecimal basePrice) {
		VehicleCategoryEntity category = findRecordById(id);
		category.setBasePrice(basePrice);
		return vehicleCategoryRepository.save(category);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void inactivateCategory(UUID id) {
		VehicleCategoryEntity category = findRecordById(id);
		category.setActive(0);
		vehicleCategoryRepository.save(category);
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
