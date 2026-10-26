package com.velvet.rest.services.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;

import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.beans.ChauffeurBean;
import com.velvet.rest.services.user.beans.ChauffeurMetadata;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.errors.AppErrors;
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

	public ChauffeurEntity getById(Long id) {
		ChauffeurEntity chauffeur = findById(id);
		if (chauffeur == null) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.USER_NOT_FOUND, "Chauffeur not found with the provided Id", "id"));
		}
		return chauffeur;
	}

	public ChauffeurEntity getByPhone(String phone) {
		ChauffeurEntity chauffeur = findByPhone(phone);
		if (chauffeur == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "No chauffeur account found for this phone number. Please contact admin.", "phone"));
		}
		if (Boolean.TRUE.equals(chauffeur.getBlocked())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_BLOCKED, "User is blocked. Please contact support.", "blocked"));
		}
		return chauffeur;
	}

	public ChauffeurEntity save(ChauffeurEntity entity) {
		return chauffeurRepository.save(entity);
	}

	public PageAndSortResult<ChauffeurEntity> findAllByPage(Integer pageSize, Integer pageNo, Map<String, Object> filters, Map<String, SortDirection> sort) {
		if (CollectionUtils.isEmpty(filters)) {
			filters = new HashMap<>();
		}
		try {
			return findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
	}

	public ChauffeurEntity findByPhone(String phone) {
		return chauffeurRepository.findByPhone(phone);
	}

	public Boolean verifyOtp(Long userId, String phone, String otp) {
		return otpService.verifyPhoneOtp(userId, phone, otp);
	}

	@Transactional()
	public ChauffeurEntity updateChauffeur(Long id, ChauffeurBean request) {
		ChauffeurEntity chauffeur = findById(id);
		if (chauffeur == null) {
			throw new ValidationException(ErrorBean.withError("USER_NOT_FOUND", "Chauffeur not found", "id"));
		}
		chauffeur.setFirstName(request.getFirstName());
		chauffeur.setLastName(request.getLastName());
		chauffeur.setEmail(request.getEmail());
		chauffeur.setGender(request.getGender());
		if (request.getLicenseNumber() != null) {
			chauffeur.setLicenseNumber(request.getLicenseNumber());
		}
		if (request.getDateOfBirth() != null) {
			chauffeur.setDateOfBirth(request.getDateOfBirth());
		}
		if (request.getDocuments() != null) {
			chauffeur.setDocuments(request.getDocuments());
		}
		if (request.getRating() != null) {
			chauffeur.setRating(request.getRating());
		}
		if (request.getMaritalStatus() != null) {
			chauffeur.setMaritalStatus(request.getMaritalStatus());
		}
		if (request.getDlExpiryDate() != null) {
			chauffeur.setDlExpiryDate(request.getDlExpiryDate());
		}
		if (request.getHasCriminalRecord() != null) {
			chauffeur.setHasCriminalRecord(request.getHasCriminalRecord());
		}
		if (request.getAddress() != null) {
			chauffeur.setAddress(request.getAddress());
		}
		if (request.getBankDetails() != null) {
			chauffeur.setBankDetails(request.getBankDetails());
		}
		if (request.getMetadata() != null) {
			if (chauffeur.getMetadata() == null) {
				chauffeur.setMetadata(new ChauffeurMetadata());
			}
			if (request.getMetadata().getWorkingHours() != null) {
				chauffeur.getMetadata().setWorkingHours(request.getMetadata().getWorkingHours());
			}
			if (request.getMetadata().getDrivingStartYear() != null) {
				chauffeur.getMetadata().setDrivingStartYear(request.getMetadata().getDrivingStartYear());
			}
			if (request.getMetadata().getLuxuryDrivingStartYear() != null) {
				chauffeur.getMetadata().setLuxuryDrivingStartYear(request.getMetadata().getLuxuryDrivingStartYear());
			}
			if (request.getMetadata().getLuxuryBrandsDriven() != null) {
				chauffeur.getMetadata().setLuxuryBrandsDriven(request.getMetadata().getLuxuryBrandsDriven());
			}
		}
		return save(chauffeur);
	}

	@Transactional()
	public ChauffeurEntity createChauffeur(String phone, String userType, ChauffeurBean chauffeurBean) {
		if (StringUtils.isBlank(phone)) {
			throw new ValidationException(ErrorBean.withError("INVALID_PHONE", "Phone number is required", "phone"));
		}
		try {
			ChauffeurEntity chauffeur = userService.registerUser(phone, userType, ChauffeurEntity.newInstance());
			chauffeur.setAllowedSessions(ParamsUtils.getIntegerParam("CHAUFFEUR_ALLOWED_SESSIONS", 1));
			chauffeur = save(chauffeur);
			return save(chauffeur.createChauffeur(chauffeurBean));
		} catch (DataIntegrityViolationException e) {
			throw new ValidationException(ErrorBean.withError(
				"PHONE_ALREADY_EXISTS",
				"A chauffeur with this phone number already exists",
				"phone"
			));
		}
	}

	@Override
	public ChauffeurEntity getAuthEntity() {
		final Long id = userService.getSessionUserId();
		return getById(id);
	}

	@Override
	public void logout() {
		final Long id = userService.getSessionUserId();
		sessionService.doLogout(id);
	}

	public void logoutById(Long id) {
		sessionService.doLogoutByUserId(id);
	}

	@Transactional()
	public ChauffeurEntity toggleBlock(Long id) {
		ChauffeurEntity chauffeur = getById(id);
		boolean isBlocked = chauffeur.getBlocked();
		chauffeur.setBlocked(!isBlocked);
		save(chauffeur);
		if (!isBlocked) {
			logoutById(id);
		}
		return chauffeur;
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
