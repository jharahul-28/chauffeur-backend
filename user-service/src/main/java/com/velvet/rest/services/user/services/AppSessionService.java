package com.velvet.rest.services.user.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.beans.SessionBean;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.DateUtils;
import com.velvet.rest.common.utils.ParamsUtils;
import com.velvet.rest.common.utils.SessionUtils;
import com.velvet.rest.common.utils.SessionUtils.ApplicationId;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.entity.AppSessionEntity;
import com.velvet.rest.services.user.repository.AppSessionRepository;

/**
 * Created by mohit on 27.4.20.
 */
@Service
public class AppSessionService implements BaseService<AppSessionEntity> {

	@Autowired
	private AppSessionRepository appSessionRepository;

	public AppSessionEntity findById(Long id) {
		AppSessionEntity result = findRecordById(id);
		return result;
	}

	public AppSessionEntity findByKey(String key) {
		AppSessionEntity result = appSessionRepository.findBySessionKey(key);
		return result;
	}

	public List<AppSessionEntity> findByUserId(Long userId) {
		List<AppSessionEntity> result = appSessionRepository.findByUserIdOrderByModifiedOnAsc(userId);
		return result;
	}

	public AppSessionEntity save(AppSessionEntity entity) {
		AppSessionEntity result = appSessionRepository.save(entity);
		return result;
	}

	public String doLogin(Long userId, String userType, ApplicationId appId, Integer allowedSessions) {
		final String authToken = SessionUtils.createAuthToken(userType, userId, appId);
		List<AppSessionEntity> existingSessions = findByUserId(userId);
		AppSessionEntity newSession;
		if (CollectionUtils.isEmpty(existingSessions)) {
			existingSessions = new ArrayList<>();
			newSession = AppSessionEntity.newInstance(userId, authToken);
			existingSessions.add(newSession);
		} else if (existingSessions.size() < allowedSessions) {
			newSession = AppSessionEntity.newInstance(userId, authToken);
			existingSessions.add(newSession);
		} else {
			existingSessions.get(0).setSessionKey(authToken);
			newSession = existingSessions.get(0);
		}
		newSession.setExpiry(SessionUtils.getTimeUnitForSessionInMinutes(ParamsUtils.getParam(CommonConstants.AUTH_TOKEN_VALIDITY + StringUtils.upperCase(userType))));
		newSession.setLastAccessed(DateUtils.getDateTime());
		appSessionRepository.saveAll(existingSessions);

		// create SessionBean
		saveSession(userId, userType, authToken);

		return authToken;
	}

	public void doLogout(Long userId) {
		final String authToken = SessionUtils.getAuthToken();
		AppSessionEntity session = findByKey(authToken);
		if (session != null) {
			session.setExpiry(DateUtils.getDateTime());
			save(session);
		}
	}

	public void doLogoutByUserId(Long userId) {
		List<AppSessionEntity> sessions = findByUserId(userId);
		if (CollectionUtils.isNotEmpty(sessions)) {
			sessions.forEach(s -> s.setExpiry(DateUtils.getDateTime()));
			appSessionRepository.saveAll(sessions);
		}
	}

	private void saveSession(Long userId, String userType, String authToken) {
		SessionBean sessionBean = new SessionBean();
		sessionBean.setUserType(userType);
		sessionBean.setUserId(userId);
		sessionBean.setAuthToken(authToken);
		SessionUtils.setSessionBean(sessionBean);
	}

	@Override
	public Class<AppSessionEntity> getEntity() {
		return AppSessionEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return appSessionRepository;
	}

}