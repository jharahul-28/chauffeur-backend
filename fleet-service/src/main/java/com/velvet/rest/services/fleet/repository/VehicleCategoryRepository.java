package com.velvet.rest.services.fleet.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.fleet.entity.VehicleCategoryConstants.CategoryType;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;

import java.util.List;
import java.util.UUID;

/**
 * The Interface VehicleCategoryRepository.
 */
@Repository
public interface VehicleCategoryRepository extends BaseCrudRepository<VehicleCategoryEntity, UUID> {

	List<VehicleCategoryEntity> findByActiveOrderByDisplayOrderAsc(Integer active);

	VehicleCategoryEntity findByType(CategoryType type);

	default List<VehicleCategoryEntity> findAllActive() {
		return findByActiveOrderByDisplayOrderAsc(1);
	}
}
