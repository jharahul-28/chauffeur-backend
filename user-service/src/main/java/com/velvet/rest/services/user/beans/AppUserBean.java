package com.velvet.rest.services.user.beans;

import java.util.UUID;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * Bean to be returned with Contains Application Level User
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ToString
@ApiModel(description = "App User details")
public class AppUserBean extends UserBean {

	private static final long serialVersionUID = -2969316166203486750L;

	private String name;

	private String profilePic;

	private Boolean emailVerified;

	private Boolean phoneVerified;

	private Integer status;

	private String referralCode;

	private UUID orgId;

	private UserAddressBean address;

	public static AppUserBean newInstance() {
		return new AppUserBean();
	}

}
