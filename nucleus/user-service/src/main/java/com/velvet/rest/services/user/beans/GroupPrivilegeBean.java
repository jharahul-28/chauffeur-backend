package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.velvet.rest.common.websupport.base.BaseBean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Bean to be returned with Contains Application Level Group Privilege
 *
 * @author Mohit
 * @version $Id: $Id
 */
@ApiModel(description = "Group Privilege Bean ")
public class GroupPrivilegeBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 4528743989501148249L;

	@ApiModelProperty(value = "List of Privilege_Slug of Group", allowEmptyValue = false, required = true)
	@NotEmpty
	private List<String> privilegeSlugs;

	public List<String> getPrivilegeSlugs() {
		return privilegeSlugs;
	}

	public void setPrivilegeSlugs(List<String> privilegeSlugs) {
		this.privilegeSlugs = privilegeSlugs;
	}

}
