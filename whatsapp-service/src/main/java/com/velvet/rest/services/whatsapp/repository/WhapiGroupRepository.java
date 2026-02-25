package com.velvet.rest.services.whatsapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.velvet.rest.services.whatsapp.entity.WhapiGroupEntity;

@Repository
public interface WhapiGroupRepository extends JpaRepository<WhapiGroupEntity, Long> {

	Optional<WhapiGroupEntity> findByWaGroupId(String waGroupId);


}
