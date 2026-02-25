package com.velvet.rest.services.user.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.beans.SessionBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.AuthenticationException;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.common.utils.SessionUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.entity.AuthUserEntity;
import com.velvet.rest.services.user.entity.GroupEntity;
import com.velvet.rest.services.user.entity.UserEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.AuthUserRepository;

/**
 * Created by mohit on 27.4.20.
 */
@Service
public class AuthUserService implements BaseService<AuthUserEntity> {

	@Autowired
	private AuthUserRepository authUserRepository;

	@Autowired
	private GroupService groupService;

	public AuthUserEntity findById(Long id) {
		AuthUserEntity result = findRecordById(id);
		return result;
	}

	public AuthUserEntity save(AuthUserEntity entity) {
		AuthUserEntity result = authUserRepository.save(entity);
		return result;
	}

	public Long createNewUser(String userType) {
		GroupEntity group = getGroupByUserType(userType);
		AuthUserEntity entity = AuthUserEntity.newInstance(userType);
		entity.addGroup(group);
		AuthUserEntity result = save(entity);
		return result.getId();
	}

	public Long createAdminUser(String userType) {
		GroupEntity group = getGroupByUserType(userType);
		AuthUserEntity entity = AuthUserEntity.newInstance(userType);
		entity.addGroup(group);
		AuthUserEntity result = save(entity);
		return result.getId();
	}

	private GroupEntity getGroupByUserType(String userType) {
		GroupEntity group = groupService.findGroupByName(userType);
		if (group == null) {
			throw new ValidationException(
					ErrorBean.withError(AppErrors.GROUP_NOT_FOUND, "Default User Group is not present. Please contact support.", "active"));
		}
		return group;
	}

	public <E extends UserEntity> E registerAppUser(String phone, String userType, E user) {
		Long userId = createNewUser(userType);
		user.setId(userId);
		user.setPhone(phone);
		user.setCountryCode("91");
		return user;
	}

	public Iterable<AuthUserEntity> saveAll(List<AuthUserEntity> entities) {
		Iterable<AuthUserEntity> result = authUserRepository.saveAll(entities);
		return result;
	}

	public List<AuthUserEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	@Override
	public Class<AuthUserEntity> getEntity() {
		return AuthUserEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return authUserRepository;
	}

	public Long getSessionUserId() {
		SessionBean session = SessionUtils.getAuthUser();
		if (session == null || session.getUserId() == null) {
			throw new AuthenticationException("User Session Expired. Please login to continue.", Errors.error(Errors.AUTHORIZATION_EXCEPTION));
		}
		final Long id = session.getUserId();
		return id;
	}

	public void updateGroups(Long userId, List<Long> groupIds) {
		List<GroupEntity> groups = groupService.findByIds(groupIds);
		if (groups != null && !groups.isEmpty()) {
			AuthUserEntity user = findById(userId);
			user.setGroups(new HashSet<>(groups));
			save(user);
		}
	}
}