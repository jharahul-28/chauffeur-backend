package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChauffeurBankDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountHolderName;
	private String accountNumber;
	private String ifscCode;
	private String bankName;
	private String accountType; // SAVINGS or CURRENT
}
