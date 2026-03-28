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
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.beans.ChauffeurBean;
import com.velvet.rest.services.user.beans.LoginGenerateOtpRequest;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.entity.ChauffeurEntity;
import com.velvet.rest.services.user.services.AppSessionService;
import com.velvet.rest.services.user.services.ChauffeurService;
import com.velvet.rest.services.user.services.OtpTokenService;
import com.velvet.rest.services.user.utils.UserServiceUtil;
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

	private static final String USER_TYPE = CommonConstants.UserTypes.CHAUFFEUR;

	private static final ApplicationId APP_ID = ApplicationId.CHAUFFEUR_APP;

	/**
	 * Generate OTP for Chauffeur Application.
	 */
	@ApiOperation(value = "Generate OTP for Chauffeur Application", nickname = "generateChauffeurOtp")
	@PostMapping("/users/chauffeurs/auth/otp/generate")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void generateOtp(@Valid @RequestBody LoginGenerateOtpRequest otpRequest, @ApiIgnore Errors errors) {

		String phone = UserServiceUtil.formatPhoneNumber(otpRequest.getCountryCode(), otpRequest.getPhone());
		ChauffeurEntity chauffeur = chauffeurService.getByPhone(phone);
		otpService.generatePhoneOtp(chauffeur.getId(), phone, otpRequest.getVerificationId());
	}

	/**
	 * Verify OTP and login for Chauffeur Application.
	 */
	@ApiOperation(value = "Login/Register using OTP for Chauffeur Application", nickname = "verifyChauffeurOtpAndLogin")
	@PostMapping("/users/chauffeurs/auth/otp/verify")
	public AuthResponse verifyOtpAndLogin(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {

		String phone = UserServiceUtil.formatPhoneNumber(otpRequest.getCountryCode(), otpRequest.getPhone());
		ChauffeurEntity chauffeur = chauffeurService.getByPhone(phone);
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
	@PostMapping("/users/chauffeurs/auth/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		chauffeurService.logout();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return chauffeurMapper;
	}
}
