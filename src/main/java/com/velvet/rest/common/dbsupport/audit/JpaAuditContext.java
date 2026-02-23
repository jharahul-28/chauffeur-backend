package com.velvet.rest.common.dbsupport.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * JpaAuditContext class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class JpaAuditContext {

	private JpaAuditContext() {}

	private static ThreadLocal<Map<String, JpaAuditInfo>> auditInfo = new ThreadLocal<>();

	/**
	 * <p>
	 * getJpaAuditInfoMap.
	 * </p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, JpaAuditInfo> getJpaAuditInfoMap() {
		return auditInfo.get();
	}

	/**
	 * <p>
	 * getJpaAuditInfo.
	 * </p>
	 *
	 * @param operation   a {@link com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaOperation} object.
	 * @param entityClazz a {@link java.lang.Class} object.
	 * @return a {@link com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaAuditInfo} object.
	 */
	public static JpaAuditInfo getJpaAuditInfo(JpaOperation operation, Class<?> entityClazz) {
		if (auditInfo.get() == null) {
			auditInfo.set(new LinkedHashMap<>());
		}
		return auditInfo.get().get(operation.toString().concat(entityClazz.getName()));
	}

	/**
	 * <p>
	 * getModifiedEntityProperties.
	 * </p>
	 *
	 * @param operation   a {@link com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaOperation} object.
	 * @param entityClazz a {@link java.lang.Class} object.
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, Object> getModifiedEntityProperties(JpaOperation operation, Class<?> entityClazz) {
		if (auditInfo.get() == null) {
			auditInfo.set(new LinkedHashMap<>());
		}
		JpaAuditInfo audit = auditInfo.get().get(operation.toString().concat(entityClazz.getName()));
		if (audit == null) {
			return new HashMap<>();
		} else {
			return audit.getModifiedProperties();
		}
	}

	/**
	 * <p>
	 * addJpaAuditInfo.
	 * </p>
	 *
	 * @param jpaAuditInfo a {@link com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaAuditInfo} object.
	 */
	public static void addJpaAuditInfo(JpaAuditInfo jpaAuditInfo) {
		if (auditInfo.get() == null) {
			auditInfo.set(new LinkedHashMap<>());
		}
		auditInfo.get().put(jpaAuditInfo.getOperation().toString().concat(jpaAuditInfo.getEntityClazz().getName()), jpaAuditInfo);
	}

	public enum JpaOperation {
		SAVE, UPDATE, DELETE
	}

	public static class JpaAuditInfo {

		private Class<?> entityClazz;

		private Serializable id;

		private JpaOperation operation;

		private String sql;

		private Map<String, Object> modifiedProperties;

		public Class<?> getEntityClazz() {
			return entityClazz;
		}

		public void setEntityClazz(Class<?> entityClazz) {
			this.entityClazz = entityClazz;
		}

		public Serializable getId() {
			return id;
		}

		public void setId(Serializable id) {
			this.id = id;
		}

		public JpaOperation getOperation() {
			return operation;
		}

		public void setOperation(JpaOperation operation) {
			this.operation = operation;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public Map<String, Object> getModifiedProperties() {
			return modifiedProperties;
		}

		public void setModifiedProperties(Map<String, Object> modifiedProperties) {
			this.modifiedProperties = modifiedProperties;
		}
	}
}
