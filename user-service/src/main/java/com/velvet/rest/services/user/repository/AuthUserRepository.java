package com.velvet.rest.services.user.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.AuthUserEntity;

/**
 * The Interface AuthUserRepository.
 */
@Repository
public interface AuthUserRepository extends BaseCrudRepository<AuthUserEntity, Long> {

}
