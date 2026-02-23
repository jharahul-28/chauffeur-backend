package com.velvet.rest.services.common.swagger;

import java.io.Serializable;

import com.velvet.rest.common.constants.ApplicationProfiles;
import com.velvet.rest.common.utils.ContextUtils;

/**
 * The Class SwaggerApiInfo.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class SwaggerApiInfo implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3839350191005781366L;

	/** The version. */
	private String version;

	/** The title. */
	private String title;

	/** The description. */
	private String description;

	/** The terms of service url. */
	private String termsOfServiceUrl;

	/** The license. */
	private String license;

	/** The license url. */
	private String licenseUrl;

	/** The contact. */
	private SwaggerContact contact;

	private String host_local;

	private String host_dev;

	private String host_prod;

	public String getHost() {
		String host = null;
		String profile = ContextUtils.getActiveProfile() == null ? ApplicationProfiles.LOCAL : ContextUtils.getActiveProfile();
		if (profile.equalsIgnoreCase(ApplicationProfiles.LOCAL) || profile.equalsIgnoreCase(ApplicationProfiles.TEST)) {
			host = getHost_local();
		} else if (profile.equalsIgnoreCase(ApplicationProfiles.DEVELOPMENT)) {
			host = getHost_dev();
		} else if (profile.equalsIgnoreCase(ApplicationProfiles.PRODUCTION)) {
			host = getHost_prod();
		}
		return host;
	}

	public String getHost_local() {
		return host_local;
	}

	public void setHost_local(String host_local) {
		this.host_local = host_local;
	}

	public String getHost_dev() {
		return host_dev;
	}

	public void setHost_dev(String host_dev) {
		this.host_dev = host_dev;
	}

	public String getHost_prod() {
		return host_prod;
	}

	public void setHost_prod(String host_prod) {
		this.host_prod = host_prod;
	}

	/**
	 * The Class SwaggerContact.
	 */
	public static class SwaggerContact implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4322223407299505306L;

		/** The name. */
		private String name;

		/** The url. */
		private String url;

		/** The email. */
		private String email;

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Gets the url.
		 *
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * Sets the url.
		 *
		 * @param url the new url
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * Gets the email.
		 *
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * Sets the email.
		 *
		 * @param email the new email
		 */
		public void setEmail(String email) {
			this.email = email;
		}
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 *
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the terms of service url.
	 *
	 * @return the terms of service url
	 */
	public String getTermsOfServiceUrl() {
		return termsOfServiceUrl;
	}

	/**
	 * Sets the terms of service url.
	 *
	 * @param termsOfServiceUrl the new terms of service url
	 */
	public void setTermsOfServiceUrl(String termsOfServiceUrl) {
		this.termsOfServiceUrl = termsOfServiceUrl;
	}

	/**
	 * Gets the license.
	 *
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * Sets the license.
	 *
	 * @param license the new license
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * Gets the license url.
	 *
	 * @return the license url
	 */
	public String getLicenseUrl() {
		return licenseUrl;
	}

	/**
	 * Sets the license url.
	 *
	 * @param licenseUrl the new license url
	 */
	public void setLicenseUrl(String licenseUrl) {
		this.licenseUrl = licenseUrl;
	}

	/**
	 * Gets the contact.
	 *
	 * @return the contact
	 */
	public SwaggerContact getContact() {
		return contact;
	}

	/**
	 * Sets the contact.
	 *
	 * @param contact the new contact
	 */
	public void setContact(SwaggerContact contact) {
		this.contact = contact;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "SwaggerApiInfo [version=" + version + ", title=" + title + ", description=" + description + ", termsOfServiceUrl=" + termsOfServiceUrl
				+ ", license=" + license + ", licenseUrl=" + licenseUrl + ", contact=" + contact + "]";
	}
}
