package com.velvet.rest.services.user.clients;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.services.user.beans.FirebaseOTPAuthRequest;
import com.velvet.rest.services.user.beans.GoogleUser;
import com.velvet.rest.services.user.beans.OrgAddressResponse;
import com.velvet.rest.services.user.beans.OrgBasicResponse;
import com.velvet.rest.services.user.beans.WalletResponse;
import com.velvet.rest.services.user.beans.WhapiSendTextRequest;
import com.velvet.rest.services.user.errors.AppErrors;

import lombok.Getter;

/**
 * @author mohitaggarwal
 */
@Service
public class UserClientService {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(UserClientService.class);

	@Autowired
	private FirebaseClient firebaseClient;

	@Autowired
	private GoogleClient googleClient;

	@Autowired
	private OrgServiceClient orgServiceClient;

	@Autowired
	private WhatsappServiceClient whatsappClient;

	@Autowired
	private PaymentServiceClient paymentServiceClient;

	@Value("${client.firebase.key}")
	@Getter
	private String firebaseApiKey;

	public GoogleUser getGoogleUser(String authToken) {
		try {
			ResponseEntity<GoogleUser> response = googleClient.oauth(authToken);
			return response.getBody();
		} catch (Exception exc) {
			_LOGGER.error("UserClientService.getGoogleUser :: Error while verifying google oauth", exc);
		}
		return null;
	}

	public Boolean verifyFirebaseOtp(String verificationId, String otp) {
		try {
			FirebaseOTPAuthRequest request = new FirebaseOTPAuthRequest(verificationId, otp);
			ResponseEntity<Object> response = firebaseClient.verifyOtp(firebaseApiKey, request);
			if (response.getStatusCode() == HttpStatus.OK) {
				return true;
			}
		} catch (HttpClientErrorException exc) {
			_LOGGER.error("Error while verifying Firebase OTP", exc);
		}
		return false;
	}

	public OrgAddressResponse getOrgAddress(Long orgAddressId) {
		try {
			ResponseEntity<OrgAddressResponse> response = orgServiceClient.getOrgAddress(orgAddressId);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception exc) {
			_LOGGER.error("Error while fetching org address", exc);
		}
		throw new ValidationException(ErrorBean.withError(AppErrors.ORG_ADDRESS_NOT_FOUND, "Organisation address not found", "orgAddressId"));
	}

	public OrgBasicResponse getOrgByDomain(String domain) {
		try {
			ResponseEntity<OrgBasicResponse> response = orgServiceClient.getOrgByDomain(domain);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception exc) {
			_LOGGER.error("Error while fetching org by domain", exc);
		}
		return null;
	}

	public void sendWhatsappText(WhapiSendTextRequest request) {
		try {
			whatsappClient.sendWhatsappText(request);
		} catch (Exception e) {
			_LOGGER.error("Failed to send text message", e);
		}
	}

	public List<WalletResponse> getUserWallets(List<Long> customerIds) {
		try {
			return paymentServiceClient.getUserWallets(customerIds);
		} catch (Exception e) {
			_LOGGER.error("Error while getting wallets for users", e);
			return Collections.emptyList();
		}
	}

	public OrgBasicResponse getOrg(java.util.UUID orgId) {
		try {
			ResponseEntity<OrgBasicResponse> response = orgServiceClient.getOrgById(orgId);
			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}
		} catch (Exception e) {
			_LOGGER.error(String.format("Failed to fetch org for orgId: %s", orgId), e);
		}
		return null;
	}
}
