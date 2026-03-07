package com.velvet.rest.services.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.repository.ChauffeurRepository;

/**
 * Service for Chauffeur operations.
 */
@Service
public class ChauffeurService implements UserService, BaseService<ChauffeurEntity> {

	@Autowired
	private ChauffeurRepository chauffeurRepository;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private AuthUserService userService;

	@Autowired
	private AppSessionService sessionService;

	public ChauffeurEntity findById(Long id) {
		return findRecordById(id);
	}

	public ChauffeurEntity save(ChauffeurEntity entity) {
		return chauffeurRepository.save(entity);
	}

	public ChauffeurEntity findByPhone(String phone) {
		return chauffeurRepository.findByPhone(phone);
	}

	public Boolean verifyOtp(Long userId, String phone, String otp) {
		return otpService.verifyPhoneOtp(userId, phone, otp);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ChauffeurEntity createChauffeur(String phone, String userType) {
		ChauffeurEntity chauffeur = (ChauffeurEntity) userService.registerAppUser(phone, userType, ChauffeurEntity.newInstance());
		chauffeur.setAllowedSessions(ParamsUtils.getIntegerParam("CHAUFFEUR_ALLOWED_SESSIONS", 1));
		return save(chauffeur);
	}

	@Override
	public ChauffeurEntity getAuthEntity() {
		final Long id = userService.getSessionUserId();
		return findById(id);
	}

	@Override
	public void logout() {
		final Long id = userService.getSessionUserId();
		sessionService.doLogout(id);
	}

	@Override
	public Class<ChauffeurEntity> getEntity() {
		return ChauffeurEntity.class;
	}

	@Override
	public BaseCrudRepository<?, ?> getRepository() {
		return chauffeurRepository;
	}
}
