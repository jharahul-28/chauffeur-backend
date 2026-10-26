package com.velvet.rest.services.util.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.util.beans.AppConfigResponse;
import com.velvet.rest.services.util.services.AppConfigService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "App Config", description = "App configuration and version management.")
public class AppConfigController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(AppConfigController.class);

	@Autowired
	private BaseMapper<?, ?> configMapper;

	@Autowired
	private AppConfigService appConfigService;

	@ApiOperation(value = "Get app config by platform and version", nickname = "getAppConfig")
	@GetMapping("/utils/config")
	public ResponseEntity<AppConfigResponse> getConfig(
			@ApiParam(value = "Platform (android, ios, web)", required = true) @RequestParam String platform,
			@ApiParam(value = "App version (e.g. 2.3.1)", required = true) @RequestParam String version) {

		logger.info("getConfig :: platform={}, version={}", platform, version);
		AppConfigResponse response = appConfigService.getConfig(platform, version);
		return ResponseEntity.ok(response);
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return configMapper;
	}
}
