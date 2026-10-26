package com.velvet.rest.services.user.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.user.constants.UserConstants.OtpType;
import com.velvet.rest.services.user.entity.OtpTokenEntity;

/**
 * The Interface OtpTokenRepository.
 */
@Repository
public interface OtpTokenRepository extends BaseCrudRepository<OtpTokenEntity, Long> {

	OtpTokenEntity findByUserIdAndReferenceId(Long userId, String email);

	OtpTokenEntity findByUserIdAndOtpTypeAndReferenceId(Long userId, OtpType type, String referenceId);

	List<OtpTokenEntity> findByOtpTypeAndReferenceIdOrderByIdDesc(OtpType type, String referenceId);

}
