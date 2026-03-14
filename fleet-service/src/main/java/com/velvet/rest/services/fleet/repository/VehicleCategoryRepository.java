package com.velvet.rest.services.fleet.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.fleet.entity.VehicleCategoryEntity;

import java.util.List;

/**
 * The Interface VehicleCategoryRepository.
 */
@Repository
public interface VehicleCategoryRepository extends BaseCrudRepository<VehicleCategoryEntity, Long> {

	List<VehicleCategoryEntity> findByActive(Integer active);

	default List<VehicleCategoryEntity> findAllActive() {
		return findByActive(1);
	}
}
