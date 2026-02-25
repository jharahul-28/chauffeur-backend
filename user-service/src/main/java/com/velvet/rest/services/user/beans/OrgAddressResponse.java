package com.velvet.rest.services.user.beans;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * Response bean for Organisation Address from org service.
 *
 * @author mohit
 */
@Data
public class OrgAddressResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private UUID orgId;

	private String fullAddress;

	private String city;

	private String pincode;

	private Integer active;
}
