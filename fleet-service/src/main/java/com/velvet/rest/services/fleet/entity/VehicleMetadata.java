package com.velvet.rest.services.fleet.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleMetadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDate registrationExpiry;

	private LocalDate insuranceExpiry;

	private LocalDate pucExpiry;
}
