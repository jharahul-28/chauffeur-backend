package com.velvet.rest.services.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.velvet.rest.common.constants.ApplicationProfiles;
import com.velvet.rest.common.constants.CommonConstants;
import com.velvet.rest.common.dbsupport.crud.BaseCrudRepositoryImpl;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by mohit on 01.01.2026
 */

@SpringBootApplication
@ComponentScan(basePackages = { CommonConstants.BASE_PACKAGE })
@EnableJpaRepositories(basePackages = CommonConstants.BASE_PACKAGE, repositoryBaseClass = BaseCrudRepositoryImpl.class)
@EntityScan(CommonConstants.BASE_PACKAGE)
@EnableSwagger2
@EnableFeignClients(basePackages = { CommonConstants.BASE_PACKAGE })
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class VelvetNucleusApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(VelvetNucleusApplication.class);
		application.setWebApplicationType(WebApplicationType.SERVLET);
		if (System.getProperty(CommonConstants.SPRING_PROFILE_ACTIVE_KEY) == null) {
			System.setProperty(CommonConstants.SPRING_PROFILE_ACTIVE_KEY, ApplicationProfiles.LOCAL);
		}
//		System.setProperty(CommonConstants.SPRING_PROFILE_ACTIVE_KEY, ApplicationProfiles.PRODUCTION);
		application.run(args);
	}

}
