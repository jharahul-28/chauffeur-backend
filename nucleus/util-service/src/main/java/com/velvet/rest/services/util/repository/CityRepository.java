package com.velvet.rest.services.util.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.util.entity.CityEntity;

@Repository
public interface CityRepository extends BaseCrudRepository<CityEntity, Long> {

	@Query("SELECT c FROM CityEntity c WHERE c.active = 1 AND LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY c.name")
	List<CityEntity> findByNameStartingWithIgnoreCase(@Param("query") String query);

	@Query("SELECT c FROM CityEntity c WHERE c.active = 1 AND LOWER(c.name) = LOWER(:name)")
	CityEntity findByNameIgnoreCase(@Param("name") String name);

}