package com.velvet.rest.services.user.entity;

/**
 * The Interface GroupPrivilegeConstants.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public interface GroupPrivilegeConstants {

	String TABLE_NAME = "groups_privileges";

	String ID = "id";

	String GROUP_ID = "group_id";

	String PRIVILEGE_SLUG = "privilege_slug";

	/**
	 * The Interface Query.
	 */
	interface Query {

		String DELETE_BY_GROUP = "delete from GroupPrivilegeEntity e where e.group = ?1";
	}
}
