package com.velvet.rest.services.whatsapp.beans;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class OrgResponse {

	private UUID id;

	private String name;

	private List<DayOfWeek> workingDays;

	private String whatsappGroupLink;
}
