package com.velvet.rest.services.user.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.CustomerEntity;

/**
 * The Interface CustomerRepository.
 */
@Repository
public interface CustomerRepository extends BaseCrudRepository<CustomerEntity, Long> {

	CustomerEntity findByPhoneAndIsDeleted(String phone, Boolean isDeleted);

	CustomerEntity findByEmailAndIsDeleted(String email, Boolean isDeleted);

	CustomerEntity findByReferralCodeAndIsDeleted(String referralCode, Boolean isDeleted);

	// For backward compatibility - these will now exclude deleted users
	default CustomerEntity findByPhone(String phone) {
		return findByPhoneAndIsDeleted(phone, false);
	}

	default CustomerEntity findByEmail(String email) {
		return findByEmailAndIsDeleted(email, false);
	}

	default CustomerEntity findByReferralCode(String referralCode) {
		return findByReferralCodeAndIsDeleted(referralCode, false);
	}

	@Query("SELECT u FROM CustomerEntity u WHERE u.status = 2 AND (u.welcomeMessageSent = false OR u.welcomeMessageSent IS NULL) AND u.isDeleted = false AND u.modifiedOn <= :cutoff")
	List<CustomerEntity> findPendingWelcomeUsers(@Param("cutoff") Date cutoff);
}
