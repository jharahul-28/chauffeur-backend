package com.velvet.rest.services.user.controller;

import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
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
import com.velvet.rest.services.user.beans.AdminUserBean;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.beans.GenerateOtpRequest;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.beans.LoginPasswordRequest;
import com.velvet.rest.services.user.entity.AdminUserEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.AdminUserService;
import com.velvet.rest.services.user.services.AppSessionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class AdminUserController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Admin User Auth Services", description = "Manage AdminUser User related services.")
public class AdminAuthController implements BaseController {

	@Autowired
	private AdminUserService adminUserService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private BaseMapper<?, ?> adminUserMapper;

	private static String USER_TYPE = CommonConstants.UserTypes.ADMIN;

	private static ApplicationId APP_ID = ApplicationId.ADMIN_APP;

	/**
	 * Generate OTP for AdminUser Application
	 * 
	 */
	@ApiOperation(value = "Generate OTP for Admin User", nickname = "generateAdminUserOtp")
	@PostMapping("/admins/auth/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void generateOtp(@Valid @RequestBody GenerateOtpRequest otpRequest, @ApiIgnore Errors errors) {
		adminUserService.generatePhoneOtp(otpRequest.getReferenceId(), otpRequest.getVerificationId());
	}

	/**
	 * Login/Register using OTP for AdminUser Application
	 * 
	 */
	@ApiOperation(value = "Login using OTP for Admin User", nickname = "verifyAdminUserOtpAndLogin")
	@PostMapping("/admins/auth/otp/login")
	public AuthResponse verifyOtpAndLogin(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		String phone = otpRequest.getPhone();
		String otp = otpRequest.getOtp();
		AdminUserEntity adminUser = adminUserService.findByPhone(otpRequest.getPhone());
		Long userId = adminUser.getId();
		adminUserService.verifyOtp(userId, phone, otp);
		Integer allowedSessions = adminUser.getAllowedSessions();
		final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID, allowedSessions);
		AuthResponse response = new AuthResponse();
		response.setAuthToken(authToken);
		response.setUser(getMapper().mapSrcToDest(adminUser, AdminUserBean.newInstance()));
		return response;
	}

	/**
	 * Login/Register using OTP for AdminUser Application
	 * 
	 */
	@ApiOperation(value = "Login using Password for Admin User", nickname = "verifyAdminUserPwdAndLogin")
	@PostMapping("/admins/auth/password/login")
	public AuthResponse verifyPasswordAndLogin(@Valid @RequestBody LoginPasswordRequest loginRequest, @ApiIgnore Errors errors) {
		validateBean(loginRequest);
		boolean isPhone = validateUsername(loginRequest.getUsername());
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		AdminUserEntity adminUser = adminUserService.findUserByPassword(username, password, isPhone);
		adminUserService.verifyPassword(USER_TYPE, password, adminUser.getPassword());
		Long userId = adminUser.getId();
		Integer allowedSessions = adminUser.getAllowedSessions();
		final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID, allowedSessions);
		AuthResponse response = new AuthResponse();
		response.setAuthToken(authToken);
		response.setUser(getMapper().mapSrcToDest(adminUser, AdminUserBean.newInstance()));
		return response;
	}

	private void validateBean(LoginPasswordRequest loginRequest) {
		Assert.notNull(loginRequest.getUsername(), "username cannot be null");
		Assert.notNull(loginRequest.getPassword(), "password cannot be null");
	}

	private boolean validateUsername(String username) {
		if (isValidPhone(username)) {
			return true;
		} else if (isValidEmail(username)) {
			return false;
		} else {
			throw new ValidationException(ErrorBean.withError(AppErrors.INVALID_USERNAME, "Username is invalid.", "username"));
		}
	}

	private boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}

	private boolean isValidPhone(String phone) {
		String phoneRegex = "^(0/91)?[7-9][0-9]{9}$";
		Pattern pat = Pattern.compile(phoneRegex);
		if (phone == null)
			return false;
		return pat.matcher(phone).matches();
	}

	/**
	 * Logout AppUser.
	 *
	 * @return the response entity
	 */
	@ApiOperation(value = "Logout AppUser", nickname = "logoutAppUser")
	@PostMapping("/admins/auth/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		adminUserService.logout();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return adminUserMapper;
	}

}
