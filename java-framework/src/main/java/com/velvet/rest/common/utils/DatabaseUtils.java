/**
 * 
 */
package com.velvet.rest.common.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import javax.persistence.Id;;

/**
 * @author Mohit
 *
 */
public class DatabaseUtils {

	/**
	 * Checks for active.
	 *
	 */
	public static boolean hasActive(Class<?> clazz) {
		return Arrays.asList(clazz.getSuperclass().getDeclaredFields()).stream().anyMatch(f -> f.getName().equalsIgnoreCase("active"));
	}

	public static String getDataType(Class<?> clazz, String fieldName) {
		Class<?> current = clazz;
		do {
			try {
				return current.getDeclaredField(fieldName).getType().getSimpleName();
			} catch (final Exception e) {
			}
		} while ((current = current.getSuperclass()) != null);
		return null;
	}

	public static <T> Object getTypedValue(Class<T> type, String fieldName, String value) {
		Object valueObj = null;
		try {
			String dataType = getDataType(type, fieldName);
			if (value != null && dataType != null) {
				switch (dataType) {
				case "Integer":
					valueObj = Integer.valueOf(value);
					break;
				case "Long":
					valueObj = Long.valueOf(value);
					break;
				case "String":
					valueObj = value;
					break;
				case "Date":
					String format = "yyyy-MM-dd";
					valueObj = DateUtils.getDate(format, value);
					break;
				default:
					break;
				}
			}
		} catch (final Exception e) {

		}
		return valueObj;
	}

	public static String entityId(Class<?> clazz) {
		Optional<Field> field = Arrays.asList(clazz.getDeclaredFields()).stream().filter(f -> hasAnnotation(Id.class, f)).findFirst();
		if (field.isPresent()) {
			return field.get().getName();
		} else {
			return "id";
		}
	}

	public static <A> boolean hasAnnotation(Class<A> annotationClass, Field field) {
		return Arrays.asList(field.getDeclaredAnnotations()).stream().anyMatch(a -> a.annotationType().getName().equals(annotationClass.getName()));
	}

}
