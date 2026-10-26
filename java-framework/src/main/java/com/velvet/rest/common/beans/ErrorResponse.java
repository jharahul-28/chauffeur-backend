package com.velvet.rest.common.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.exceptions.JSONMappingException;

/**
 * The Class ErrorResponse.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class ErrorResponse implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4416805417925350195L;

	/** The message. */
	private String message;

	/** The errors. */
	private List<ErrorBean> errors;

	/**
	 * Instantiates a new error response.
	 */
	public ErrorResponse() {
	}

	/**
	 * Creates the.
	 *
	 * @return the error response
	 */
	public static ErrorResponse create() {
		return new ErrorResponse();
	}

	/**
	 * Message.
	 *
	 * @param message the message
	 * @return the error response
	 */
	public ErrorResponse message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Errors.
	 *
	 * @param errors the errors
	 * @return the error response
	 */
	public ErrorResponse errors(List<ErrorBean> errors) {
		this.errors = errors;
		return this;
	}

	/**
	 * Adds the error.
	 *
	 * @param error the error
	 * @return the error response
	 */
	public ErrorResponse addError(ErrorBean error) {
		if (this.errors == null) {
			this.errors = new ArrayList<>();
		}
		this.errors.add(error);
		return this;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the errors.
	 *
	 * @return the errors
	 */
	public List<ErrorBean> getErrors() {
		return errors;
	}

	/**
	 * Sets the errors.
	 *
	 * @param errors the new errors
	 */
	public void setErrors(List<ErrorBean> errors) {
		this.errors = errors;
	}

	/**
	 * Instantiates a new error response.
	 *
	 * @param message the message
	 * @param errors the errors
	 */
	@JsonCreator
	public ErrorResponse(@JsonProperty("message") String message,
			@JsonProperty("errors") List<Map<String, Object>> errors) {
		this.message = message;
		if (!CollectionUtils.isEmpty(errors)) {
			this.errors = new ArrayList<>();
			ObjectMapper mapper = new ObjectMapper();
			for (Map<String, Object> error : errors) {
				try {
					ErrorBean bean = mapper.readValue(mapper.writeValueAsString(error), ErrorBean.class);
					this.errors.add(bean);
				} catch (IOException e) {
					throw new JSONMappingException("Error Creating Bean Error Response : " + e.getMessage());
				}
			}
		}
	}

}
