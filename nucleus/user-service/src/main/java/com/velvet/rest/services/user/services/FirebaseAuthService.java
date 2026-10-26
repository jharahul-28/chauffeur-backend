package com.velvet.rest.services.user.services;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.services.user.beans.FirebaseOTPAuthRequest;

/**
 * Created by mohit on 22.6.21.
 */
@Service
public class FirebaseAuthService {
	private static final AppLogger _LOGGER = LoggingManager.getLogger(FirebaseAuthService.class);

	private RestTemplate restTemplate = new RestTemplate();

	private String firebaseApiKey = ParamsUtils.getParam("FIREBASE_AUTH_KEY");
	private String firebaseOTPVerifyURL = ParamsUtils.getParam("FIREBASE_OTP_VERIFY_URL");

	public Boolean firebaseOtpAuthVerify(FirebaseOTPAuthRequest request) {
		try {
			URI uri = UriComponentsBuilder.fromUriString(firebaseOTPVerifyURL).queryParam("key", firebaseApiKey).build().toUri();
			ResponseEntity<Object> response = restTemplate.postForEntity(uri, request, Object.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				return true;
			}
		} catch (HttpClientErrorException exc) {
			_LOGGER.error(exc);
			return false;
		}
		return false;
	}
}