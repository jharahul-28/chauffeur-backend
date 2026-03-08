package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.velvet.rest.services.user.constants.UserConstants.Gender;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Chauffeur profile details")
public class ChauffeurProfileBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String firstName;

	private String lastName;

	private String phone;

	private String email;

	private Gender gender;

	private LocalDate dateOfBirth;

	private Double rating;

	private Integer numberOfRides;

	public static ChauffeurProfileBean newInstance() {
		return new ChauffeurProfileBean();
	}
}
