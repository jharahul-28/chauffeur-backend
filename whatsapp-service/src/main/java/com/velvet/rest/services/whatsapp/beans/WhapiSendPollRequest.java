package com.velvet.rest.services.whatsapp.beans;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhapiSendPollRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String to;

	private String title;

	private List<String> options;
}
