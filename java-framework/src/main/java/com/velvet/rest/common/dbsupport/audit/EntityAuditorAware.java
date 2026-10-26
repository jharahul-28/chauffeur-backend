/*
 * @author Mohit
 */
package com.velvet.rest.common.dbsupport.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import com.velvet.rest.common.utils.SessionUtils;

/**
 * The Class EntityAuditorAware.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class EntityAuditorAware implements AuditorAware<Long> {

	/** {@inheritDoc} */
	@Override
	public Optional<Long> getCurrentAuditor() {
		return Optional.of(SessionUtils.getAuthUserId());
	}

}
