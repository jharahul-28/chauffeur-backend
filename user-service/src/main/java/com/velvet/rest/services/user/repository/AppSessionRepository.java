package com.velvet.rest.services.user.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.AppSessionEntity;

/**
 * The Interface AppSessionRepository.
 */
@Repository
public interface AppSessionRepository extends BaseCrudRepository<AppSessionEntity, Long> {

	AppSessionEntity findBySessionKey(String key);

	AppSessionEntity findByUserId(Long userId);

	List<AppSessionEntity> findByUserIdOrderByModifiedOnAsc(Long userId);
}
