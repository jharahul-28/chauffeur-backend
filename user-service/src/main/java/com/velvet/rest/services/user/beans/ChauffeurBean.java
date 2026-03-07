package com.velvet.rest.services.user.beans;

import java.sql.Date;

import com.velvet.rest.services.user.constants.UserConstants.Gender;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

/**
 * Bean to be returned for Chauffeur
 */
@Data
@ToString
@ApiModel(description = "Chauffeur details")
public class ChauffeurBean extends UserBean {

	private static final long serialVersionUID = -3969316166203486850L;

	private String name;

	private String phone;

	private String email;

	private Gender gender;

	private Date dob;

	private Integer status;

	public static ChauffeurBean newInstance() {
		return new ChauffeurBean();
	}
}
