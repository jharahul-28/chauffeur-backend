package com.velvet.rest.services.user.services;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.entity.GroupEntity;
import com.velvet.rest.services.user.entity.GroupPrivilegeEntity;
import com.velvet.rest.services.user.errors.AppErrors;
import com.velvet.rest.services.user.repository.GroupPrivilegeRepository;
import com.velvet.rest.services.user.repository.GroupRepository;

/**
 * Created by mohit on 27.4.20.
 */
@Service
public class GroupService implements BaseService<GroupEntity> {

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private GroupPrivilegeRepository groupPrivilegeRepository;

	public GroupEntity findById(Long id) {
		GroupEntity result = findRecordById(id);
		return result;
	}

	public List<GroupEntity> findByIds(List<Long> ids) {
		List<GroupEntity> result = findRecordsByIds(ids);
		return result;
	}

	public GroupEntity save(GroupEntity entity) {
		GroupEntity result = groupRepository.save(entity);
		return result;
	}

	public Iterable<GroupEntity> saveAll(List<GroupEntity> entities) {
		Iterable<GroupEntity> result = groupRepository.saveAll(entities);
		return result;
	}

	public List<GroupEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	@Override
	public Class<GroupEntity> getEntity() {
		return GroupEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return groupRepository;
	}

	public GroupEntity findGroupByName(String name) {
		GroupEntity result = groupRepository.findByName(name);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePrivileges(Long groupId, List<String> privilegeSlugs) {
		GroupEntity group = findById(groupId);
		Set<GroupPrivilegeEntity> privileges = new HashSet<>();
		if (group != null && CollectionUtils.isNotEmpty(privilegeSlugs)) {
			groupPrivilegeRepository.deleteByGroup(group);
			privilegeSlugs.parallelStream().forEach(p -> privileges.add(new GroupPrivilegeEntity(group, p)));
			groupPrivilegeRepository.saveAll(privileges);
		} else {
			throw new ValidationException(ErrorBean.withError(AppErrors.GROUP_NOT_FOUND, " Group is not present. Please contact support.", "active"));
		}
	}
}