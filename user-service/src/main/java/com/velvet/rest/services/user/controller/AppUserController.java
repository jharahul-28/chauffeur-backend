package com.velvet.rest.services.user.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.AppUserBean;
import com.velvet.rest.services.user.beans.AppUserInfoRequest;
import com.velvet.rest.services.user.beans.AppUserResponse;
import com.velvet.rest.services.user.beans.CreateWAUserRequest;
import com.velvet.rest.services.user.beans.GenerateOtpRequest;
import com.velvet.rest.services.user.beans.InitBean;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.beans.ProfilePicResponse;
import com.velvet.rest.services.user.beans.UserAddressBean;
import com.velvet.rest.services.user.beans.UserAddressRequest;
import com.velvet.rest.services.user.entity.AppUserEntity;
import com.velvet.rest.services.user.entity.UserAddressEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.AppUserService;
import com.velvet.rest.services.user.services.AuthUserService;
import com.velvet.rest.services.user.services.OtpTokenService;
import com.velvet.rest.services.user.services.UserAddressService;
import com.velvet.rest.services.user.services.WelcomeMessageService;
import com.velvet.rest.services.user.utils.UserServiceUtil;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class AppUserController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "AppUser Services", description = "Manage AppUser related services.")
public class AppUserController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

	private static final String EMAIL_REGEX = "\\S+@\\S+\\.\\S+";

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private AuthUserService authUserService;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private WelcomeMessageService welcomeMessageService;

	@Autowired
	private BaseMapper<?, ?> appUserMapper;

	/**
	 * Get users by IDs.
	 *
	 */
	@ApiOperation(value = "Get users by comma-separated IDs", nickname = "getUsersByIds")
	@GetMapping("/users")
	public ResponseEntity<List<AppUserResponse>> findAllByIds(@RequestParam String ids) {
		logger.info("findAllByIds :: ids: {}", ids);
		try {
			List<Long> userIds = Arrays.stream(ids.split(",")).map(String::trim).map(Long::valueOf).collect(Collectors.toList());

			List<AppUserEntity> users = appUserService.findRecordByIds(userIds);
			List<AppUserResponse> response = getMapper().mapAsList(users, AppUserResponse.class);
			return ResponseEntity.ok(response);
		} catch (NumberFormatException e) {
			logger.error("Invalid user IDs format: " + ids, e);
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Find all paginated.
	 *
	 */
	@ApiOperation(value = "List all AppUsers Paginated in application.", nickname = "findAppUserListPaginated")
	@GetMapping("/users/paginated")
	public PageAndSortResult<AppUserBean> findAllPaginated(@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "20") Integer pageSize, @RequestParam(required = false) String sortBy,
			@RequestParam(required = false) SortDirection sortDirection, HttpServletRequest request) {
		logger.info("findAllPaginated :: pageNo: {}, pageSize: {}, sortBy: {}", pageNo, pageSize, sortBy);
		Map<String, SortDirection> sort = null;
		if (sortBy != null) {
			sort = buildSortMap(sortBy, sortDirection);
		}
		final PageAndSortResult<AppUserEntity> results = appUserService.findAllByPage(pageSize, pageNo, getSearchParams(request, AppUserEntity.class), sort);
		PageAndSortResult<AppUserBean> response = new PageAndSortResult<AppUserBean>();
		if (results != null && results.getData() != null) {
			response = prepareResponsePageData(results, AppUserBean.class);
		}
		return response;
	}

	/**
	 * Find one.
	 *
	 */
	@ApiOperation(value = "Get one AppUser with provided id.", nickname = "findOneAppUserById")
	@GetMapping("/users/{id}")
	public ResponseEntity<AppUserBean> findOne(@PathVariable Long id) {
		logger.info("findOne :: id: {}", id);
		AppUserEntity entity = appUserService.findById(id);
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		AppUserBean userResponse = getMapper().mapSrcToDest(entity, AppUserBean.newInstance());
		addAddressInAppUserBean(userResponse);
		return ResponseEntity.ok(userResponse);
	}

	/**
	 * Find one.
	 *
	 */
	@ApiOperation(value = "Get one AppUser with provided id.", nickname = "findOneAppUserByPhone")
	@GetMapping("/users/phone/{phone}")
	public ResponseEntity<AppUserBean> findByPhone(@PathVariable String phone) {
		logger.info("findByPhone : {}", phone);
		AppUserEntity entity = appUserService.findByPhone(phone);
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		AppUserBean userResponse = getMapper().mapSrcToDest(entity, AppUserBean.newInstance());
		// addAddressInAppUserBean(userResponse);
		return ResponseEntity.ok(userResponse);
	}

	/**
	 * Get user name by ID - Internal service endpoint.
	 *
	 */
	@ApiOperation(value = "Get user name with provided id.", nickname = "getUserNameById")
	@GetMapping("/users/{id}/name")
	public ResponseEntity<String> getUserName(@PathVariable Long id) {
		logger.info("getUserName :: id: {}", id);
		AppUserEntity entity = appUserService.findById(id);
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(entity.getName());
	}

	/**
	 * <p>
	 * save.
	 * </p>
	 */
	@ApiOperation(value = "create new AppUser", nickname = "createAppUser")
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	public Long save(@Valid @RequestBody AppUserBean appUser, @ApiIgnore Errors errors) {
		logger.info("save :: request: {}", appUser);
		checkError(errors);
		final AppUserEntity entity = appUserService.save(getMapper().mapSrcToDest(appUser, AppUserEntity.newInstance()));
		return entity.getId();
	}

	/**
	 * <p>
	 * update.
	 * </p>
	 *
	 */
	@ApiOperation(value = "update existing app user with provided id", nickname = "updateAppUser")
	@PutMapping("/users/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@PathVariable Long id, @Valid @RequestBody AppUserBean appUserBean, @ApiIgnore Errors errors) {
		logger.info("update :: id: {}, request: {}", id, appUserBean);
		appUserBean.setId(id);
		checkError(errors);
		final AppUserEntity entity = appUserService.findById(id);
		appUserService.save(getMapper().mapSrcToDest(appUserBean, entity));
	}

	/**
	 * Init Call
	 *
	 */
	@ApiOperation(value = "AppUser Init Call", nickname = "appUserInitCall")
	@GetMapping("/users/init")
	public ResponseEntity<InitBean> init() {
		logger.info("init :: request received");
		String version = appUserService.appVersion(ApplicationId.CUSTOMER_APP.toString());
		Integer hardUpdate = appUserService.hardUpdate(ApplicationId.CUSTOMER_APP.toString());
		InitBean response = InitBean.newInstance();
		response.setVersion(version);
		response.setHardUpdate(hardUpdate);
		return ResponseEntity.ok(response);
	}

	/**
	 * Find AppUser's own user details.
	 *
	 */
	@ApiOperation(value = "Get AppUser's own user details from session.", nickname = "findAuthAppUserProfile")
	@GetMapping("/users/profile")
	public ResponseEntity<AppUserBean> getMyProfile() {
		logger.info("getMyProfile :: request received");
		AppUserEntity entity = appUserService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		AppUserBean userResponse = getMapper().mapSrcToDest(entity, AppUserBean.newInstance());
		addAddressInAppUserBean(userResponse);
		return ResponseEntity.ok(userResponse);
	}

	private void addAddressInAppUserBean(AppUserBean userResponse) {
		UserAddressEntity address = userAddressService.findByUserId(userResponse.getId());
		if (address != null) {
			UserAddressBean addressBean = getMapper().mapSrcToDest(address, UserAddressBean.newInstance());
			userResponse.setAddress(addressBean);
		}
	}

	/**
	 * Update AppUser's Address.
	 *
	 */
	@ApiOperation(value = "Create AppUser's Address.", nickname = "createAuthAppUserAddress")
	@PostMapping("/users/address")
	public ResponseEntity<UserAddressBean> createProfileAddress(@Valid @RequestBody UserAddressRequest addressRequest) {
		logger.info(String.format("createProfileAddress :: %s", addressRequest));

		AppUserEntity entity = appUserService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		UserAddressEntity userAddressEntity = userAddressService.updateAddress(entity.getId(), addressRequest);
		if (userAddressEntity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		entity.setOrgId(userAddressEntity.getOrgId());
		appUserService.save(entity);
		return ResponseEntity.ok(getMapper().mapSrcToDest(userAddressEntity, UserAddressBean.newInstance()));
	}

	/**
	 * Update AppUser's Address.
	 *
	 */
	@ApiOperation(value = "Create AppUser's Onboarding Address.", nickname = "createAuthAppUserAddress")
	@PostMapping("/users/address/onboarding")
	public ResponseEntity<UserAddressBean> createOnboardingProfileAddress(@Valid @RequestBody UserAddressRequest addressRequest) {
		logger.info(String.format("createProfileAddress :: %s", addressRequest));

		AppUserEntity entity = appUserService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		Integer status = 1;
		validateOnboardingStatus(entity, status);
		UserAddressEntity userAddressEntity = userAddressService.updateAddress(entity.getId(), addressRequest);
		if (userAddressEntity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		entity.setOrgId(userAddressEntity.getOrgId());
		appUserService.updateStatus(entity, status);
		return ResponseEntity.ok(getMapper().mapSrcToDest(userAddressEntity, UserAddressBean.newInstance()));
	}

	private void validateOnboardingStatus(AppUserEntity entity, Integer status) {
		if (entity.getStatus() >= status) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.ONBOARDING_STEP_COMPLETED, "This onboarding step has already been completed.", "status"));
		}
	}

	/**
	 * Update AppUser's Profile.
	 *
	 */
	@ApiOperation(value = "Update AppUser's Onboarding Profile.", nickname = "updateAuthAppUserProfile")
	@PostMapping("/users/profile/onboarding")
	public ResponseEntity<AppUserBean> updateOnboardingProfileInfo(@Valid @RequestBody AppUserInfoRequest request) {
		logger.info(String.format("createProfileAddress :: %s", request));
		AppUserEntity entity = appUserService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		Integer status = 2;
		validateOnboardingStatus(entity, status);
		entity = appUserService.updateInfo(entity, request, status);
		AppUserBean userResponse = getMapper().mapSrcToDest(entity, AppUserBean.newInstance());
		addAddressInAppUserBean(userResponse);
		return ResponseEntity.ok(userResponse);
	}

	/**
	 * Update AppUser's Profile.
	 *
	 */
	@ApiOperation(value = "Update AppUser's Profile.", nickname = "updateAppUserProfile")
	@PostMapping("/users/profile")
	public ResponseEntity<AppUserBean> updateProfileInfo(@Valid @RequestBody AppUserInfoRequest request) {
		logger.info("updateProfileInfo :: request: {}", request);
		AppUserEntity entity = appUserService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		entity = appUserService.updateInfo(entity, request, null);
		return ResponseEntity.ok(getMapper().mapSrcToDest(entity, AppUserBean.newInstance()));
	}

	/**
	 * This method will upload profile pic to S3.
	 * 
	 * @param files
	 * @param docType
	 * @param docNum
	 * @return UserKycEntity
	 */
	@ApiOperation(value = "upload single document on S3", nickname = "uploadProfilePic")
	@PostMapping(path = "/users/profile/pic/upload")
	public ResponseEntity<ProfilePicResponse> uploadProfilePic(@RequestParam MultipartFile file) {
		logger.info("uploadProfilePic :: filename: {}", file.getOriginalFilename());
		String profilePic = appUserService.uploadProfilePic(file);
		ProfilePicResponse response = new ProfilePicResponse();
		response.setProfilePic(profilePic);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * @param mailId
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
	 * @throws TemplateNotFoundException
	 */
	@ApiOperation(value = "Send an email verification mail", nickname = "sendEmailVerification")
	@PostMapping("/users/email/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sendEmailOtpForVerification(@Valid @RequestBody GenerateOtpRequest otpRequest, @ApiIgnore Errors errors)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, TemplateException, IOException {
		logger.info("sendEmailOtpForVerification :: request: {}", otpRequest);
		if (UserServiceUtil.patternMatches(otpRequest.getReferenceId(), EMAIL_REGEX)) {
			AppUserEntity appUser = appUserService.getAuthEntity();
			String otp = otpService.generateEmailOtp(otpRequest.getReferenceId(), appUser.getId());
			// send notifiaction.
		} else {
			logger.error("The input email id is incorrect- {}", otpRequest.getReferenceId());
			throw new ValidationException(ErrorBean.withError(AppErrors.EMAIL_ERROR, "Your email is not valid. Please enter again.", "email"));
		}
	}

	@ApiOperation(value = "validate email verification code", nickname = "validateEmailOtp")
	@PostMapping("/users/email/otp/verify")
	public ResponseEntity<AppUserBean> validateEmailOtp(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("validateEmailOtp :: request: {}", otpRequest);
		AppUserEntity appUser = appUserService.getAuthEntity();
		otpService.verifyEmailOtp(appUser.getId(), otpRequest.getPhone(), otpRequest.getOtp());
		appUser = appUserService.updateVerifiedEmail(otpRequest.getPhone());
		return new ResponseEntity<>(getMapper().mapSrcToDest(appUser, AppUserBean.newInstance()), HttpStatus.OK);
	}

	/**
	 * @param mailId
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws ParseException
	 * @throws MalformedTemplateNameException
	 * @throws TemplateNotFoundException
	 */
	@ApiOperation(value = "Send an email verification mail", nickname = "sendEmailVerification")
	@PostMapping("/users/phone/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sendPhoneOtpForVerification(@Valid @RequestBody GenerateOtpRequest otpRequest, @ApiIgnore Errors errors)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, TemplateException, IOException {
		logger.info("sendPhoneOtpForVerification :: request: {}", otpRequest);
		AppUserEntity appUser = appUserService.getAuthEntity();
		otpService.generatePhoneOtp(appUser.getId(), otpRequest.getReferenceId(), otpRequest.getVerificationId());
	}

	@ApiOperation(value = "validate email verification code", nickname = "validateEmailOtp")
	@PostMapping("/users/phone/otp/verify")
	public ResponseEntity<AppUserBean> validatePhoneOtp(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("validatePhoneOtp :: request: {}", otpRequest);
		AppUserEntity appUser = appUserService.getAuthEntity();
		otpService.verifyPhoneOtp(appUser.getId(), String.format("%s%s", otpRequest.getCountryCode(), otpRequest.getPhone()), otpRequest.getOtp());
		appUser = appUserService.updateVerifiedPhone(otpRequest.getPhone(), otpRequest.getCountryCode());
		return new ResponseEntity<>(getMapper().mapSrcToDest(appUser, AppUserBean.newInstance()), HttpStatus.OK);
	}

	/**
	 * This method will validate referral code for the app user and add referral
	 * bonus in account.
	 * 
	 * @return AppUserEntity
	 */
	@ApiOperation(value = "validate Referral Code", nickname = "validateReferralCode")
	@PostMapping(path = "/users/referralCode/validate")
	public ResponseEntity<AppUserBean> validateReferralCode(@RequestParam String referralCode) {
		logger.info("validateReferralCode :: referralCode: {}", referralCode);
		AppUserEntity appUser = appUserService.getAuthEntity();
		AppUserEntity response = appUserService.validateReferralCode(appUser, referralCode);
		return new ResponseEntity<>(getMapper().mapSrcToDest(response, AppUserBean.newInstance()), HttpStatus.OK);
	}

	/**
	 * This method will block the user
	 * 
	 * @return void
	 */
	@ApiOperation(value = "block the user", nickname = "blockAppUser")
	@PostMapping(path = "/users/{id}/block")
	@ResponseStatus(HttpStatus.OK)
	public void blockAppUser(@PathVariable Long id) {
		logger.info("blockAppUser :: id: {}", id);
		AppUserEntity entity = appUserService.findById(id);
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "User not found with the provided Id", "userId"));
		}
		entity.setBlocked(true);
		appUserService.save(entity);
	}

	/**
	 * This method will unblock the user
	 * 
	 * @return void
	 */
	@ApiOperation(value = "unblock the user", nickname = "unblockAppUser")
	@PostMapping(path = "/users/{id}/unblock")
	@ResponseStatus(HttpStatus.OK)
	public void unblockAppUser(@PathVariable Long id) {
		logger.info("unblockAppUser :: id: {}", id);
		AppUserEntity entity = appUserService.findById(id);
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "User not found with the provided Id", "userId"));
		}
		entity.setBlocked(false);
		appUserService.save(entity);
	}

	/**
	 * Soft delete a user by ID. After soft delete, the user will not be able to
	 * login or be found in queries. The user can create a new account with the same
	 * email/phone/provider ID.
	 *
	 * @param id the ID of the user to delete
	 * @return success message
	 */
	@ApiOperation(value = "Soft delete user by ID", nickname = "softDeleteUser")
	@DeleteMapping(path = "/users/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> softDeleteUser(@PathVariable Long id) {
		logger.info("softDeleteUser :: id: {}", id);
		appUserService.softDelete(id);
		return ResponseEntity.ok("User has been successfully deleted.");
	}

	/**
	 * This method will hard delete the user by email
	 *
	 * @param email the email of the user to delete
	 * @return void
	 */
	@ApiOperation(value = "hard delete user by email", nickname = "hardDeleteUserByEmail")
	@PostMapping(path = "/users/delete")
	@ResponseStatus(HttpStatus.OK)
	public void deleteUserSelf() {
		logger.info("deleteUserSelf :: request received");
		final Long userId = authUserService.getSessionUserId();
		appUserService.softDelete(userId);
	}

	/**
	 * Internal endpoint: Find or create user by phone, and set orgId if provided.
	 */
	@ApiOperation(value = "Find or create user by phone (internal) from whatsapp", nickname = "createUserFromWhatsapp")
	@PostMapping("/users/internal/whatsapp")
	public ResponseEntity<AppUserResponse> createUserFromWhatsapp(@RequestBody CreateWAUserRequest request) {
		logger.info("ensureUser :: phone: {}, orgId: {}", request.getPhone(), request.getOrgId());
		AppUserEntity appUser = appUserService.findByPhone(request.getPhone());
		if (appUser == null) {
			appUser = appUserService.createAppUser(request.getPhone(), CommonConstants.UserTypes.CHAUFFEUR);
		}
		if (request.getOrgId() != null && appUser.getOrgId() == null) {
			appUser.setOrgId(request.getOrgId());
			appUser = appUserService.save(appUser);
		}
		AppUserResponse response = getMapper().mapSrcToDest(appUser, AppUserResponse.newInstance());
		return ResponseEntity.ok(response);
	}

	/**
	 * Internal endpoint: Get users who completed onboarding and are pending welcome
	 * message.
	 */
	@ApiOperation(value = "Get pending welcome users (internal)", nickname = "getPendingWelcomeUsers")
	@PostMapping("/users/welcome-message")
	public void getPendingWelcomeUsers() {
		logger.info("getPendingWelcomeUsers :: request received");
		CompletableFuture.runAsync(() -> {
			welcomeMessageService.sendPendingWelcomeMessages();
		});
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return appUserMapper;
	}
}
