package com.velvet.rest.services.util.beans;

import lombok.Data;

@Data
public class CityResponse {

	private Long id;

	private String name;

	private String state;

	private String country;

	private String countryId;

	private Double latitude;

	private Double longitude;

	private String phoneCode;

	private String image;
}