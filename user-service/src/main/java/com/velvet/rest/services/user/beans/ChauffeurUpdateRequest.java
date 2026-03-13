package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.velvet.rest.services.user.constants.UserConstants.Gender;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Request body for updating a chauffeur")
@Data
public class ChauffeurUpdateRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "First name", required = true)
	@NotBlank
	private String firstName;

	@ApiModelProperty(value = "Last name", required = true)
	@NotBlank
	private String lastName;

	@ApiModelProperty(value = "Email address", required = true)
	@NotBlank
	private String email;

	@ApiModelProperty(value = "Gender", required = true)
	@NotNull
	private Gender gender;

	@ApiModelProperty(value = "Driver's license number")
	private String licenseNumber;

	@ApiModelProperty(value = "Date of birth")
	private LocalDate dateOfBirth;

	@ApiModelProperty(value = "Chauffeur documents (license, ID, etc.)")
	private ChauffeurDocuments documents;

	@ApiModelProperty(value = "Rating")
	private Double rating;

	@ApiModelProperty(value = "Chauffeur metadata (working hours, etc.)")
	private ChauffeurMetadata metadata;
}
