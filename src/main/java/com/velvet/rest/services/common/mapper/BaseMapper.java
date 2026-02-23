/**
 * 
 */
package com.velvet.rest.services.common.mapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.common.utils.CollectionUtils;

/**
 * @author mohit
 *
 */
@Component
public class BaseMapper<S, D> {

	@Autowired
	private ObjectMapper jacksonMapper;

	@SuppressWarnings("unchecked")
	public <S, D> D mapSrcToDest(S src, D dest) {
		try {
			Class<D> clazz = (Class<D>) dest.getClass();
			// Serialize the original object to JSON
			String json = jacksonMapper.writeValueAsString(src);
			// Deserialize back to a new object
			return jacksonMapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("Deep copy failed", e);
		}
	}

	@SuppressWarnings("hiding")
	public <D> D copyNullAwareMapProp(@SuppressWarnings("rawtypes") Map map, D dest) {
		final BeanWrapper trg = new BeanWrapperImpl(dest);
		String fieldName = null;
		for (final Field field : dest.getClass().getDeclaredFields()) {
			fieldName = field.getName();
			if (map.containsKey(fieldName)) {
				trg.setPropertyValue(fieldName, map.get(fieldName));
			}
		}
		return dest;
	}

	public ObjectMapper getJacksonMapper() {
		return this.jacksonMapper;
	}

	public <T> T mapJsonAsList(String json, TypeReference<T> typeRef) {
		T t = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			t = mapper.readValue(json, typeRef);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	public <T> List<T> mapAsList(List<?> data, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(data)) {
			JavaType listType = jacksonMapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
			// single convertValue call that produces List<T>
			return jacksonMapper.convertValue(data, listType);
		}
		return result;
	}

	public <T> T mapJsonAsObject(String json, Class<T> clazz) {
		T t = null;
		try {
			t = jacksonMapper.readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}
}
