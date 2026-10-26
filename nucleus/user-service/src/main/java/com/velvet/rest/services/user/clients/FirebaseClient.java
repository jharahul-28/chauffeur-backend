package com.velvet.rest.services.user.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.velvet.rest.services.user.beans.FirebaseOTPAuthRequest;

@FeignClient(value = "FirebaseClient", url = "${client.firebase.url}")
public interface FirebaseClient {

	@PostMapping(value = "/relyingparty/verifyPhoneNumber")
	ResponseEntity<Object> verifyOtp(@RequestParam String key, @RequestBody FirebaseOTPAuthRequest request);

}