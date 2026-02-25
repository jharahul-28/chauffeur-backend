package com.velvet.rest.services.user.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ServerException;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.common.upload.UploadService;
import com.velvet.rest.services.common.upload.UploadServiceUtil;
import com.velvet.rest.services.user.beans.AppUserInfoRequest;
import com.velvet.rest.services.user.constants.UserConstants.Gender;
import com.velvet.rest.services.user.entity.AppUserEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.AppUserRepository;
import com.velvet.rest.services.user.utils.AuthUtils;

/**
 * Created by mohit on 27.4.20.
 */
@Service
public class AppUserService implements UserService, BaseService<AppUserEntity> {

	private static final AppLogger logger = LoggingManager.getLogger(AppUserService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private AuthUserService userService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private UploadService uploadService;

	public AppUserEntity findById(Long id) {
		AppUserEntity result = findRecordById(id);
		return result;
	}

	public AppUserEntity save(AppUserEntity entity) {
		AppUserEntity result = appUserRepository.save(entity);
		return result;
	}

	public Iterable<AppUserEntity> saveAll(List<AppUserEntity> entities) {
		Iterable<AppUserEntity> result = appUserRepository.saveAll(entities);
		return result;
	}

	public List<AppUserEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	public PageAndSortResult<AppUserEntity> findAllByPage(Integer pageSize, Integer pageNo, Map<String, Object> filters, Map<String, SortDirection> sort) {
		PageAndSortResult<AppUserEntity> appUserList = null;
		if (CollectionUtils.isEmpty(filters)) {
			filters = new HashMap<>();
		}
		try {
			appUserList = findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			logger.error(e);
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
		return appUserList;
	}

	@Override
	public Class<AppUserEntity> getEntity() {
		return AppUserEntity.class;
	}

	@Override
	public BaseCrudRepository<?, ?> getRepository() {
		return appUserRepository;
	}

	public AppUserEntity findByPhone(String phone) {
		AppUserEntity appUser = appUserRepository.findByPhone(phone);
		return appUser;
	}

	public AppUserEntity findByEmail(String email) {
		AppUserEntity appUser = appUserRepository.findByEmail(email);
		return appUser;
	}

	public Boolean verifyOtp(Long userId, String phone, String otp) {
		return otpService.verifyPhoneOtp(userId, phone, otp);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AppUserEntity createAppUser(String phone, String userType) {
		AppUserEntity appUser = (AppUserEntity) userService.registerAppUser(phone, userType, AppUserEntity.newInstance());
		appUser.setAllowedSessions(ParamsUtils.getIntegerParam("APP_USER_ALLOWED_SESSIONS", 2));
		return save(appUser);
	}

	public void generateReferralCode(AppUserEntity appUser) {
		String referralCode = null;
		do {
			referralCode = AuthUtils.generateReferralCode();
			AppUserEntity otherAppUser = appUserRepository.findByReferralCode(referralCode);
			logger.info(String.format("AppUserService :: generateRefferrerCode method :: referralCode %s", referralCode));
			if (Objects.isNull(otherAppUser) || !Objects.equals(referralCode, otherAppUser.getReferralCode())) {
				break;
			}
		} while (true);

		appUser.setReferralCode(referralCode);
		save(appUser);
	}

	@Override
	public AppUserEntity getAuthEntity() {
		final Long id = userService.getSessionUserId();
		AppUserEntity entity = findById(id);
		return entity;
	}

	public AppUserEntity updateVerifiedEmail(String email) {
		AppUserEntity appUser = getAuthEntity();
		appUser.setEmail(email);
		appUser.setEmailVerified(true);
		appUser = save(appUser);
		return appUser;
	}

	public AppUserEntity updateVerifiedPhone(String phone, String countryCode) {
		AppUserEntity appUser = getAuthEntity();
		appUser.setCountryCode(countryCode);
		appUser.setPhone(phone);
		appUser.setPhoneVerified(true);
		appUser = save(appUser);
		return appUser;
	}

	public AppUserEntity markPhoneVerified(AppUserEntity appUser) {
		appUser.setPhoneVerified(true);
		appUser = save(appUser);
		return appUser;
	}

	public AppUserEntity updateInfo(AppUserEntity entity, String name, String email, Gender gender, Integer status) {
		boolean saving = false;
		if (StringUtils.isNotEmpty(name)) {
			entity.setName(name.trim());
			saving = true;
		}
		if (StringUtils.isNotEmpty(email)) {
			if (!email.trim().equals(entity.getEmail())) {
				entity.setEmailVerified(false);
			}
			entity.setEmail(email.trim());
			saving = true;
		}
		if (gender != null) {
			entity.setGender(gender);
			saving = true;
		}
		if (status != null) {
			entity.setStatus(status);
			saving = true;
		}
		if (saving) {
			save(entity);
		}
		return entity;
	}

	public AppUserEntity updateInfo(AppUserEntity entity, @Valid AppUserInfoRequest userInfoRequest, Integer status) {
		return updateInfo(entity, userInfoRequest.getName(), userInfoRequest.getEmail(), userInfoRequest.getGender(), status);
	}

	public AppUserEntity updateStatus(AppUserEntity entity, Integer status) {
		if (status != null) {
			entity.setStatus(status);
		}
		return save(entity);
	}

	@Override
	public void logout() {
		final Long id = userService.getSessionUserId();
		sessionService.doLogout(id);
	}

	public String uploadProfilePic(MultipartFile file) {
		Assert.notNull(file, "File cannot be empty");

		AppUserEntity entity = getAuthEntity();
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "User not found", "id"));
		}

		String bucketName = ParamsUtils.getParam("S3_FILES_BUCKET");
		String subdirectory = null;
		subdirectory = ParamsUtils.getParam("S3_PROFILE_SUBDIRECTORY");
		String fileExt = uploadService.findFileExt(file.getOriginalFilename());
		String filename = String.format("%s-%s.%s", entity.getId(), System.currentTimeMillis(), fileExt);
		String fileUrl = validateAndUploadFile(file, bucketName, subdirectory, filename);

		entity.setProfilePic(fileUrl);
		save(entity);
		return fileUrl;
	}

	private String validateAndUploadFile(MultipartFile file, String bucketName, String subdirectory, String fileName) {
		String response = null;
		try {
			if (UploadServiceUtil.isValidImgFile(file)) {
				byte[] bytes = file.getBytes();
				response = (String) uploadService.uploadFile(bucketName, subdirectory, bytes, fileName);
			}
		} catch (IOException e) {
			throw new ServerException("error uploading kyc file: " + e.getMessage(), null);
		}
		String context = ParamsUtils.getParam("CLOUDFRONT_URL");
		return String.format("%s/%s", context, response);
	}

	public List<AppUserEntity> findPendingWelcomeUsers() {
		Integer pendingWelcomeUserTime = ParamsUtils.getIntegerParam("APP_PENDING_WELCOME_USER_TIME", 2);
		Date cutoff = new Date(System.currentTimeMillis() - pendingWelcomeUserTime * 60 * 1000);
		return appUserRepository.findPendingWelcomeUsers(cutoff);
	}

	public AppUserEntity markWelcomeMessageSent(Long userId) {
		AppUserEntity entity = findById(userId);
		if (entity != null) {
			entity.setWelcomeMessageSent(true);
			save(entity);
		}
		return entity;
	}

	public AppUserEntity validateReferralCode(AppUserEntity appUser, String referralCode) {
		Long userId = appUser.getId();
		AppUserEntity response = null;
		AppUserEntity referredByEntity = appUserRepository.findByReferralCode(referralCode);
		if (Objects.nonNull(referredByEntity) && !Objects.equals(userId, referredByEntity.getId())) {
			appUser.setReferredBy(referralCode);
			response = appUserRepository.save(appUser);
		} else {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.INCORRECT_REFERRAL_CODE, "The referral code is not valid. Please enter again.", "referralCode"));
		}
		return response;
	}

	/**
	 * Soft delete a user. Sets isDeleted flag and invalidates all active sessions.
	 * After soft delete, the user will not be able to login or be found in queries.
	 * The user can create a new account with the same email/phone/provider ID.
	 *
	 * @param userId The ID of the user to soft delete
	 * @return The soft deleted user entity
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public AppUserEntity softDelete(Long userId) {
		AppUserEntity entity = findById(userId);
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError("USER_NOT_FOUND", "User not found", null));
		}

		if (entity.getIsDeleted() != null && entity.getIsDeleted()) {
			throw new ValidationException(ErrorBean.withError("USER_ALREADY_DELETED", "User is already deleted", null));
		}

		// Set soft delete flag
		entity.setIsDeleted(true);

		// Invalidate all active sessions for this user
		sessionService.doLogout(userId);

		// Save the entity
		save(entity);

		logger.info(String.format("User %s has been soft deleted", userId));

		return entity;
	}

	/**
	 * Soft delete the currently authenticated user (self-delete).
	 *
	 * @return The soft deleted user entity
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public AppUserEntity softDeleteSelf() {
		final Long userId = userService.getSessionUserId();
		return softDelete(userId);
	}

}