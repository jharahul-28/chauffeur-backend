package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChauffeurDocuments implements Serializable {

	private static final long serialVersionUID = 1L;

	private String aadharCard;

	private String panCard;

	private String license;

}
