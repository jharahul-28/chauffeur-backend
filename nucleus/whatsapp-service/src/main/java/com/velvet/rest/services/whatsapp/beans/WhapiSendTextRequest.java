package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiSendTextRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private String to;

	@NotNull
	private String body;

	@JsonProperty("typing_time")
	@Builder.Default
	private int typingTime = 0;
}
