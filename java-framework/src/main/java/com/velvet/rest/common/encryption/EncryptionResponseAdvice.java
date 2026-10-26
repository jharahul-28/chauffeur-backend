package com.velvet.rest.common.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.beans.ErrorResponse;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.EncryptionUtils;

@RestControllerAdvice
public class EncryptionResponseAdvice implements ResponseBodyAdvice<Object> {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(EncryptionResponseAdvice.class);

	@Autowired
	private ApiEncryptionProperties encryptionProperties;

	@Value("${auth.header}")
	private String internalAuthKey;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return encryptionProperties.isEnabled();
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (body == null) {
			return null;
		}
		if (body instanceof ErrorResponse) {
			return body;
		}
		if (!isApplicableUrl(request)) {
			return body;
		}
		try {
			String json = objectMapper.writeValueAsString(body);
			String encrypted = EncryptionUtils.encrypt(json);
			_LOGGER.debug("Response body encrypted successfully");
			EncryptedPayload encryptedPayload = new EncryptedPayload();
			encryptedPayload.setData(encrypted);
			return encryptedPayload;
		} catch (Exception e) {
			_LOGGER.error("Failed to encrypt response body: " + e.getMessage(), e);
			return body;
		}
	}

	private boolean isApplicableUrl(ServerHttpRequest request) {
		try {
			if (isInternalCall(request)) {
				return false;
			}
			String uri = request.getURI().getPath();
			for (String pattern : encryptionProperties.getApplicableUrls()) {
				if (pathMatcher.match(pattern, uri)) {
					return true;
				}
			}
		} catch (Exception e) {
			_LOGGER.warn("Failed to check bypass URL: " + e.getMessage());
		}
		return false;
	}

	private boolean isInternalCall(ServerHttpRequest request) {
		String authKey = request.getHeaders().getFirst(CommonConstants.AUTH_HEADER);
		return internalAuthKey != null && internalAuthKey.equals(authKey);
	}

}
