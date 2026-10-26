package com.velvet.rest.common.eligibility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import com.velvet.rest.common.beans.Endpoint;
import com.velvet.rest.common.utils.CollectionUtils;
import com.velvet.rest.common.utils.ContextUtils;

public class EligibilityUtils {

	public static boolean isBypassSessionHandler(String httpMethod, Object handler) {
		return matchHandlerCall(httpMethod, handler, populateHandlers(getProvider().getNoSessionEndpoints()));
	}

	private static boolean isAllAccess(List<String> resolvedHandlers, String httpMethod) {
		return resolvedHandlers.contains(httpMethod.concat(":").concat("*"));
	}

	private static boolean matchHandlerCall(String httpMethod, Object handler, List<String> resolvedHandlers) {
		boolean allowAccess = false;
		String[] paths = null;
		if (handler instanceof String) {
			paths = new String[] { (String) handler };
		} else if (handler instanceof HandlerMethod) {
			final HandlerMethod method = (HandlerMethod) handler;
			final RequestMapping mapping = method.getMethodAnnotation(RequestMapping.class);
			paths = mapping != null ? mapping.path() : new String[] {};
		}
		if (paths != null) {
			for (final String path : paths) {
				if (resolvedHandlers.contains(httpMethod.concat(":").concat(path.toLowerCase()))) {
					// _LOGGER.info("matchHandlerCall : " + path);
					allowAccess = true;
					break;
				}
			}
		}
		return allowAccess;
	}

	private static String[] getHandlerPath(Object handler) {
		String[] paths = null;
		if (handler instanceof String) {
			paths = new String[] { (String) handler };
		} else if (handler instanceof HandlerMethod) {
			final HandlerMethod method = (HandlerMethod) handler;
			final RequestMapping mapping = method.getMethodAnnotation(RequestMapping.class);
			paths = mapping != null ? mapping.path() : new String[] {};
		}
		return paths;
	}

	private static List<String> populateHandlers(List<Endpoint> handlers) {
		final List<String> resolvedHandlers = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(handlers)) {
			for (final Endpoint ep : handlers) {
				final String method = ep.getMethod().toUpperCase();
				String uri = ep.getUri().toLowerCase();
				if (uri.indexOf("/*") == -1) {
					explodeHandlers(resolvedHandlers, method, uri);
				} else {
					uri = uri.replace("/*", "");
					if (uri.length() == 0) {
						uri = "*";
					}
					explodeHandlers(resolvedHandlers, method, uri);
				}
			}
		}
		return resolvedHandlers;
	}

	private static void explodeHandlers(List<String> resolvedHandlers, String m, String u) {
		if (m.equals("ANY")) {
			resolvedHandlers.add(HttpMethod.GET.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.HEAD.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.OPTIONS.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.PATCH.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.POST.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.PUT.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.TRACE.toString().concat(":").concat(u));
			resolvedHandlers.add(HttpMethod.DELETE.toString().concat(":").concat(u));
		} else {
			resolvedHandlers.add(m.concat(":").concat(u));
		}
	}

	private static EligibilityProvider getProvider() {
		return ContextUtils.getBean(EligibilityProvider.class);
	}

	public static boolean isEligibleHandler(String httpMethod, String calledUri, Object handler, Long userId) {

		final String[] handlerPaths = getHandlerPath(handler);
		if (handlerPaths == null) {
			return false;
		}
		final List<Endpoint> groupHandlers = getProvider().getGroupEndpoints(userId, handlerPaths, httpMethod);

		final List<String> resolvedHandlers = populateHandlers(groupHandlers);

		boolean allowAccess = false;

		if (isAllAccess(resolvedHandlers, httpMethod)) {
			allowAccess = true;
		} else if (matchHandlerCall(httpMethod, handler, resolvedHandlers)) {
			allowAccess = true;
		}
		return allowAccess;
	}

	public static boolean isAuthKeyValid(String authHeader, Long userId) {
		return getProvider().validateAuthKey(authHeader, userId);
	}

}
