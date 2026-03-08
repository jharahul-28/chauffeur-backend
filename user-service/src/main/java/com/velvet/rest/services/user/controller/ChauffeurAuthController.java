package com.velvet.rest.services.user.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.lang3.BooleanUtils;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.beans.ChauffeurBean;
import com.velvet.rest.services.user.beans.LoginGenerateOtpRequest;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.AppSessionService;
import com.velvet.rest.services.user.services.ChauffeurService;
import com.velvet.rest.services.user.services.OtpTokenService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class ChauffeurAuthController.
 */
@RestController
@Api(tags = "Chauffeur Auth Services", description = "Manage Chauffeur authentication related services.")
public class ChauffeurAuthController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(ChauffeurAuthController.class);

	@Autowired
	private ChauffeurService chauffeurService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private BaseMapper<?, ?> chauffeurMapper;

	@Autowired
	private OtpTokenService otpService;

	private static String USER_TYPE = CommonConstants.UserTypes.CHAUFFEUR;

	private static ApplicationId APP_ID = ApplicationId.CHAUFFEUR_APP;

	/**
	 * Generate OTP for Chauffeur Application.
	 */
	@ApiOperation(value = "Generate OTP for Chauffeur Application", nickname = "generateChauffeurOtp")
	@PostMapping("/chauffeurs/auth/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void generateOtp(@Valid @RequestBody LoginGenerateOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("generateOtp :: request: {}", otpRequest);
		String phone = otpRequest.getCountryCode() + otpRequest.getPhone();
		ChauffeurEntity chauffeur = chauffeurService.findByPhone(phone);
		if (chauffeur == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "No chauffeur account found for this phone number. Please contact admin.", "phone"));
		}
		if (BooleanUtils.isTrue(chauffeur.getBlocked())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_BLOCKED, "User is blocked. Please contact support.", "blocked"));
		}
		otpService.generatePhoneOtp(chauffeur.getId(), phone, otpRequest.getVerificationId());
	}

	/**
	 * Verify OTP and login for Chauffeur Application.
	 */
	@ApiOperation(value = "Login/Register using OTP for Chauffeur Application", nickname = "verifyChauffeurOtpAndLogin")
	@PostMapping("/chauffeurs/auth/otp/verify")
	public AuthResponse verifyOtpAndLogin(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("verifyOtpAndLogin :: request: {}", otpRequest);
		String phone = otpRequest.getCountryCode() + otpRequest.getPhone();
		ChauffeurEntity chauffeur = chauffeurService.findByPhone(phone);
		if (chauffeur == null) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_NOT_FOUND, "No chauffeur account found for this phone number. Please contact admin.", "phone"));
		}
		if (BooleanUtils.isTrue(chauffeur.getBlocked())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_BLOCKED, "User is blocked. Please contact support.", "blocked"));
		}
		chauffeurService.verifyOtp(chauffeur.getId(), phone, otpRequest.getOtp());
		return buildLoginResponse(chauffeur);
	}

	private AuthResponse buildLoginResponse(ChauffeurEntity chauffeur) {
		Integer allowedSessions = chauffeur.getAllowedSessions();
		final String authToken = sessionService.doLogin(chauffeur.getId(), USER_TYPE, APP_ID, allowedSessions);
		AuthResponse response = new AuthResponse();
		response.setAuthToken(authToken);
		response.setUser(getMapper().mapSrcToDest(chauffeur, ChauffeurBean.newInstance()));
		return response;
	}

	/**
	 * Logout Chauffeur.
	 */
	@ApiOperation(value = "Logout Chauffeur", nickname = "logoutChauffeur")
	@PostMapping("/chauffeurs/auth/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		chauffeurService.logout();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return chauffeurMapper;
	}
}
