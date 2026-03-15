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
import com.velvet.rest.services.fleet.beans.VehicleCategoryBean;
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

	public List<VehicleCategoryEntity> getAllCategories() {
		return vehicleCategoryRepository.findAllActive();
	}

	public VehicleCategoryEntity getById(Long id) {
		return findRecordById(id);
	}

	public VehicleCategoryEntity validateCategory(Long categoryId) {
		VehicleCategoryEntity category = vehicleCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Vehicle category not found", "categoryId")));
		if (category.getActive() != 1) {
			throw new ValidationException(ErrorBean.withError(AppErrors.CATEGORY_INACTIVE, "Vehicle category is inactive", "categoryId"));
		}
		return category;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity createCategory(VehicleCategoryBean request) {
		VehicleCategoryEntity category = new VehicleCategoryEntity();
		category.setCategoryName(request.getCategoryName());
		category.setBasePrice(request.getBasePrice());
		category.setActive(1);
		return vehicleCategoryRepository.save(category);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public VehicleCategoryEntity updateCategory(Long id, VehicleCategoryBean request) {
		VehicleCategoryEntity category = vehicleCategoryRepository.findById(id)
				.orElseThrow(() -> new ValidationException(ErrorBean.withError(AppErrors.CATEGORY_NOT_FOUND, "Category not found", "id")));
		category.setCategoryName(request.getCategoryName());
		category.setBasePrice(request.getBasePrice());
		if (request.getImageUrl() != null) {
			category.setImageUrl(request.getImageUrl());
		}
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
