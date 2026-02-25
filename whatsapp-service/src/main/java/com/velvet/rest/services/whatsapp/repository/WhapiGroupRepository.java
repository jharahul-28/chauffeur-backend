package com.velvet.rest.services.whatsapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;

@Repository
public interface WhapiGroupRepository extends JpaRepository<WhapiGroupEntity, Long> {

	Optional<WhapiGroupEntity> findByWaGroupId(String waGroupId);

	WhapiGroupEntity findByOrgId(UUID orgId);

	List<WhapiGroupEntity> findByOrgIdNotNull();
}
