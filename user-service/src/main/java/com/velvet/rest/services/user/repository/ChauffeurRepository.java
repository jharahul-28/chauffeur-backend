package com.velvet.rest.services.user.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.ChauffeurEntity;

/**
 * The Interface ChauffeurRepository.
 */
@Repository
public interface ChauffeurRepository extends BaseCrudRepository<ChauffeurEntity, Long> {

	ChauffeurEntity findByPhoneAndIsDeleted(String phone, Boolean isDeleted);

	default ChauffeurEntity findByPhone(String phone) {
		return findByPhoneAndIsDeleted(phone, false);
	}
}
