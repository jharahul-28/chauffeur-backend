package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ChauffeurMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer numberOfRides = 0;

	private Double workingHours = 0.0;

	private Integer ratingCount = 0;

	private Integer drivingStartYear;

	private Integer luxuryDrivingStartYear;

	private List<String> luxuryBrandsDriven = new ArrayList<>();
}
