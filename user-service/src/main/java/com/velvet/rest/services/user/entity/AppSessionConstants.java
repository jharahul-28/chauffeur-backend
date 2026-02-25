package com.velvet.rest.services.user.entity;

/**
 * Column field mapping as constants for table AuthAppSessions
 *
 * @author Mohit
 * @version $Id: $Id
 */
public interface AppSessionConstants {

	String TABLE_NAME = "app_sessions";

	String ID = "id";

	String USER_ID = "user_id";

	String KEY = "session_key";

	String EXPIRY = "session_expiry";

	String LAST_ACCESSED = "last_accessed";

	interface Query {
		String UPDATE_LAST_ACCESS_TIME = "update AppSessionEntity e set e.lastAccessed = ?1 where e.key = ?2";
	}
}
