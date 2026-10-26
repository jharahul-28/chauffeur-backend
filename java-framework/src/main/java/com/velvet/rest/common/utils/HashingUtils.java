package com.velvet.rest.common.utils;

import java.security.SecureRandom;
import java.util.Random;

public class HashingUtils {

	private static final Random RANDOM = new SecureRandom();

	/** Length of hash. @see #generateHash() */
	public static final int HASH_LENGTH = 8;

	/**
	 * Generate a random String suitable for use as hash.
	 *
	 * @return String suitable for use as hash
	 */
	public static String generateHash() {

		String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

		String pw = "";
		for (int i = 0; i < HASH_LENGTH; i++) {
			int index = (int) (RANDOM.nextDouble() * letters.length());
			pw += letters.substring(index, index + 1);
		}
		return pw;
	}
}