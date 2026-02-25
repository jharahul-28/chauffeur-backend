package com.velvet.rest.services.util.repository;

import org.springframework.stereotype.Repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.util.entity.AppConfigEntity;

@Repository
public interface AppConfigRepository extends BaseCrudRepository<AppConfigEntity, Long> {

	AppConfigEntity findByPlatformAndActive(String platform, Integer active);
}
