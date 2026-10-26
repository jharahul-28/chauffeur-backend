package com.velvet.rest.services.user.beans;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * Bean to be returned with Contains Customer Level User
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@ToString
@ApiModel(description = "Customer details")
public class CustomerResponse {

	private Long id;

	private String name;

	private String username;

	private String profilePic;

	private String bio;

	private String city;

	public static CustomerResponse newInstance() {
		return new CustomerResponse();
	}

}
