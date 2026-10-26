package com.velvet.rest.common.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.EncryptionUtils;

@RestControllerAdvice
public class DecryptionRequestAdvice extends RequestBodyAdviceAdapter {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(DecryptionRequestAdvice.class);

	@Autowired
	private ApiEncryptionProperties encryptionProperties;

	@Value("${auth.header}")
	private String internalAuthKey;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		if (!encryptionProperties.isEnabled()) {
			return false;
		}
		return isApplicableUrl();
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		String rawBody = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
		try {
			EncryptedPayload encryptedPayload = objectMapper.readValue(rawBody, EncryptedPayload.class);
			if (encryptedPayload.getData() != null) {
				String decryptedJson = EncryptionUtils.decrypt(encryptedPayload.getData());
				_LOGGER.debug("Request body decrypted successfully");
				byte[] decryptedBytes = decryptedJson.getBytes(StandardCharsets.UTF_8);
				return new DecryptedHttpInputMessage(inputMessage.getHeaders(), decryptedBytes);
			}
		} catch (Exception e) {
			_LOGGER.warn("Request body is not encrypted, passing through as-is: " + e.getMessage());
		}
		return new DecryptedHttpInputMessage(inputMessage.getHeaders(), rawBody.getBytes(StandardCharsets.UTF_8));
	}

	private boolean isApplicableUrl() {
		try {
			ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attrs != null) {
				HttpServletRequest request = attrs.getRequest();
				if (isInternalCall(request)) {
					return false;
				}
				String uri = request.getRequestURI();
				for (String pattern : encryptionProperties.getApplicableUrls()) {
					if (pathMatcher.match(pattern, uri)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			_LOGGER.warn("Failed to check bypass URL: " + e.getMessage());
		}
		return false;
	}

	private boolean isInternalCall(HttpServletRequest request) {
		String authKey = request.getHeader(CommonConstants.AUTH_HEADER);
		return internalAuthKey != null && internalAuthKey.equals(authKey);
	}

	private static class DecryptedHttpInputMessage implements HttpInputMessage {

		private final HttpHeaders headers;

		private final byte[] body;

		public DecryptedHttpInputMessage(HttpHeaders headers, byte[] body) {
			this.headers = headers;
			this.body = body;
		}

		@Override
		public InputStream getBody() {
			return new ByteArrayInputStream(body);
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}
	}
}
