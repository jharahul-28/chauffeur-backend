package com.velvet.rest.services.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.GroupEntity;
import com.velvet.rest.services.user.entity.GroupPrivilegeConstants;
import com.velvet.rest.services.user.entity.GroupPrivilegeEntity;

/**
 * The Interface GroupPrivilegeRepository.
 */
@Repository
public interface GroupPrivilegeRepository extends BaseCrudRepository<GroupPrivilegeEntity, Long> {

	/**
	 * Delete by group.
	 *
	 * @param group
	 */
	@Modifying
	@Query(GroupPrivilegeConstants.Query.DELETE_BY_GROUP)
	void deleteByGroup(GroupEntity group);
}
