package com.velvet.rest.services.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.chat.entity.ChatMessageEntity;

public interface ChatMessageRepository extends BaseCrudRepository<ChatMessageEntity, Long> {

	@Query("SELECT m FROM ChatMessageEntity m WHERE m.session.id = :sessionId AND m.active = 1 ORDER BY m.createdOn ASC")
	List<ChatMessageEntity> findBySessionId(@Param("sessionId") Long sessionId);

	@Query(value = "SELECT * FROM chat_messages m WHERE m.session_id = :sessionId AND m.active = 1 ORDER BY m.created_on DESC LIMIT :limit", nativeQuery = true)
	List<ChatMessageEntity> findLastNMessages(@Param("sessionId") Long sessionId, @Param("limit") int limit);

	List<ChatMessageEntity> findTop20BySessionIdOrderByCreatedOnDesc(Long sessionId);

}
