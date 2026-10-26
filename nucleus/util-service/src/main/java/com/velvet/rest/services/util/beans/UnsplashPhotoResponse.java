package com.velvet.rest.services.util.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnsplashPhotoResponse {

	private String id;
	private UnsplashPhotoUrls urls;
	private UnsplashPhotoLinks links;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UnsplashPhotoUrls {
		private String raw;
		private String full;
		private String regular;
		private String small;
		private String thumb;
	}

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UnsplashPhotoLinks {
		private String self;
		private String html;
		private String download;
		private String downloadLocation;
	}
}
