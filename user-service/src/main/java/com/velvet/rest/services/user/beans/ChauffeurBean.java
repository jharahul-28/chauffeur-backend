package com.velvet.rest.services.user.beans;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.velvet.rest.services.user.entity.ChauffeurConstants.MaritalStatus;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Bean to be returned for Chauffeur
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Chauffeur details")
public class ChauffeurBean extends UserBean {

	private static final long serialVersionUID = -3969316166203486850L;

	private String firstName;

	private String lastName;

	private String licenseNumber;

	private LocalDate dateOfBirth;

	private Double rating;

	private MaritalStatus maritalStatus;

	private LocalDate dlExpiryDate;

	private Boolean hasCriminalRecord;

	private Integer totalDrivingExperience;

	private Integer luxuryDrivingExperience;

	private ChauffeurDocuments documents;

	private ChauffeurMetadata metadata;

	private ChauffeurAddress address;

	private ChauffeurBankDetails bankDetails;

	public static ChauffeurBean newInstance() {
		return new ChauffeurBean();
	}
}
