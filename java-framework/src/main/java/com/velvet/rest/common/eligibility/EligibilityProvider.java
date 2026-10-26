package com.velvet.rest.common.eligibility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.velvet.rest.common.beans.Endpoint;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

@Component
class EligibilityProvider {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(EligibilityProvider.class);

	@Autowired
	@Qualifier(CommonConstants.NO_SESSION_ENDPOINTS_QUALIFIER)
	private List<Endpoint> noSessionEndpoints;

	@Autowired
	private EligibilityHelper helper;

	public List<Endpoint> getNoSessionEndpoints() {
		if (noSessionEndpoints == null || noSessionEndpoints.isEmpty()) {
			_LOGGER.debug("noSessionEndpoints is " + noSessionEndpoints);
			noSessionEndpoints = helper.getNoSessionEndpoints();
		}
		return noSessionEndpoints;
	}

	public List<Endpoint> getGroupEndpoints(Long userId, String[] handlerPaths, String httpMethod) {
		return helper.getUserGroupEndpoints(userId, handlerPaths, httpMethod);
	}

	public boolean validateAuthKey(String authHeader, Long userId) {
		int result = helper.validateAuthKey(authHeader, userId);
		if (result > 0) {
			return true;
		}
		return false;
	}
}
