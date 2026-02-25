package com.velvet.rest.services.user.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.entity.AppUserEntity;

/**
 * The Interface AppUserRepository.
 */
@Repository
public interface AppUserRepository extends BaseCrudRepository<AppUserEntity, Long> {

	AppUserEntity findByPhoneAndIsDeleted(String phone, Boolean isDeleted);

	AppUserEntity findByEmailAndIsDeleted(String email, Boolean isDeleted);

	AppUserEntity findByReferralCodeAndIsDeleted(String referralCode, Boolean isDeleted);

	// For backward compatibility - these will now exclude deleted users
	default AppUserEntity findByPhone(String phone) {
		return findByPhoneAndIsDeleted(phone, false);
	}

	default AppUserEntity findByEmail(String email) {
		return findByEmailAndIsDeleted(email, false);
	}

	default AppUserEntity findByReferralCode(String referralCode) {
		return findByReferralCodeAndIsDeleted(referralCode, false);
	}

	@Query("SELECT u FROM AppUserEntity u WHERE u.status = 2 AND (u.welcomeMessageSent = false OR u.welcomeMessageSent IS NULL) AND u.isDeleted = false AND u.modifiedOn <= :cutoff")
	List<AppUserEntity> findPendingWelcomeUsers(@Param("cutoff") Date cutoff);
}
