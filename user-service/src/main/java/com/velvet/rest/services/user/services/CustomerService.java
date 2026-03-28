package com.velvet.rest.services.user.services;

import java.io.IOException;
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
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.common.upload.UploadService;
import com.velvet.rest.services.common.upload.UploadServiceUtil;
import com.velvet.rest.services.user.beans.CustomerInfoRequest;
import com.velvet.rest.services.user.beans.GoogleUser;
import com.velvet.rest.services.user.constants.UserConstants.Gender;
import com.velvet.rest.services.user.entity.CustomerEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.CustomerRepository;
import com.velvet.rest.services.user.utils.AuthUtils;

/**
 * Created by mohit on 27.4.20.
 */
@Service
public class CustomerService implements UserService, BaseService<CustomerEntity> {

	private static final AppLogger logger = LoggingManager.getLogger(CustomerService.class);

	private static final String USER_TYPE = CommonConstants.UserTypes.CUSTOMER;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private AuthUserService userService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private UploadService uploadService;

	public CustomerEntity findById(Long id) {
		CustomerEntity result = findRecordById(id);
		return result;
	}

	public CustomerEntity save(CustomerEntity entity) {
		CustomerEntity result = customerRepository.save(entity);
		return result;
	}

	public Iterable<CustomerEntity> saveAll(List<CustomerEntity> entities) {
		Iterable<CustomerEntity> result = customerRepository.saveAll(entities);
		return result;
	}

	public List<CustomerEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	public PageAndSortResult<CustomerEntity> findAllByPage(Integer pageSize, Integer pageNo, Map<String, Object> filters, Map<String, SortDirection> sort) {
		PageAndSortResult<CustomerEntity> customerList = null;
		if (CollectionUtils.isEmpty(filters)) {
			filters = new HashMap<>();
		}
		try {
			customerList = findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			logger.error(e);
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
		return customerList;
	}

	@Override
	public Class<CustomerEntity> getEntity() {
		return CustomerEntity.class;
	}

	@Override
	public BaseCrudRepository<?, ?> getRepository() {
		return customerRepository;
	}

	public CustomerEntity findByPhone(String phone) {
		CustomerEntity customer = customerRepository.findByPhone(phone);
		return customer;
	}

	public CustomerEntity findByEmail(String email) {
		CustomerEntity customer = customerRepository.findByEmail(email);
		return customer;
	}

	public Boolean verifyOtp(Long userId, String phone, String otp) {
		return otpService.verifyPhoneOtp(userId, phone, otp);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CustomerEntity createCustomer(String phone, String userType) {
		CustomerEntity customer = (CustomerEntity) userService.registerUser(phone, userType, CustomerEntity.newInstance());
		customer.setAllowedSessions(ParamsUtils.getIntegerParam("CUSTOMER_ALLOWED_SESSIONS", 2));
		return save(customer);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CustomerEntity findOrCreateCustomer(String phone) {
		CustomerEntity customer = findByPhone(phone);
		if (customer == null) {
			customer = createCustomer(phone, USER_TYPE);
		}
		return customer;
	}

	public CustomerEntity updateName(Long id, String name) {
		CustomerEntity entity = findById(id);
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "User not found", "id"));
		}
		entity.setName(name);
		return save(entity);
	}

	public CustomerEntity buildCustomerEntity(GoogleUser googleUser, CustomerEntity customer) {
		String name = ((googleUser.getFirstName() != null ? googleUser.getFirstName() : "") + " "
				+ (googleUser.getLastName() != null ? googleUser.getLastName() : "")).trim();
		if (!name.isEmpty()) {
			customer.setName(name);
		}
		customer.setProfilePic(googleUser.getPicture());
		return customer;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public CustomerEntity createCustomerByEmail(String email, String userType) {
		Long userId = userService.createNewUser(userType);
		CustomerEntity customer = CustomerEntity.newInstance();
		customer.setId(userId);
		customer.setEmail(email);
		customer.setEmailVerified(true);
		customer.setAllowedSessions(ParamsUtils.getIntegerParam("CUSTOMER_ALLOWED_SESSIONS", 2));
		return save(customer);
	}

	public void generateReferralCode(CustomerEntity customer) {
		String referralCode = null;
		do {
			referralCode = AuthUtils.generateReferralCode();
			CustomerEntity otherCustomer = customerRepository.findByReferralCode(referralCode);
			logger.info(String.format("CustomerService :: generateRefferrerCode method :: referralCode %s", referralCode));
			if (Objects.isNull(otherCustomer) || !Objects.equals(referralCode, otherCustomer.getReferralCode())) {
				break;
			}
		} while (true);

		customer.setReferralCode(referralCode);
		save(customer);
	}

	@Override
	public CustomerEntity getAuthEntity() {
		final Long id = userService.getSessionUserId();
		CustomerEntity entity = findById(id);
		return entity;
	}

	public CustomerEntity updateVerifiedEmail(String email) {
		CustomerEntity customer = getAuthEntity();
		customer.setEmail(email);
		customer.setEmailVerified(true);
		customer = save(customer);
		return customer;
	}

	public CustomerEntity updateVerifiedPhone(String phone, String countryCode) {
		CustomerEntity customer = getAuthEntity();
		customer.setCountryCode(countryCode);
		customer.setPhone(phone);
		customer.setPhoneVerified(true);
		customer = save(customer);
		return customer;
	}

	public CustomerEntity markPhoneVerified(CustomerEntity customer) {
		customer.setPhoneVerified(true);
		customer = save(customer);
		return customer;
	}

	public CustomerEntity updateInfo(CustomerEntity entity, String name, String email, Gender gender, Integer status) {
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

	public CustomerEntity updateInfo(CustomerEntity entity, @Valid CustomerInfoRequest userInfoRequest, Integer status) {
		return updateInfo(entity, userInfoRequest.getName(), userInfoRequest.getEmail(), userInfoRequest.getGender(), status);
	}

	public CustomerEntity updateStatus(CustomerEntity entity, Integer status) {
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

		CustomerEntity entity = getAuthEntity();
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

	public List<CustomerEntity> findPendingWelcomeUsers() {
		Integer pendingWelcomeUserTime = ParamsUtils.getIntegerParam("APP_PENDING_WELCOME_USER_TIME", 2);
		Date cutoff = new Date(System.currentTimeMillis() - pendingWelcomeUserTime * 60 * 1000);
		return customerRepository.findPendingWelcomeUsers(cutoff);
	}

	public CustomerEntity markWelcomeMessageSent(Long userId) {
		CustomerEntity entity = findById(userId);
		if (entity != null) {
			entity.setWelcomeMessageSent(true);
			save(entity);
		}
		return entity;
	}

	public CustomerEntity validateReferralCode(CustomerEntity customer, String referralCode) {
		Long userId = customer.getId();
		CustomerEntity response = null;
		CustomerEntity referredByEntity = customerRepository.findByReferralCode(referralCode);
		if (Objects.nonNull(referredByEntity) && !Objects.equals(userId, referredByEntity.getId())) {
			customer.setReferredBy(referralCode);
			response = customerRepository.save(customer);
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
	public CustomerEntity softDelete(Long userId) {
		CustomerEntity entity = findById(userId);
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
	public CustomerEntity softDeleteSelf() {
		final Long userId = userService.getSessionUserId();
		return softDelete(userId);
	}

}