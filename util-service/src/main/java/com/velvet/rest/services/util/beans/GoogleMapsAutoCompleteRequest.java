
package com.velvet.rest.services.util.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class GoogleMapsAutoCompleteRequest {

	public String input;

	public ArrayList<String> includedPrimaryTypes;

	public LocationBias locationBias;

	@Data
	@Builder
	public static class Circle {

		public Location center;

		public double radius;
	}

	@Data
	@Builder
	public static class LocationBias {

		public Circle circle;
	}

}
