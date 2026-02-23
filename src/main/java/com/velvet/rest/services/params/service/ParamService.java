package com.velvet.rest.services.params.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.params.entity.ParamEntity;
import com.velvet.rest.services.params.repository.ParamRepository;

/**
 * Created by mohit on 24.6.22.
 */
@Service
public class ParamService implements BaseService<ParamEntity> {

	@Autowired
	private ParamRepository paramRepository;

	public ParamEntity findById(Long id) {
		ParamEntity result = findRecordById(id);
		return result;
	}

	public String getParam(String key, String defaultValue) {
		String value = findValueByParamKey(key);
		value = !StringUtils.isEmpty(value) ? value : defaultValue;
		return value;
	}

	public String findValueByParamKey(String paramKey) {
		ParamEntity result = paramRepository.findByParamKey(paramKey);
		if (result != null) {
			return result.getParamValue();
		}
		return null;
	}

	public ParamEntity findByParamKey(String paramKey) {
		ParamEntity result = paramRepository.findByParamKey(paramKey);
		return result;
	}

	public ParamEntity save(ParamEntity entity) {
		ParamEntity result = paramRepository.save(entity);
		return result;
	}

	public Iterable<ParamEntity> saveAll(List<ParamEntity> entities) {
		Iterable<ParamEntity> result = paramRepository.saveAll(entities);
		return result;
	}

	public void updateParamValue(String paramKey, String paramValue) {
		ParamEntity paramEntity = paramRepository.findByParamKey(paramKey);
		if (paramEntity == null) {
			paramEntity = ParamEntity.create(paramKey, paramValue, "1", "1");
		}
		paramEntity.setParamValue(paramValue);
		paramRepository.save(paramEntity);
	}

	public List<ParamEntity> findAll() {
		return findAllRecords();
	}

	@Override
	public Class<ParamEntity> getEntity() {
		return ParamEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return paramRepository;
	}

}