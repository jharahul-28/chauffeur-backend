package com.velvet.rest.common.beans;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;


/**
 * <p>Endpoint class.</p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class Endpoint implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2992506665582135044L;
	private String method;
	private String handler;
	private String privilegeSlugs;

	/**
	 * <p>Constructor for Endpoint.</p>
	 *
	 * @param method a {@link java.lang.String} object.
	 * @param uri a {@link java.lang.String} object.
	 * @param privilegeIds a {@link java.lang.String} object.
	 */
	public Endpoint(String method, String uri, String privilegeSlugs) {
		super();
		this.method = method;
		this.handler = uri;
		this.privilegeSlugs = privilegeSlugs;
	}

	/**
	 * <p>Getter for the field <code>method</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * <p>Setter for the field <code>method</code>.</p>
	 *
	 * @param method a {@link java.lang.String} object.
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * <p>getUri.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUri() {
		return handler;
	}

	/**
	 * <p>setUri.</p>
	 *
	 * @param uri a {@link java.lang.String} object.
	 */
	public void setUri(String uri) {
		this.handler = uri;
	}

	/**
	 * <p>Getter for the field <code>privilegeIds</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPrivilegeSlugs() {
		return privilegeSlugs;
	}

	/**
	 * <p>getPrivileges.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Long> getPrivileges() {
		if (!StringUtils.isEmpty(privilegeSlugs)) {
			return Arrays.asList(privilegeSlugs.split(",")).stream().map(id -> new Long(id)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	/**
	 * <p>Setter for the field <code>privilegeIds</code>.</p>
	 *
	 * @param privilegeIds a {@link java.lang.String} object.
	 */
	public void setPrivilegeSlugs(String privilegeIds) {
		this.privilegeSlugs = privilegeIds;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(method).append(":").append(handler);
		return builder.toString();
	}

}
