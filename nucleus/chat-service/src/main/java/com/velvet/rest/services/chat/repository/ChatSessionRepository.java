package com.velvet.rest.services.chat.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.chat.entity.ChatSessionEntity;

public interface ChatSessionRepository extends BaseCrudRepository<ChatSessionEntity, Long> {

	@Query("SELECT s FROM ChatSessionEntity s WHERE s.phone = :phone AND s.createdOn >= :since ORDER BY s.modifiedOn DESC")
	List<ChatSessionEntity> findRecentByPhone(@Param("phone") String phone, @Param("since") Date since);

}
