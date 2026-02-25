package com.velvet.rest.services.user.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.AdminUserEntity;

/**
 * The Interface AdminUserRepository.
 */
@Repository
public interface AdminUserRepository extends BaseCrudRepository<AdminUserEntity, Long> {

	AdminUserEntity findByPhone(String phone);

	AdminUserEntity findByEmail(String email);

	int countByPhoneOrEmail(String phone, String email);
}
