package com.velvet.rest.services.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.user.beans.GroupBean;
import com.velvet.rest.services.user.beans.GroupPrivilegeBean;
import com.velvet.rest.services.user.entity.GroupEntity;
import com.velvet.rest.services.user.services.GroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class GroupController.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Group Services", description = "Manage Groups related services.")
public class GroupController implements BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private BaseMapper<?, ?> groupMapper;

	/**
	 * Find all.
	 *
	 */
	@ApiOperation(value = "List all Groups in application store.", nickname = "findGroupList")
	@GetMapping("/groups")
	public ResponseEntity<List<GroupBean>> findAll(@RequestParam(required = false) String name, HttpServletRequest request) {
		final List<GroupEntity> results = groupService.findAll(getSearchParams(request, GroupEntity.class));
		if (results == null || results.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(getMapper().mapAsList(results, GroupBean.class));
	}

	/**
	 * Find one.
	 *
	 */
	@ApiOperation(value = "Get one Group with provided id.", nickname = "findOneGroup")
	@GetMapping("/groups/{id}")
	public ResponseEntity<GroupBean> findOne(@PathVariable Long id) {
		GroupEntity entity = groupService.findById(id);
		if (entity == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(getMapper().mapSrcToDest(entity, GroupBean.newInstance()));
	}

	/**
	 * <p>
	 * save.
	 * </p>
	 */
	@ApiOperation(value = "create a new Group", nickname = "createGroup")
	@PostMapping("/groups")
	@ResponseStatus(HttpStatus.CREATED)
	public Long save(@Valid @RequestBody GroupBean group, @ApiIgnore Errors errors) {
		checkError(errors);
		final GroupEntity entity = groupService.save(getMapper().mapSrcToDest(group, GroupEntity.newInstance()));
		return entity.getId();
	}

	/**
	 * <p>
	 * update.
	 * </p>
	 *
	 */
	@ApiOperation(value = "update existing group with provided id", nickname = "updateGroup")
	@PutMapping("/groups/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void update(@PathVariable Long id, @Valid @RequestBody GroupBean groupBean, @ApiIgnore Errors errors) {
		groupBean.setId(id);
		checkError(errors);
		final GroupEntity entity = groupService.findById(id);
		groupService.save(getMapper().mapSrcToDest(groupBean, entity));
	}

	/**
	 * <p>
	 * update group privilege.
	 * </p>
	 *
	 */
	@ApiOperation(value = "update group privileges with provided id", nickname = "updateGroupPrivileges")
	@PutMapping("/groups/{id}/privileges")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updatePrivileges(@PathVariable Long id, @Valid @RequestBody GroupPrivilegeBean groupPrivileges, @ApiIgnore Errors errors) {
		checkError(errors);
		groupService.updatePrivileges(id, groupPrivileges.getPrivilegeSlugs());
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return groupMapper;
	}

}
