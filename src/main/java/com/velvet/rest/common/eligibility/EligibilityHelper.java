package com.velvet.rest.common.eligibility;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.velvet.rest.common.beans.Endpoint;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

@Component
class EligibilityHelper {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(EligibilityHelper.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public enum HandlerModeKeys {
		ONLY_SESSION, NO_SESSION, MIXED_SESSION, GROUP_PRIVILEGE
	}

	private static final String Q_NO_SESSION_HANDLERS = "SELECT method, uri, privilege_slug FROM privilege_endpoints where uri_mode = 'NO_SESSION'";

	private static final String Q_APP_SESSION = "SELECT count(id) FROM app_sessions where user_id=? and session_key=? and session_expiry > now()";

	private static final String Q_USER_GROUPS_HANDLERS = "SELECT e.method, e.uri, e.privilege_slug FROM privilege_endpoints e left join groups_privileges g  on g.privilege_slug=e.privilege_slug left join users_groups u on u.group_id=g.group_id where e.uri_mode = 'ONLY_SESSION' and u.user_id=? and e.method=? and e.uri = ?";

	public List<Endpoint> getNoSessionEndpoints() {
		return getEndpoints(Q_NO_SESSION_HANDLERS);
	}

	private List<Endpoint> getEndpoints(String query) {
		List<Endpoint> endpoints = new ArrayList<>();
		try {
			endpoints = jdbcTemplate.query(query, (rs, rowNum) -> new Endpoint(rs.getString("method"), rs.getString("uri"), rs.getString("privilege_slug")));
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return endpoints;
	}

	public List<Endpoint> getUserGroupEndpoints(Long userId, String[] handlerPaths, String httpMethod) {
		List<Endpoint> endpoints = new ArrayList<>();
		try {
			endpoints = jdbcTemplate.query(Q_USER_GROUPS_HANDLERS, new Object[] { userId, httpMethod, handlerPaths[0] },
					(rs, rowNum) -> new Endpoint(rs.getString("method"), rs.getString("uri"), rs.getString("privilege_slug")));
		} catch (final Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return endpoints;
	}

	public int validateAuthKey(String authHeader, Long userId) {
		int result = jdbcTemplate.queryForObject(Q_APP_SESSION, new Object[] { userId, authHeader }, Integer.class);
		return result;
	}

}
