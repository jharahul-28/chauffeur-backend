package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChauffeurAddress implements Serializable {

	private static final long serialVersionUID = 1L;

	private AddressDetail currentAddress;
	private AddressDetail permanentAddress;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AddressDetail implements Serializable {

		private static final long serialVersionUID = 1L;

		private String houseNo;
		private String street;
		private String city;
		private String state;
		private String pincode;
		private String country;
	}
}
