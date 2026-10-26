package com.velvet.rest.services.util.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velvet.rest.common.dbsupport.crud.BaseCrudRepository;
import com.velvet.rest.common.websupport.base.BaseService;
import com.velvet.rest.services.util.entity.CityEntity;
import com.velvet.rest.services.util.repository.CityRepository;

@Service
public class CityService implements BaseService<CityEntity> {

	@Autowired
	private CityRepository cityRepository;

	@Transactional(readOnly = true)
	public List<CityEntity> searchCities(String query) {
		return cityRepository.findByNameStartingWithIgnoreCase(query);
	}
	
	@Transactional(readOnly = true)
	public CityEntity findByName(String name) {
		return cityRepository.findByNameIgnoreCase(name);
	}

	@Override
	public Class<CityEntity> getEntity() {
		return CityEntity.class;
	}

	@Override
	public BaseCrudRepository getRepository() {
		return cityRepository;
	}
}