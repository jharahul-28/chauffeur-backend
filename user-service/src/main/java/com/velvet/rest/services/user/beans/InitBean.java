package com.velvet.rest.services.user.beans;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Bean to be returned with Contains Init values
 *
 * @author Mohit
 * @version $Id: $Id
 */
@ApiModel(description = "Init Response Bean")
public class InitBean implements Serializable {
	private static final long serialVersionUID = -488654431742021608L;

	@ApiModelProperty(value = "app version", readOnly = true)
	private String version;
	

	@ApiModelProperty(value = "hard update version", readOnly = true)
	private int hardUpdate;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getHardUpdate() {
		return hardUpdate;
	}

	public void setHardUpdate(int hardUpdate) {
		this.hardUpdate = hardUpdate;
	}

	public static InitBean newInstance() {
		return new InitBean();
	}
}
