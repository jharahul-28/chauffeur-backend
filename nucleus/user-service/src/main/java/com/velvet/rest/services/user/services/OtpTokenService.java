package com.velvet.rest.services.user.services;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ContextUtils;
import com.velvet.rest.common.utils.DateUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.clients.UserClientService;
import com.velvet.rest.services.user.constants.UserConstants.OtpType;
import com.velvet.rest.services.user.entity.OtpTokenEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.OtpTokenRepository;
import com.velvet.rest.services.user.beans.WhapiSendTextRequest;
import com.velvet.rest.services.user.utils.AuthUtils;

@Service
public class OtpTokenService implements BaseService<OtpTokenEntity> {

	private static final Logger logger = LoggerFactory.getLogger(OtpTokenService.class);

	@Autowired
	private OtpTokenRepository otpTokenRepository;

	@Autowired
	private UserClientService clientService;

	public String generateEmailOtp(String email, Long userId) {
		Assert.notNull(userId, "user id cannot be null");
		OtpTokenEntity otpEntity = otpTokenRepository.findByUserIdAndOtpTypeAndReferenceId(userId, OtpType.EMAIL, email);
		if (otpEntity != null && !otpEntity.isVerified() && otpEntity.getExpiry().after(DateUtils.getDateTime())) {
			return otpEntity.getOtp();
		}
		if (otpEntity == null) {
			otpEntity = OtpTokenEntity.newInstance();
			otpEntity.setOtpType(OtpType.EMAIL);
			otpEntity.setUserId(userId);
			otpEntity.setReferenceId(email);
			otpEntity.setProvider(CommonConstants.AuthTypes.SYSTEM);
		}
		otpEntity.setOtp(AuthUtils.generateUserOtp());
		otpEntity.setExpiry(AuthUtils.getUserOtpExpiry());
		otpEntity.setVerified(false);
		save(otpEntity);
		return otpEntity.getOtp();
	}

	public OtpTokenEntity save(OtpTokenEntity entity) {
		OtpTokenEntity result = otpTokenRepository.save(entity);
		return result;
	}

	@Override
	public Class<OtpTokenEntity> getEntity() {
		return OtpTokenEntity.class;
	}

	public OtpTokenEntity fetchSentEmailOtpToUser(Long userId, String email) {
		OtpTokenEntity otpEntity = otpTokenRepository.findByUserIdAndOtpTypeAndReferenceId(userId, OtpType.EMAIL, email);
		return otpEntity;
	}

	public void verifyEmailOtp(Long userId, String email, String receivedOtp) {
		Assert.notNull(receivedOtp, "OTP cannot be empty");
		try {
			OtpTokenEntity otpEntity = fetchSentEmailOtpToUser(userId, email);
			if (Objects.nonNull(otpEntity) && Objects.equals(receivedOtp, otpEntity.getOtp())) {
				otpEntity.setVerified(true);
				save(otpEntity);
			} else {
				throw new ValidationException(ErrorBean.withError(AppErrors.OTP_MATCH_ERROR, "OTP did not match. Please try again", "otp"));
			}
		} catch (Exception ex) {
			logger.error("EmailOtpService :: validateEmailOtp :: exception ocurred " + ex);
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_MOBILE_ERROR, "OTP is not correct. Please try again", "otp"));
		}
	}

	public String generatePhoneOtp(Long userId, String phone, String verificationId) {
		Assert.notNull(phone, "phone cannot be null");
		String otp = null;
		Date expiry = AuthUtils.getUserOtpExpiry();

		List<OtpTokenEntity> otpEntities = otpTokenRepository.findByOtpTypeAndReferenceIdOrderByIdDesc(OtpType.PHONE, phone);
		OtpTokenEntity otpEntity = getExistingOtpEntity(otpEntities);
		if (otpEntity != null && !otpEntity.isVerified() && otpEntity.getExpiry().after(DateUtils.getDateTime())) {
			otpEntity.setExpiry(expiry);
			sendOtp(phone, otpEntity.getOtp());
			save(otpEntity);
			return otpEntity.getOtp();
		} else if (otpEntity != null) {
			otpEntity.setActive(0);
			save(otpEntity);
		}
		otpEntity = OtpTokenEntity.newInstance();
		otpEntity.setOtpType(OtpType.PHONE);
		otpEntity.setReferenceId(phone);
		otpEntity.setUserId(userId);
		if (StringUtils.isNotEmpty(verificationId)) {
			otpEntity.setVerificationId(verificationId);
			otpEntity.setProvider(CommonConstants.AuthTypes.FIREBASE);
		} else {
			if (isTestUser(phone)) {
				otp = AuthUtils.generateTestUserOtp();
			} else {
				otp = AuthUtils.generateUserOtp();
			}
			otpEntity.setProvider(CommonConstants.AuthTypes.SYSTEM);
		}
		otpEntity.setExpiry(expiry);
		otpEntity.setOtp(otp);
		otpEntity.setVerified(false);
		sendOtp(phone, otpEntity.getOtp());
		save(otpEntity);
		return otpEntity.getOtp();
	}

	private boolean isTestUser(String phone) {
		String testUsers = ParamsUtils.getParam("TEST_USERS");
		Set<String> testUserList = new HashSet<>(List.of(testUsers.split(",")));
		return testUserList.contains(phone);
	}

	private void sendOtp(String phone, String otp) {
		if (!ContextUtils.isProfileProd() || StringUtils.isEmpty(otp)) {
			return;
		}
		CompletableFuture.runAsync(() -> {
			String whatsappOTPText = ParamsUtils.getParam("WHATSAPP_OTP_PROMPT", "Your OTP is %s. It is valid for 5 minutes.");
			WhapiSendTextRequest request = WhapiSendTextRequest.builder()
					.to(phone)
					.body(String.format(whatsappOTPText, otp))
					.build();
			clientService.sendWhatsappText(request);
		});
	}

	private OtpTokenEntity getExistingOtpEntity(List<OtpTokenEntity> otpEntities) {
		OtpTokenEntity otpEntity = null;
		if (CollectionUtils.isNotEmpty(otpEntities)) {
			otpEntity = otpEntities.get(0);
			otpEntities.remove(0);
			otpEntities.stream().forEach(o -> {
				o.setActive(0);
				save(o);
			});
		}
		return otpEntity;
	}

	public boolean verifyPhoneOtp(Long userId, String phone, String otp) {
		Assert.notNull(phone, "phone cannot be null");
		List<OtpTokenEntity> otpEntities = otpTokenRepository.findByOtpTypeAndReferenceIdOrderByIdDesc(OtpType.PHONE, phone);
		OtpTokenEntity otpEntity = getExistingOtpEntity(otpEntities);
		if (otpEntity == null || otpEntity.isVerified()) {
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_ERROR, "OTP not generated or already verified. Please retry", "otp"));
		} else if (otpEntity.getExpiry().before(DateUtils.getDateTime())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_EXPIRED_ERROR, "OTP has expired. Please retry", "otp"));
		} else if (otpEntity.getAttempts() > ParamsUtils.getIntegerParam("MAX_OTP_ATTEMPT", 5)
				&& DateUtils.addMinutes(otpEntity.getModifiedOn(), 5).before(DateUtils.getDateTime())) {
			throw new ValidationException(ErrorBean.withError(AppErrors.OTP_MAX_ATTEMPT, "Your login attempt exceed. Please try again after 5 min", "otp"));
		} else {
			if (otpEntity.getProvider().equals(CommonConstants.AuthTypes.FIREBASE)) {
				if (!clientService.verifyFirebaseOtp(otpEntity.getVerificationId(), otp)) {
					saveUnsuccessfulAttempts(otpEntity);
					throw new ValidationException(ErrorBean.withError(AppErrors.OTP_MATCH_ERROR, "OTP did not match. Please try again", "otp"));
				}
			} else if (!otpEntity.getOtp().equals(otp)) {
				saveUnsuccessfulAttempts(otpEntity);
				throw new ValidationException(ErrorBean.withError(AppErrors.OTP_MATCH_ERROR, "OTP did not match. Please try again", "otp"));
			}
		}
		otpEntity.setAttempts(0);
		otpEntity.setVerified(true);
		otpEntity.setActive(0);
		save(otpEntity);
		return true;
	}

	private void saveUnsuccessfulAttempts(OtpTokenEntity otpEntity) {
		otpEntity.setAttempts(otpEntity.getAttempts() + 1);
		save(otpEntity);
	}

	public boolean verifyPassword(final String userType, final String requestPassword, final String entityPassword) {
		final String requestPasswordHash = AuthUtils.getHashPassword(userType, requestPassword);
		if (!requestPasswordHash.equals(entityPassword)) {
			throw new ValidationException(ErrorBean.withError(AppErrors.INCORRECT_PASSWORD, "Incorrect Password", "password"));
		}
		return true;
	}

	@Override
	public BaseCrudRepository<?, ?> getRepository() {
		return otpTokenRepository;
	}
}
