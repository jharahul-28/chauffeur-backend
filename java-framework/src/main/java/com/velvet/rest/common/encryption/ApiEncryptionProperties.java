package com.velvet.rest.common.encryption;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "velvet.api-encryption")
public class ApiEncryptionProperties {

	private boolean enabled = false;

	private List<String> applicableUrls = new ArrayList<>();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<String> getApplicableUrls() {
		return applicableUrls;
	}

	public void setApplicableUrls(List<String> applicableUrls) {
		this.applicableUrls = applicableUrls;
	}
}
