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
import com.velvet.rest.services.user.beans.CustomerBean;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.beans.LoginGenerateOtpRequest;
import com.velvet.rest.services.user.beans.LoginOtpRequest;
import com.velvet.rest.services.user.beans.OrgBasicResponse;
import com.velvet.rest.services.user.beans.OrgCodeEmailRequest;
import com.velvet.rest.services.user.beans.UserAddressBean;
import com.velvet.rest.services.user.clients.UserClientService;
import com.velvet.rest.services.user.entity.CustomerEntity;
import com.velvet.rest.services.user.entity.UserAddressEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.services.AppSessionService;
import com.velvet.rest.services.user.services.CustomerService;
import com.velvet.rest.services.user.services.EmailService;
import com.velvet.rest.services.user.services.OtpTokenService;
import com.velvet.rest.services.user.services.UserAddressService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class CustomerAuthController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Customer Auth Services", description = "Manage Customer authentication related services.")
public class CustomerAuthController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerAuthController.class);

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AppSessionService sessionService;

	@Autowired
	private BaseMapper<?, ?> customerMapper;

	@Autowired
	private OtpTokenService otpService;

	@Autowired
	private UserAddressService userAddressService;

	@Autowired
	private UserClientService userClientService;

	@Autowired
	private EmailService emailService;

	private static String USER_TYPE = CommonConstants.UserTypes.CUSTOMER;

	private static ApplicationId APP_ID = ApplicationId.CUSTOMER_APP;

	/**
	 * Generate OTP for Customer Application.
	 *
	 * @param phoneNumber the phone number
	 * @return the response entity
	 */
	@ApiOperation(value = "Generate OTP for Customer Application", nickname = "generateCustomerOtp")
	@PostMapping("/users/customers/auth/otp/generate")
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
	@ApiOperation(value = "Login/Register using OTP for Customer Application", nickname = "verifyCustomerOtpAndLogin")
	@PostMapping("/users/customers/auth/otp/verify")
	public AuthResponse verifyOtpAndLogin(@Valid @RequestBody LoginOtpRequest otpRequest, @ApiIgnore Errors errors) {
		logger.info("verifyOtpAndLogin :: request: {}", otpRequest);
		String phone = otpRequest.getCountryCode() + otpRequest.getPhone();
		CustomerEntity customer = customerService.findByPhone(phone);
		if (customer != null && customer.getBlocked().equals(true)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.USER_BLOCKED, "User is blocked. Please contact support.", "blocked"));
		}
		customerService.verifyOtp(customer != null ? customer.getId() : null, phone, otpRequest.getOtp());
		if (customer == null) {
			// Create new user (also handles deleted users who are treated as new)
			customer = customerService.createCustomer(phone, USER_TYPE);
		}
		if (!customer.getPhoneVerified()) {
			customerService.markPhoneVerified(customer);
		}
		if (StringUtils.isEmpty(customer.getReferralCode())) {
			customerService.generateReferralCode(customer);
		}
		Long userId = customer.getId();
		return buildLoginResponse(customer, userId);
	}

	private AuthResponse buildLoginResponse(CustomerEntity customer, Long userId) {
		Integer allowedSessions = customer.getAllowedSessions();
		final String authToken = sessionService.doLogin(userId, USER_TYPE, APP_ID, allowedSessions);
		AuthResponse response = new AuthResponse();
		response.setAuthToken(authToken);
		CustomerBean userBean = getMapper().mapSrcToDest(customer, CustomerBean.newInstance());
		addAddressInCustomerBean(userBean);
		response.setUser(userBean);
		return response;
	}

	private void addAddressInCustomerBean(CustomerBean userResponse) {
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
	@PostMapping("/users/customers/auth/org/email-code")
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
		CustomerEntity customer = customerService.getAuthEntity();
		if (customer != null) {
			customer.setEmail(email);
			customerService.save(customer);
		}
	}

	/**
	 * Logout Customer.
	 *
	 * @return the response entity
	 */
	@ApiOperation(value = "Logout Customer", nickname = "logoutCustomer")
	@PostMapping("/users/customers/auth/logout")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void logout() {
		logger.info("logout :: request received");
		customerService.logout();
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return customerMapper;
	}

}
