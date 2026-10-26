package com.velvet.rest.services.whatsapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.velvet.rest.services.whatsapp.entity.WhapiChatEntity;

@Repository
public interface WhapiChatRepository extends JpaRepository<WhapiChatEntity, Long> {

	Optional<WhapiChatEntity> findByWaChatId(String waChatId);
}
