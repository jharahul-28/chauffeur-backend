package com.velvet.rest.services.user.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.AuthUserEntity;

/**
 * The Interface AuthUserRepository.
 */
@Repository
public interface AuthUserRepository extends BaseCrudRepository<AuthUserEntity, Long> {

	@Query("SELECT au FROM AuthUserEntity au JOIN CustomerEntity c ON au.id = c.id WHERE au.providerId = :providerId AND c.isDeleted = false")
	AuthUserEntity findByProviderIdAndCustomerNotDeleted(@Param("providerId") String providerId);

	default AuthUserEntity findByProviderId(String providerId) {
		return findByProviderIdAndCustomerNotDeleted(providerId);
	}

}
