package com.velvet.rest.services.util.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.util.beans.AppConfigResponse;
import com.velvet.rest.services.util.entity.AppConfigEntity;
import com.velvet.rest.services.util.repository.AppConfigRepository;

@Service
public class AppConfigService implements BaseService<AppConfigEntity> {

	private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

	@Autowired
	private AppConfigRepository appConfigRepository;

	@Transactional(readOnly = true)
	public AppConfigResponse getConfig(String platform, String version) {
		AppConfigEntity entity = appConfigRepository.findByPlatformAndActive(platform.toLowerCase(), 1);

		AppConfigResponse response = new AppConfigResponse();
		response.setPlatform(platform);
		response.setVersion(version);

		if (entity == null) {
			logger.warn("No config found for platform: {}", platform);
			return response;
		}

		response.setLatestVersion(entity.getLatestVersion());
		response.setMinSupportedVersion(entity.getMinSupportedVersion());
		response.setLabels(entity.getLabels());
		response.setPhones(entity.getPhones());
		response.setDynamicFields(entity.getDynamicFields());

		response.setForceUpdate(compareVersions(version, entity.getMinSupportedVersion()) < 0);
		response.setSoftUpdate(!response.isForceUpdate()
				&& entity.getSoftUpdateVersion() != null
				&& compareVersions(version, entity.getSoftUpdateVersion()) < 0);

		return response;
	}

	/**
	 * Compares two semantic version strings (e.g. "2.3.1" vs "2.4.0").
	 * Returns negative if v1 < v2, zero if equal, positive if v1 > v2.
	 */
	private int compareVersions(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return 0;
		}
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");
		int length = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < length; i++) {
			int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
			int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
			if (p1 != p2) {
				return p1 - p2;
			}
		}
		return 0;
	}

	@Override
	public Class<AppConfigEntity> getEntity() {
		return AppConfigEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return appConfigRepository;
	}
}
