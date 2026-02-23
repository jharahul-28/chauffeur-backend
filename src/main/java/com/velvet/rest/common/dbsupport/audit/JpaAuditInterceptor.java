package com.velvet.rest.common.dbsupport.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaAuditInfo;
import com.velvet.rest.common.dbsupport.audit.JpaAuditContext.JpaOperation;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.utils.JSONUtils;

/**
 * <p>
 * JpaAuditInterceptor class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class JpaAuditInterceptor extends EmptyInterceptor {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(JpaAuditInterceptor.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 3635500100832396613L;

	/** {@inheritDoc} */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		// _LOGGER.info("JPA Audit >> On Delete");
		final JpaAuditInfo auditInfo = new JpaAuditContext.JpaAuditInfo();
		auditInfo.setId(id);
		if (entity != null) {
			auditInfo.setEntityClazz(entity.getClass());
		}
		auditInfo.setOperation(JpaOperation.DELETE);
		auditInfo.setModifiedProperties(new HashMap<>());
		JpaAuditContext.addJpaAuditInfo(auditInfo);

		super.onDelete(entity, id, state, propertyNames, types);
	}

	/** {@inheritDoc} */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		// _LOGGER.info("JPA Audit >> On Save");
		try {
			final JpaAuditInfo auditInfo = new JpaAuditContext.JpaAuditInfo();
			auditInfo.setId(id);
			if (entity != null) {
				auditInfo.setEntityClazz(entity.getClass());
			}
			auditInfo.setOperation(JpaOperation.SAVE);
			final Map<String, Object> modifiedProps = new HashMap<>();
			for (int i = 0; i < state.length; i++) {
				final Object value = getValue(state, i);
				modifiedProps.put(String.valueOf(getValue(propertyNames, i)), value);
			}
			auditInfo.setModifiedProperties(modifiedProps);
			_LOGGER.info("modified props are :" + JSONUtils.objectToJson(modifiedProps));

			JpaAuditContext.addJpaAuditInfo(auditInfo);
		} catch (final Exception e) {
			_LOGGER.warn("JPA Audit  >> On Save Error : " + e.getMessage());
		}
		return super.onSave(entity, id, state, propertyNames, types);
	}

	/** {@inheritDoc} */
	@Override
	public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
		// _LOGGER.info("JPA Audit >> Find Dirty");
		try {
			final JpaAuditInfo auditInfo = new JpaAuditContext.JpaAuditInfo();
			auditInfo.setId(id);
			if (entity != null) {
				auditInfo.setEntityClazz(entity.getClass());
			}
			auditInfo.setOperation(JpaOperation.UPDATE);
			final Map<String, Object> modifiedProps = new HashMap<>();
			for (int i = 0; i < currentState.length; i++) {
				final Object currentValue = getValue(currentState, i);
				final Object previousValue = getValue(previousState, i);
				if (currentValue != null && !currentValue.equals(previousValue) || currentValue == null && previousValue != null) {
					modifiedProps.put(String.valueOf(getValue(propertyNames, i)), currentValue);
				}
			}
			auditInfo.setModifiedProperties(modifiedProps);
			JpaAuditContext.addJpaAuditInfo(auditInfo);
		} catch (final Exception e) {
			_LOGGER.warn("JPA Audit  >> Find Dirty Error : " + e.getMessage());
		}
		return super.findDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	private Object getValue(Object[] arr, int index) {
		try {
			return arr[index];
		} catch (final Exception e) {
			return null;
		}
	}
}
