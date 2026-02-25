package com.velvet.rest.services.whatsapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.velvet.rest.services.whatsapp.entity.WhapiGroupParticipantEntity;

public interface WhapiGroupParticipantRepository extends JpaRepository<WhapiGroupParticipantEntity, Long> {

	void deleteByGroupId(Long groupId);

	List<WhapiGroupParticipantEntity> findByGroupId(Long groupId);

	Optional<WhapiGroupParticipantEntity> findByGroupIdAndWaUserId(Long groupId, String waUserId);

	long countByGroupIdAndLeftAtTsIsNull(Long groupId);
}
