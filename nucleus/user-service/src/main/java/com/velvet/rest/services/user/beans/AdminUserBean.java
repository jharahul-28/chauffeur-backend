package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Bean to be returned with Contains Application Level Admin User
 *
 * @author Mohit
 * @version $Id: $Id
 */
@ApiModel(description = "Admin User Bean")
@Data
public class AdminUserBean extends UserBean implements Serializable {

	private static final long serialVersionUID = 3254818673615784014L;

	@ApiModelProperty(value = "Firstname of Admin User", allowEmptyValue = false, required = true)
	@NotEmpty
	private String firstname;

	@ApiModelProperty(value = "Lastname of Admin User", allowEmptyValue = false, required = true)
	@NotEmpty
	private String lastname;

	public static AdminUserBean newInstance() {
		return new AdminUserBean();
	}
}
