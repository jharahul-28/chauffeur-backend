package com.velvet.rest.services.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.AdminInfoRequest;
import com.velvet.rest.services.user.beans.AdminUserBean;
import com.velvet.rest.services.user.beans.UserGroupRequest;
import com.velvet.rest.services.user.entity.AdminUserEntity;
import com.velvet.rest.services.user.services.AdminUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class AdminController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Admins Services", description = "Manage Admins related services.")
public class AdminController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminUserService adminService;

	@Autowired
	private BaseMapper<?, ?> adminMapper;

	private static String USER_TYPE = CommonConstants.UserTypes.ADMIN;

	/**
	 * Find all.
	 *
	 */
	@ApiOperation(value = "List all Admin in application admin.", nickname = "findAdminList")
	@GetMapping("/admins")
	public ResponseEntity<List<AdminUserBean>> findAll(HttpServletRequest request) {
		final List<AdminUserEntity> results = adminService.findAll(getSearchParams(request, AdminUserEntity.class));
		if (results == null || results.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(getMapper().mapAsList(results, AdminUserBean.class));
	}

	/**
	 * get admin profile.
	 *
	 */
	@ApiOperation(value = "Get Admin Profile.", nickname = "findAdminProfile")
	@GetMapping("/admins/profile")
	public ResponseEntity<AdminUserBean> getAdminProfile() {
		AdminUserEntity entity = adminService.getAuthEntity();
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(getMapper().mapSrcToDest(entity, AdminUserBean.newInstance()));
	}

	/**
	 * Find one.
	 *
	 */
	@ApiOperation(value = "Get one Admin with provided id.", nickname = "findOneAdminById")
	@GetMapping("/admins/{id}")
	public ResponseEntity<AdminUserBean> findOne(@PathVariable Long id) {
		AdminUserEntity entity = adminService.findById(id);
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(getMapper().mapSrcToDest(entity, AdminUserBean.newInstance()));
	}

	/**
	 * <p>
	 * save.
	 * </p>
	 */
	@ApiOperation(value = "register new Admin User", nickname = "registerAdminUser")
	@PostMapping("/admins")
	@ResponseStatus(HttpStatus.CREATED)
	public void save(@Valid @RequestBody AdminUserBean admin, @ApiIgnore Errors errors) {
		checkError(errors);
		adminService.registerUser(adminMapper.mapSrcToDest(admin, AdminUserEntity.newInstance()), USER_TYPE);
	}

	/**
	 * Update AdminUser's Address.
	 *
	 */
	@ApiOperation(value = "Update AdminUser's Info.", nickname = "updateAdminUserInfo")
	@PutMapping("/admins/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateProfileInfo(@PathVariable Long id, @Valid @RequestBody AdminInfoRequest bean, @ApiIgnore Errors errors) {
		checkError(errors);
		adminService.updateInfo(id, bean.getFirstname(), bean.getLastname(), bean.getPhone());
	}

	/**
	 * Update AdminUser's Address.
	 *
	 */
	@ApiOperation(value = "Update Admin's Groups.", nickname = "updateAuthAdminUserGroups")
	@PutMapping("/admins/{id}/groups")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateProfileGroups(@PathVariable Long id, @Valid @RequestBody UserGroupRequest request, @ApiIgnore Errors errors) {
		checkError(errors);
		adminService.updateGroups(id, request.getGroupIds());
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return adminMapper;
	}

}
