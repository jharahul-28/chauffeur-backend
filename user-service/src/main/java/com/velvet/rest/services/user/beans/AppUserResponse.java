package com.velvet.rest.services.user.beans;

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
public class AppUserResponse {

	private Long id;

	private String name;

	private String username;

	private String profilePic;

	private String bio;

	private String city;

	public static AppUserResponse newInstance() {
		return new AppUserResponse();
	}

}
