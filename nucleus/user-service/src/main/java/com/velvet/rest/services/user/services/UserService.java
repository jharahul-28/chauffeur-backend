package com.velvet.rest.services.user.services;

import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.services.user.beans.AuthResponse;
import com.velvet.rest.services.user.entity.UserEntity;

public interface UserService {

	UserEntity getAuthEntity();

	void logout();

	default String appVersion(String app) {
		return ParamsUtils.getParam(app + "_VERSION");
	}

	default Integer hardUpdate(String app) {
		return ParamsUtils.getIntegerParam(app + "_HARD_UPDATE");
	}
}
