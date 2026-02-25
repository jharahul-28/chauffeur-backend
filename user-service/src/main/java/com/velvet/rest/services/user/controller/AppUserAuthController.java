package com.velvet.rest.services.user.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.AppUserBean;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.beans.LoginGenerateOtpRequest;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.beans.OrgBasicResponse;
import com.velvet.rest.services.user.beans.OrgCodeEmailRequest;
import com.velvet.rest.services.user.beans.UserAddressBean;
import com.velvet.rest.services.user.clients.UserClientService;
import com.velvet.rest.services.user.entity.AppUserEntity;
import com.velvet.rest.services.user.entity.UserAddressEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.AppSessionService;
import com.velvet.rest.services.user.services.AppUserService;
import com.velvet.rest.services.user.services.EmailService;
import com.velvet.rest.services.user.services.OtpTokenService;
import com.velvet.rest.services.user.services.UserAddressService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class AppUserAuthController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "AppUser Auth Services", description = "Manage AppUser authentication related services.")
public class AppUserAuthController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AppUserAuthController.class);

	@Autowired
	private AppUserService appUserService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private BaseMapper<?, ?> appUserMapper;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private UserClientService userClientService;

	@Autowired
	private EmailService emailService;

	private static String USER_TYPE = CommonConstants.UserTypes.APP_USER;

	private static ApplicationId APP_ID = ApplicationId.USER_APP;

	/**
	 * Generate OTP for AppUser Application.
	 *
	 * @param phoneNumber the phone number
	 * @return the response entity
	 */
	@ApiOperation(value = "Generate OTP for AppUser Application", nickname = "generateAppUserOtp")
	@PostMapping("/users/auth/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void generateOtp(@Valid @RequestBody LoginGenerateOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("generateOtp :: request: {}", otpRequest);
		String phone = otpRequest.getCountryCode() + otpRequest.getPhone();
		otpService.generatePhoneOtp(null, phone, otpRequest.getVerificationId());
	}

	/**
	 * Verify OTP and login.
	 *
	 * @param phoneNumber the phone number
	 * @param otp         the otp
	 * @return the response entity
	 */
	@ApiOperation(value = "Login/Register using OTP for AppUser Application", nickname = "verifyAppUserOtpAndLogin")
	@PostMapping("/users/auth/otp/verify")
	public AuthResponse verifyOtpAndLogin(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("verifyOtpAndLogin :: request: {}", otpRequest);
		String phone = otpRequest.getCountryCode() + otpRequest.getPhone();
		AppUserEntity appUser = appUserService.findByPhone(phone);
		if (appUser != null && appUser.getBlocked().equals(true)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_BLOCKED, "User is blocked. Please contact support.", "blocked"));
		}
		appUserService.verifyOtp(appUser != null ? appUser.getId() : null, phone, otpRequest.getOtp());
		if (appUser == null) {
			// Create new user (also handles deleted users who are treated as new)
			appUser = appUserService.createAppUser(phone, USER_TYPE);
		}
		if (!appUser.getPhoneVerified()) {
			appUserService.markPhoneVerified(appUser);
		}
		if (StringUtils.isEmpty(appUser.getReferralCode())) {
			appUserService.generateReferralCode(appUser);
		}
		Long userId = appUser.getId();
		return buildLoginResponse(appUser, userId);
	}

	private AuthResponse buildLoginResponse(AppUserEntity appUser, Long userId) {
		Integer allowedSessions = appUser.getAllowedSessions();
		final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID, allowedSessions);
		AuthResponse response = new AuthResponse();
		response.setAuthToken(authToken);
		AppUserBean userBean = getMapper().mapSrcToDest(appUser, AppUserBean.newInstance());
		addAddressInAppUserBean(userBean);
		response.setUser(userBean);
		return response;
	}

	private void addAddressInAppUserBean(AppUserBean userResponse) {
		UserAddressEntity address = userAddressService.findByUserId(userResponse.getId());
		if (address != null) {
			UserAddressBean addressBean = getMapper().mapSrcToDest(address, UserAddressBean.newInstance());
			userResponse.setAddress(addressBean);
		}
	}

	/**
	 * Send organisation code link to user's email.
	 */
	@ApiOperation(value = "Send org email link via email", nickname = "sendOrgEmailLink")
	@PostMapping("/users/auth/org/email-code")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sendOrgCodeLink(@Valid @RequestBody OrgCodeEmailRequest request, @ApiIgnore Errors errors) {
		logger.info("sendOrgLoginLink :: email: {}", request.getEmail());
		checkError(errors);
		String email = request.getEmail().trim().toLowerCase();
		String domain = email.substring(email.indexOf("@") + 1);
		OrgBasicResponse org = userClientService.getOrgByDomain(domain);
		if (org == null) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.ORG_NOT_FOUND, "No organisation found for this email domain. Please contact support.", "email"));
		}
		emailService.sendLoginLink(email, org.getCode(), org.getName());
		AppUserEntity appUser = appUserService.getAuthEntity();
		if (appUser != null) {
			appUser.setEmail(email);
			appUserService.save(appUser);
		}
	}

	/**
	 * Logout AppUser.
	 *
	 * @return the response entity
	 */
	@ApiOperation(value = "Logout AppUser", nickname = "logoutAppUser")
	@PostMapping("/users/auth/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		logger.info("logout :: request received");
		appUserService.logout();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return appUserMapper;
	}

}
