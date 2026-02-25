package com.velvet.rest.services.user.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.GroupEntity;

/**
 * The Interface GroupRepository.
 */
@Repository
public interface GroupRepository extends BaseCrudRepository<GroupEntity, Long> {

	GroupEntity findByName(String name);
}
