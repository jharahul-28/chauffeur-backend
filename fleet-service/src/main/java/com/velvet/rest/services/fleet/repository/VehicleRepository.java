package com.velvet.rest.services.fleet.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.fleet.entity.VehicleEntity;

/**
 * The Interface VehicleRepository.
 */
@Repository
public interface VehicleRepository extends BaseCrudRepository<VehicleEntity, Long> {

	List<VehicleEntity> findByCategory_Id(Long categoryId);
}
