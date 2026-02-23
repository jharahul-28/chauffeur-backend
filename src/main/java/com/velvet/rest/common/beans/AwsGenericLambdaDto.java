package com.velvet.rest.common.beans;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DTO for AWS Generic Lambda requests - Enhanced with Builder Pattern Provides type-safe, fluent API for building HTTP requests
 */
public class AwsGenericLambdaDto {

	/**
	 * Supported HTTP Methods
	 */
	public enum HttpMethod {
		GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE
	}

	private final String url;

	private final HttpMethod method;

	private final Map<String, String> headers;

	private final Object data;

	private final Map<String, String> params;

	// Private constructor - only accessible via builder
	private AwsGenericLambdaDto(Builder builder) {
		this.url = builder.url;
		this.method = builder.method;
		this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
		this.data = builder.data;
		this.params = Collections.unmodifiableMap(new HashMap<>(builder.params));
	}

	/**
	 * Creates a new builder instance
	 *
	 * @return new Builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Creates a new builder with a specific URL
	 *
	 * @param url the target URL
	 * @return new Builder instance with URL set
	 */
	public static Builder builder(String url) {
		return new Builder().url(url);
	}

	/**
	 * Builder class for constructing AwsGenericLambdaDto instances
	 */
	public static class Builder {

		private String url;

		private HttpMethod method = HttpMethod.GET; // Default to GET

		private Map<String, String> headers = new HashMap<>();

		private Object data;

		private Map<String, String> params = new HashMap<>();

		private Builder() {
			// Add default headers
			headers.put("Content-Type", "application/json");
			headers.put("Accept", "application/json");
		}

		/**
		 * Sets the target URL
		 *
		 * @param url the target URL
		 * @return this builder instance
		 */
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		/**
		 * Sets the HTTP method
		 *
		 * @param method the HTTP method
		 * @return this builder instance
		 */
		public Builder method(HttpMethod method) {
			this.method = method;
			return this;
		}

		/**
		 * Convenience method to set GET method
		 *
		 * @return this builder instance
		 */
		public Builder get() {
			this.method = HttpMethod.GET;
			return this;
		}

		/**
		 * Convenience method to set POST method
		 *
		 * @return this builder instance
		 */
		public Builder post() {
			this.method = HttpMethod.POST;
			return this;
		}

		/**
		 * Convenience method to set PUT method
		 *
		 * @return this builder instance
		 */
		public Builder put() {
			this.method = HttpMethod.PUT;
			return this;
		}

		/**
		 * Convenience method to set DELETE method
		 *
		 * @return this builder instance
		 */
		public Builder delete() {
			this.method = HttpMethod.DELETE;
			return this;
		}

		/**
		 * Convenience method to set PATCH method
		 *
		 * @return this builder instance
		 */
		public Builder patch() {
			this.method = HttpMethod.PATCH;
			return this;
		}

		/**
		 * Sets the request body data
		 *
		 * @param data the request body data
		 * @return this builder instance
		 */
		public Builder data(Object data) {
			this.data = data;
			return this;
		}

		/**
		 * Adds a single header
		 *
		 * @param name  header name
		 * @param value header value
		 * @return this builder instance
		 */
		public Builder header(String name, String value) {
			if (StringUtils.hasText(name) && value != null) {
				this.headers.put(name, value);
			}
			return this;
		}

		/**
		 * Adds multiple headers
		 *
		 * @param headers map of headers to add
		 * @return this builder instance
		 */
		public Builder headers(Map<String, String> headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		/**
		 * Clears all headers and sets new ones
		 *
		 * @param headers map of headers to set
		 * @return this builder instance
		 */
		public Builder setHeaders(Map<String, String> headers) {
			this.headers.clear();
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		/**
		 * Adds a single query parameter
		 *
		 * @param name  parameter name
		 * @param value parameter value
		 * @return this builder instance
		 */
		public Builder param(String name, String value) {
			if (StringUtils.hasText(name) && value != null) {
				this.params.put(name, value);
			}
			return this;
		}

		/**
		 * Adds multiple query parameters
		 *
		 * @param params map of parameters to add
		 * @return this builder instance
		 */
		public Builder params(Map<String, String> params) {
			if (params != null) {
				this.params.putAll(params);
			}
			return this;
		}

		/**
		 * Clears all parameters and sets new ones
		 *
		 * @param params map of parameters to set
		 * @return this builder instance
		 */
		public Builder setParams(Map<String, String> params) {
			this.params.clear();
			if (params != null) {
				this.params.putAll(params);
			}
			return this;
		}

		/**
		 * Sets Content-Type header
		 *
		 * @param contentType the content type
		 * @return this builder instance
		 */
		public Builder contentType(String contentType) {
			return header("Content-Type", contentType);
		}

		/**
		 * Sets Accept header
		 *
		 * @param accept the accept type
		 * @return this builder instance
		 */
		public Builder accept(String accept) {
			return header("Accept", accept);
		}

		/**
		 * Sets Authorization header
		 *
		 * @param authorization the authorization value
		 * @return this builder instance
		 */
		public Builder authorization(String authorization) {
			return header("Authorization", authorization);
		}

		/**
		 * Sets Bearer token authorization
		 *
		 * @param token the bearer token
		 * @return this builder instance
		 */
		public Builder bearerToken(String token) {
			return authorization("Bearer " + token);
		}

		/**
		 * Validates the builder state and builds the DTO
		 *
		 * @return new AwsGenericLambdaDto instance
		 * @throws IllegalArgumentException if validation fails
		 */
		public AwsGenericLambdaDto build() {
			validate();
			return new AwsGenericLambdaDto(this);
		}

		/**
		 * Validates the builder state
		 *
		 * @throws IllegalArgumentException if validation fails
		 */
		private void validate() {
			if (!StringUtils.hasText(url)) {
				throw new IllegalArgumentException("URL cannot be null or empty");
			}

			if (method == null) {
				throw new IllegalArgumentException("HTTP method cannot be null");
			}

			// Validate that methods with body have data and vice versa
			if (isMethodWithBody(method) && data == null) {
				throw new IllegalArgumentException("HTTP method " + method + " requires a request body, but data is null");
			}

			if (!isMethodWithBody(method) && data != null) {
				throw new IllegalArgumentException("HTTP method " + method + " should not have a request body, but data is provided");
			}
		}

		/**
		 * Checks if the HTTP method typically requires a body
		 *
		 * @param method the HTTP method
		 * @return true if method typically has a body
		 */
		private boolean isMethodWithBody(HttpMethod method) {
			return method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH;
		}
	}

	/**
	 * Returns the HTTP method as a string for backward compatibility
	 *
	 * @return HTTP method as string
	 */
	public String getMethodAsString() {
		return method != null ? method.name() : null;
	}

	public String getUrl() {
		return url;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Object getData() {
		return data;
	}

	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * Builder for creating common request configurations
	 */
	public static class RequestTemplates {

		/**
		 * Creates a builder for JSON API requests
		 *
		 * @param url the target URL
		 * @return builder configured for JSON
		 */
		public static Builder jsonApi(String url) {
			return builder(url).contentType("application/json").accept("application/json");
		}

		/**
		 * Creates a builder for form data requests
		 *
		 * @param url the target URL
		 * @return builder configured for form data
		 */
		public static Builder formData(String url) {
			return builder(url).contentType("application/x-www-form-urlencoded").accept("application/json");
		}

		/**
		 * Creates a builder for file upload requests
		 *
		 * @param url the target URL
		 * @return builder configured for file upload
		 */
		public static Builder fileUpload(String url) {
			return builder(url).contentType("multipart/form-data").accept("application/json");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AwsGenericLambdaDto that = (AwsGenericLambdaDto) o;
		return Objects.equals(url, that.url) && method == that.method && Objects.equals(headers, that.headers) && Objects.equals(data, that.data)
				&& Objects.equals(params, that.params);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url, method, headers, data, params);
	}

	@Override
	public String toString() {
		return "AwsGenericLambdaDto{" + "url='" + url + '\'' + ", method=" + method + ", headers=" + headers + ", data=" + data + ", params=" + params + '}';
	}
}