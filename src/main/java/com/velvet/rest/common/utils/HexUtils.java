package com.velvet.rest.common.utils;


public class HexUtils {

	/**
	 * Instantiates a new hex utils.
	 */
	private HexUtils() {}

	/**
	 * Convert hex string to byte array.
	 *
	 * @param hex the hex string
	 * @return the byte array
	 */
	public static byte[] hexToBytes(String hex) {
		if (hex == null || (hex.length() % 2) != 0) {
			throw new IllegalArgumentException("Invalid hex");
		}
		int len = hex.length();
		byte[] out = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			out[i / 2] = (byte) ((toNibble(hex.charAt(i)) << 4) | toNibble(hex.charAt(i + 1)));
		}
		return out;
	}

	/**
	 * Convert hex character to nibble value (0-15).
	 *
	 * @param c the hex character (0-9, A-F, a-f)
	 * @return the nibble value (0-15)
	 */
	public static int toNibble(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		if (c >= 'A' && c <= 'F')
			return 10 + (c - 'A');
		if (c >= 'a' && c <= 'f')
			return 10 + (c - 'a');
		throw new IllegalArgumentException("Invalid hex char: " + c);
	}
}

