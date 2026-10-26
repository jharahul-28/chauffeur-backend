package com.velvet.rest.services.user.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortRequest.SortDirection;
import com.velvet.rest.common.dbsupport.pagination.PageAndSortResult;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.user.beans.OrgAddressResponse;
import com.velvet.rest.services.user.beans.UserAddressRequest;
import com.velvet.rest.services.user.clients.UserClientService;
import com.velvet.rest.services.user.entity.UserAddressEntity;
import com.velvet.rest.services.user.repository.UserAddressRepository;

/**
 * Service class for UserAddress operations.
 *
 * @author mohit
 */
@Service
public class UserAddressService implements BaseService<UserAddressEntity> {

	private static final AppLogger logger = LoggingManager.getLogger(UserAddressService.class);

	@Autowired
	private UserAddressRepository userAddressRepository;

	@Autowired
	private AuthUserService authUserService;

	@Autowired
	private UserClientService userClientService;

	/**
	 * Find address by ID.
	 */
	public UserAddressEntity findById(Integer id) {
		return userAddressRepository.findById(id).orElse(null);
	}

	/**
	 * Save address entity.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public UserAddressEntity save(UserAddressEntity entity) {
		return userAddressRepository.save(entity);
	}

	/**
	 * Save all address entities.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Iterable<UserAddressEntity> saveAll(List<UserAddressEntity> entities) {
		return userAddressRepository.saveAll(entities);
	}

	/**
	 * Find all addresses with filters.
	 */
	public List<UserAddressEntity> findAll(Map<String, Object> filters) {
		return findAllRecords(filters);
	}

	/**
	 * Find all addresses with pagination.
	 */
	public PageAndSortResult<UserAddressEntity> findAllByPage(Integer pageSize, Integer pageNo, Map<String, Object> filters, Map<String, SortDirection> sort) {
		PageAndSortResult<UserAddressEntity> result = null;
		if (CollectionUtils.isEmpty(filters)) {
			filters = new HashMap<>();
		}
		try {
			result = findPagedRecords(filters, sort, pageSize, pageNo);
		} catch (Exception e) {
			logger.error(e);
			throw new ValidationException(ErrorBean.withError("FETCH_ERROR", e.getMessage(), null));
		}
		return result;
	}

	/**
	 * Find all active addresses for a user.
	 */
	public UserAddressEntity findByUserId(Long userId) {
		return userAddressRepository.findByUserId(userId);
	}

	/**
	 * Find addresses by organisation.
	 */
	public List<UserAddressEntity> findByOrgId(UUID orgId) {
		return userAddressRepository.findByOrgId(orgId);
	}

	/**
	 * Find addresses by org address ID.
	 */
	public List<UserAddressEntity> findByOrgAddressId(Integer orgAddressId) {
		return userAddressRepository.findByOrgAddressId(orgAddressId);
	}

	/**
	 * Find address by user ID and org address ID.
	 */
	public UserAddressEntity findByUserIdAndOrgAddressId(Long userId, Integer orgAddressId) {
		return userAddressRepository.findByUserIdAndOrgAddressId(userId, orgAddressId);
	}

	/**
	 * Get current user's addresses.
	 */
	public UserAddressEntity getMyAddress() {
		Long userId = authUserService.getSessionUserId();
		return findByUserId(userId);
	}

	/**
	 * Create a new address for current user.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public UserAddressEntity create(UserAddressEntity entity) {
		Long userId = authUserService.getSessionUserId();
		entity.setUserId(userId);
		entity.setActive(1);
		return save(entity);
	}

	/**
	 * Create or update user address from org address request. Calls org service via
	 * REST to get org address details.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public UserAddressEntity updateAddress(Long userId, UserAddressRequest request) {
		// Get org address via REST call
		OrgAddressResponse orgAddress = userClientService.getOrgAddress(request.getOrgAddressId());

		// Check if user already has address for this org address
		UserAddressEntity existing = findByUserIdAndOrgAddressId(userId, orgAddress.getId());
		// Soft delete existing
		if (existing != null) {
			softDelete(existing);
		}
		// Create new address
		UserAddressEntity entity = UserAddressEntity.newInstance();
		entity.setUserId(userId);
		entity.setOrgId(orgAddress.getOrgId());
		entity.setOrgAddressId(orgAddress.getId());
		entity.setOrgAddress(orgAddress.getFullAddress());
		entity.setTower(request.getTower());
		entity.setFloor(request.getFloor());
		return save(entity);
	}

	/**
	 * Update an existing address.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public UserAddressEntity update(Integer id, UserAddressEntity updatedEntity) {
		UserAddressEntity existing = findById(id);
		if (existing == null) {
			throw new ValidationException(ErrorBean.withError("ADDRESS_NOT_FOUND", "Address not found", "id"));
		}

		// Verify ownership
		Long userId = authUserService.getSessionUserId();
		if (!existing.getUserId().equals(userId)) {
			throw new ValidationException(ErrorBean.withError("UNAUTHORIZED", "You are not authorized to update this address", "id"));
		}

		existing.setOrgId(updatedEntity.getOrgId());
		existing.setOrgAddressId(updatedEntity.getOrgAddressId());
		existing.setOrgAddress(updatedEntity.getOrgAddress());
		existing.setTower(updatedEntity.getTower());
		existing.setFloor(updatedEntity.getFloor());

		return save(existing);
	}

	/**
	 * Soft delete address (set active = 0).
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public UserAddressEntity softDelete(Integer id) {
		UserAddressEntity entity = findById(id);
		if (entity == null) {
			throw new ValidationException(ErrorBean.withError("ADDRESS_NOT_FOUND", "Address not found", "id"));
		}

		// Verify ownership
		Long userId = authUserService.getSessionUserId();
		if (!entity.getUserId().equals(userId)) {
			throw new ValidationException(ErrorBean.withError("UNAUTHORIZED", "You are not authorized to delete this address", "id"));
		}

		if (entity.getActive() != null && entity.getActive() == 0) {
			throw new ValidationException(ErrorBean.withError("ADDRESS_ALREADY_INACTIVE", "Address is already inactive", "id"));
		}

		entity.setActive(0);
		save(entity);

		logger.info(String.format("User address %s has been deactivated", id));
		return entity;
	}

	@Override
	public Class<UserAddressEntity> getEntity() {
		return UserAddressEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return userAddressRepository;
	}
}
