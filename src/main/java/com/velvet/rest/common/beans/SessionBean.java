package com.velvet.rest.common.beans;

import java.io.Serializable;

/**
 * <p>
 * SessionBean class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class SessionBean implements Serializable {

	private static final long serialVersionUID = 4599413762862017279L;

	private Long userId;

	private String userType;

	private String authToken;

	/**
	 * <p>
	 * Getter for the field <code>id</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getUserId() {
		return userId;
	}

	/**
	 * <p>
	 * Setter for the field <code>id</code>.
	 * </p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * <p>
	 * Getter for the field <code>authToken</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * <p>
	 * Setter for the field <code>authToken</code>.
	 * </p>
	 *
	 * @param authToken a {@link java.lang.String} object.
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * <p>
	 * Getter for the field <code>userType</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * <p>
	 * Setter for the field <code>userType</code>.
	 * </p>
	 *
	 * @param userType a {@link java.lang.String} object.
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
}
