package com.velvet.rest.services.params.repository;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.services.params.entity.ParamEntity;

public interface ParamRepository extends BaseCrudRepository<ParamEntity, Long> {

	ParamEntity findByParamKey(String paramKey);
}
