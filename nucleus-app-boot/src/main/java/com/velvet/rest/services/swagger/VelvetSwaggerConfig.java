/*
 * @author Mohit
 */
package com.velvet.rest.services.swagger;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.velvet.rest.common.constants.ApplicationProfiles;
import com.velvet.rest.common.utils.ContextUtils;
import com.velvet.rest.services.common.swagger.SwaggerApiInfo;
import com.velvet.rest.services.common.swagger.SwaggerConfig;

/**
 * The Class MoleculeSwaggerConfig.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@Component
@Profile(value = { ApplicationProfiles.LOCAL, ApplicationProfiles.DEVELOPMENT, ApplicationProfiles.TEST })
public class VelvetSwaggerConfig extends SwaggerConfig {

	@Override
	public SwaggerApiInfo getApiInfo() {
		return getSwaggerFileInfo(ContextUtils.getBootName());
	}
}
