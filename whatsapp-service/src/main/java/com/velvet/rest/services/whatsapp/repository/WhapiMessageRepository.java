package com.velvet.rest.services.whatsapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.velvet.rest.services.whatsapp.entity.WhapiMessageEntity;

public interface WhapiMessageRepository extends JpaRepository<WhapiMessageEntity, Long> {

	Optional<WhapiMessageEntity> findByWaMessageId(String waMessageId);

	List<WhapiMessageEntity> findByChat_IdOrderByMessageTimestampDesc(Long chatId);
}
