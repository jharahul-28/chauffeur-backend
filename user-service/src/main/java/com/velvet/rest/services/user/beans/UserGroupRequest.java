package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Bean for UserGroupRequest
 *
 * @author Mohit
 * @version $Id: $Id
 */
@ApiModel(description = "User Group Request Bean ")
public class UserGroupRequest implements Serializable {

	private static final long serialVersionUID = 8069446705163629325L;

	@ApiModelProperty(value = "List of Group Ids", allowEmptyValue = false, required = true)
	@NotEmpty
	private List<Long> groupIds;

	public List<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public static UserGroupRequest newInstance() {
		return new UserGroupRequest();
	}
}
