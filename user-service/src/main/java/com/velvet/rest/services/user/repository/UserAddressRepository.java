package com.velvet.rest.services.user.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.UserAddressEntity;

/**
 * Repository interface for UserAddress entity.
 *
 * @author mohit
 */
@Repository
public interface UserAddressRepository extends BaseCrudRepository<UserAddressEntity, Integer> {

	UserAddressEntity findByUserId(Long userId);

	List<UserAddressEntity> findByOrgId(UUID orgId);

	List<UserAddressEntity> findByOrgAddressId(Integer orgAddressId);

	UserAddressEntity findByUserIdAndOrgAddressId(Long userId, Integer orgAddressId);

}
