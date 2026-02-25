package com.velvet.rest.services.whatsapp.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhapiGroupResponse {

	private String id;

	private String name;

	private String type;

	private Long timestamp;

	@JsonProperty("participants")
	private List<Participant> participants;

	@JsonProperty("participants_count")
	private Integer participantsCount;

	@JsonProperty("created_at")
	private Long createdAt;

	@JsonProperty("created_by")
	private String createdBy;

	private Boolean restricted;

	private Boolean announcements;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Participant {

		private String id;

		private String rank;
	}
}
