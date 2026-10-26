package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Firebase otp auth request bean
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Data
@AllArgsConstructor
public class FirebaseOTPAuthRequest implements Serializable {

	private static final long serialVersionUID = -7916847028665698793L;

	private String sessionInfo;

	private String code;
}
