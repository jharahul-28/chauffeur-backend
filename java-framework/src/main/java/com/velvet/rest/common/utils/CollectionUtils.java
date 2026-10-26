/**
 * 
 */
package com.velvet.rest.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Mohit
 *
 */
public class CollectionUtils {

	private CollectionUtils() {
	}

	public static int BATCH_SIZE = 500;
	
	/**
	 * Return {@code true} if the supplied Collection is {@code null} or empty.
	 * Otherwise, return {@code false}.
	 */
	public static Boolean isEmpty(Collection<?> collection) {
		return org.springframework.util.CollectionUtils.isEmpty(collection);
	}

	/**
	 * Return {@code true} if the supplied Map is {@code null} or empty. Otherwise,
	 * return {@code false}.
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return org.springframework.util.CollectionUtils.isEmpty(map);
	}

	/**
	 * <p>
	 * isNotEmpty.
	 * </p>
	 */
	public static Boolean isNotEmpty(Map<?, ?> map) {
		return !org.springframework.util.CollectionUtils.isEmpty(map);
	}

	/**
	 * <p>
	 * isNotEmpty.
	 * </p>
	 */
	public static Boolean isNotEmpty(Collection<?> collection) {
		return !org.springframework.util.CollectionUtils.isEmpty(collection);
	}

	public static <T> Stream<List<T>> batches(List<T> source, int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("length = " + length);
		}
		final int size = source.size();
		if (size <= 0) {
			return Stream.empty();
		}
		final int fullChunks = (size - 1) / length;
		return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
	}
}
