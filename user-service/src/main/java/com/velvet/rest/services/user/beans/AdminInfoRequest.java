package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Bean to be returned with Contains Application Level Admin User Info Request
 *
 * @author Mohit
 * @version $Id: $Id
 */
@ApiModel(description = "Admin User Info Request")
public class AdminInfoRequest implements Serializable {

	private static final long serialVersionUID = 3254818673615784014L;

	@ApiModelProperty(value = "Firstname of Admin User", allowEmptyValue = false, required = true)
	@NotEmpty
	private String firstname;

	@ApiModelProperty(value = "Lastname of Admin User", allowEmptyValue = false, required = true)
	@NotEmpty
	private String lastname;

	@ApiModelProperty(value = "Phone no. of user", allowEmptyValue = false, required = true)
	@NotEmpty
	private String phone;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public static AdminInfoRequest newInstance() {
		return new AdminInfoRequest();
	}
}
