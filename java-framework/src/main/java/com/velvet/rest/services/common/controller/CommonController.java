package com.velvet.rest.services.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.beans.ErrorResponse;
import com.velvet.rest.common.exceptions.AuthenticationException;
import com.velvet.rest.common.exceptions.PrivilegeException;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.common.state.ApplicationState;
import com.velvet.rest.common.utils.ContextUtils;

/**
 * Created by mohit on 24.6.22.
 */
@RestController
public class CommonController {

	@Value("${spring.application.name}")
	private String appName;

	@GetMapping("/status")
	public ResponseEntity<String> getStatus() {
		return ResponseEntity.ok("app running: " + appName);
	}

	@GetMapping("/swag/apps")
	public ResponseEntity<String> getCatalog() {
		ApplicationState.setAppControlFlag(ApplicationState.getAppControlFlag() == 0 ? 2 : 0);
		return ResponseEntity.ok("app running: " + appName);
	}

	@GetMapping("/admin/app-control/status")
	public ResponseEntity<String> getAppControlStatus() {
		int flag = ApplicationState.getAppControlFlag();
		String mode = "N";
		if (flag == 1) {
			mode = "M";
		} else if (flag == 2) {
			mode = "RO";
		}
		return ResponseEntity.ok(String.format("App: %s | Mode: %s | Flag: %d | Requests: %d", appName, mode, flag, ApplicationState.getRequestCounter()));
	}

	@GetMapping("/admin/app-control/set/{flag}")
	public ResponseEntity<Void> setAppControlFlag(@PathVariable int flag) {
		ApplicationState.setAppControlFlag(flag);
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/error", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.TEXT_HTML_VALUE })
	public ErrorResponse error(HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> attributes = getErrorAttributes(request, true);
		if (String.valueOf(attributes.get("trace")).contains(AuthenticationException.class.getName())) {
			throw new AuthenticationException(ErrorBean.withError(Errors.AUTHORIZATION_EXCEPTION, String.valueOf(attributes.get("message")), null));
		} else if (String.valueOf(attributes.get("trace")).contains(PrivilegeException.class.getName())) {
			throw new PrivilegeException(ErrorBean.withError(Errors.PRIVILEGE_EXCEPTION, String.valueOf(attributes.get("message")), null));
		}
		return ErrorResponse.create().message(attributes.get("status") + ". " + attributes.get("message"))
				.addError(ErrorBean.withError(String.valueOf(attributes.get("error")), String.valueOf(attributes.get("message")),
						!ContextUtils.isProfileProd() ? (" *** ERROR TRACE (NON PROD) ***   " + attributes.get("trace")) : ""));
	}

	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		ErrorAttributes errorAttributes = new DefaultErrorAttributes();
		WebRequest requestAttributes = new ServletWebRequest(request);
		return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

}
