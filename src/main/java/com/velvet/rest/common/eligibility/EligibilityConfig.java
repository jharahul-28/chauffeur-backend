package com.velvet.rest.common.eligibility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.velvet.rest.common.beans.Endpoint;
import com.velvet.rest.common.constants.CommonConstants;

@Configuration
public class EligibilityConfig {

	@Autowired
	private EligibilityHelper helper;

	@Bean(CommonConstants.NO_SESSION_ENDPOINTS_QUALIFIER)
	public List<Endpoint> noSessionEndpoints() {
		return helper.getNoSessionEndpoints();
	}
}
