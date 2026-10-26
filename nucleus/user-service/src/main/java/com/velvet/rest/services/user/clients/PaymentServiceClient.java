package com.velvet.rest.services.user.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.velvet.rest.common.openfeign.FeignInternalConfiguration;
import com.velvet.rest.services.user.beans.WalletResponse;

@FeignClient(value = "PaymentServiceUserClient", url = "${client.payment.url}", configuration = { FeignInternalConfiguration.class })
public interface PaymentServiceClient {

	@GetMapping(value = "/payments/wallets/users")
	List<WalletResponse> getUserWallets(@RequestParam List<Long> userIds);

}
