package com.velvet.rest.services.util.beans;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class AppConfigResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String platform;
	private String version;
	private boolean forceUpdate;
	private boolean softUpdate;
	private String latestVersion;
	private String minSupportedVersion;
	private Map<String, String> labels;
	private Map<String, String> phones;
	private Map<String, Object> dynamicFields;
}
