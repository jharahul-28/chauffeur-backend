package com.velvet.rest.services.user.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.entity.AdminUserEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.AdminUserRepository;

/**
 * Created by mohit on 27.4.25.
 */
@Service
public class AdminUserService implements BaseService<AdminUserEntity> {

	@Autowired
	private AdminUserRepository adminUserRepository;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private AuthUserService userService;

	@Autowired
	private AppSessionService sessionService;

	public AdminUserEntity findById(Long id) {
		AdminUserEntity result = findRecordById(id);
		return result;
	}

	public AdminUserEntity save(AdminUserEntity entity) {
		AdminUserEntity result = adminUserRepository.save(entity);
		return result;
	}

	public Iterable<AdminUserEntity> saveAll(List<AdminUserEntity> entities) {
		Iterable<AdminUserEntity> result = adminUserRepository.saveAll(entities);
		return result;
	}

	public List<AdminUserEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	@Override
	public Class<AdminUserEntity> getEntity() {
		return AdminUserEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return adminUserRepository;
	}

	public void generatePhoneOtp(String phone, String verificationId) {
		otpService.generatePhoneOtp(null, phone, verificationId);
	}

	@Transactional
	public void registerUser(AdminUserEntity user, String userType) {
		validateUserForRegistration(user);
		Long userId = userService.createAdminUser(userType);
		user.setId(userId);
		save(user);
	}

	private void validateUserForRegistration(final AdminUserEntity user) {
		boolean adminUserExists = exists(user);
		if (adminUserExists) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.USER_FOUND, "User's Phone or Email already registered. Please contact support.", "phone-email"));
		}
	}

	public boolean exists(final AdminUserEntity user) {
		return adminUserRepository.countByPhoneOrEmail(user.getPhone(), user.getEmail()) > 0;
	}

	private void validateActiveUser(final AdminUserEntity adminUser) {
		if (adminUser == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "User does not exist. Please contact support.", "user"));
		} else if (adminUser != null && adminUser.getActive() == 0) {
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_EXPIRED_ERROR, "User is inactive. Please contact support.", "active"));
		} else if (adminUser != null && adminUser.getBlocked()) {
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_EXPIRED_ERROR, "User is blocked. Please contact support.", "blocked"));
		}
	}

	public AdminUserEntity findByPhone(String phone) {
		AdminUserEntity adminUser = adminUserRepository.findByPhone(phone);
		validateActiveUser(adminUser);
		return adminUser;
	}

	public AdminUserEntity findByEmail(String email) {
		AdminUserEntity adminUser = adminUserRepository.findByEmail(email);
		validateActiveUser(adminUser);
		return adminUser;
	}

	public Boolean verifyOtp(Long userId, String phone, String otp) {
		return otpService.verifyPhoneOtp(userId, phone, otp);
	}

//	@Transactional
//	public AuthResponse verifyOtpAndLogin(String mobile, String otp) {
//		AuthResponse response = new AuthResponse();
//		AdminUserEntity adminUser = adminUserRepository.findByPhone(mobile);
//		validateActiveUser(adminUser);
//		otpService.verifyOtp(mobile, USER_TYPE, otp);
//		Long userId = adminUser.getId();
//
//		final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID);
//
//		response.setEntity(adminUser);
//		response.setAuthToken(authToken);
//		return response;
//	}

	public AdminUserEntity findUserByPassword(String username, String password, boolean isLoginWithPhone) {
		AdminUserEntity adminUser = null;
		if (isLoginWithPhone) {
			adminUser = adminUserRepository.findByPhone(username);
		} else {
			adminUser = adminUserRepository.findByEmail(username);
		}
		validateActiveUser(adminUser);
		return adminUser;
	}

	public Boolean verifyPassword(String userType, String requestPassword, String adminPassword) {
		return otpService.verifyPassword(userType, requestPassword, adminPassword);
	}

//	@Transactional
//	public AuthResponse verifyPasswordAndLogin(String username, String password, boolean isLoginWithPhone) {
//		AuthResponse response = new AuthResponse();
//		AdminUserEntity adminUser = null;
//		if (isLoginWithPhone) {
//			adminUser = adminUserRepository.findByPhone(username);
//		} else {
//			adminUser = adminUserRepository.findByEmail(username);
//		}
//		validateActiveUser(adminUser);
//		boolean passwordVerified = otpService.verifyPassword(USER_TYPE, password, adminUser.getPassword());
//		if (passwordVerified) {
//			Long userId = adminUser.getId();
//			final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID);
//			response.setEntity(adminUser);
//			response.setAuthToken(authToken);
//		} else {
//			throw new ValidationException(ErrorBean.withError(AppErrors.INCORRECT_PASSWORD, "Incorrect Password", null));
//		}
//		return response;
//	}

	public AdminUserEntity getAuthEntity() {
		final Long id = userService.getSessionUserId();
		AdminUserEntity entity = findById(id);
		return entity;
	}

	public void updateInfo(Long userId, String firstname, String lastname, String phone) {
		AdminUserEntity entity = findById(userId);
		validateActiveUser(entity);
		boolean save = false;
		if (!StringUtils.isEmpty(firstname)) {
			entity.setFirstname(firstname);
			save = true;
		}
		if (!StringUtils.isEmpty(lastname)) {
			entity.setLastname(lastname);
			save = true;
		}
		if (!StringUtils.isEmpty(phone)) {
			entity.setPhone(phone);
			save = true;
		}
		if (save) {
			save(entity);
		}
	}

	public void updateGroups(Long userId, List<Long> groupIds) {
		userService.updateGroups(userId, groupIds);
	}

	public void logout() {
		final Long id = userService.getSessionUserId();
		sessionService.doLogout(id);
	}

}