package com.velvet.rest.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

/**
 * The Class SecurityUtils.
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class SecurityUtils {

	/**
	 * Instantiates a new security utils.
	 */
	private SecurityUtils() {}

	/** The Constant _128_BIT_KEY. */
	public static final String _128_BIT_KEY = "Lv5TsepPsiL2keWQ";

	/** The Constant _INIT_VECTOR. */
	public static final String _INIT_VECTOR = "mWzZRiYrc9GQllHb";

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(SecurityUtils.class);

	/**
	 * Decrypt object.
	 *
	 * @param value the value
	 * @return the object
	 */
	public static Object decryptObject(Object value) {
		try {
			String encryptedValue = (String) value;
			byte[] yourBytes = SecurityUtils.decryptForBytes(SecurityUtils._128_BIT_KEY, SecurityUtils._INIT_VECTOR, encryptedValue);
			ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
			ObjectInput in = null;
			try {
				in = new ObjectInputStream(bis);
				value = in.readObject();
			} finally {
				try {
					bis.close();
				} catch (IOException ex) {
					_LOGGER.error(ex.getMessage(), ex);
				}
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					_LOGGER.error(ex.getMessage(), ex);
				}
			}
		} catch (Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return value;
	}

	/**
	 * Decrypt.
	 *
	 * @param key        the key
	 * @param initVector the init vector
	 * @param encrypted  the encrypted
	 * @return the string
	 */
	public static String decrypt(String key, String initVector, String encrypted) {
		try {
			byte[] original = decryptForBytes(key, initVector, encrypted);

			return new String(original);
		} catch (Exception ex) {
			_LOGGER.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * Decrypt for bytes.
	 *
	 * @param key        the key
	 * @param initVector the init vector
	 * @param encrypted  the encrypted
	 * @return the byte[]
	 */
	public static byte[] decryptForBytes(String key, String initVector, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			return cipher.doFinal(Base64.decodeBase64(encrypted));
		} catch (Exception ex) {
			_LOGGER.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * Encrypt.
	 *
	 * @param key        the key
	 * @param initVector the init vector
	 * @param value      the value
	 * @return the string
	 */
	public static String encrypt(String key, String initVector, byte[] value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value);
			String str = Base64.encodeBase64String(encrypted);
			if (!StringUtils.isEmpty(str)) {
				str = str.replace("\r\n", "");
			}
			return str;
		} catch (Exception ex) {
			_LOGGER.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * <p>
	 * encrypt.
	 * </p>
	 *
	 * @param value a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String encrypt(String value) {
		return encrypt(_128_BIT_KEY, _INIT_VECTOR, value);
	}

	/**
	 * <p>
	 * decrypt.
	 * </p>
	 *
	 * @param encrypted a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String decrypt(String encrypted) {
		return decrypt(_128_BIT_KEY, _INIT_VECTOR, encrypted);
	}

	/**
	 * Encrypt.
	 *
	 * @param key        the key
	 * @param initVector the init vector
	 * @param value      the value
	 * @return the string
	 */
	public static String encrypt(String key, String initVector, String value) {
		try {
			return encrypt(key, initVector, value.getBytes());
		} catch (Exception ex) {
			_LOGGER.error(ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * Encrypt object.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String encryptObject(Object value) {
		String encValue = null;
		try {
			byte[] yourBytes = null;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(value);
				yourBytes = bos.toByteArray();

				encValue = encrypt(SecurityUtils._128_BIT_KEY, SecurityUtils._INIT_VECTOR, yourBytes);
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException ex) {}
				try {
					bos.close();
				} catch (IOException ex) {
					_LOGGER.error(ex.getMessage(), ex);
				}
			}
		} catch (Exception ex) {
			_LOGGER.error(ex.getMessage(), ex);
		}
		return encValue;
	}

	/**
	 * <p>
	 * toMd5.
	 * </p>
	 *
	 * @param data a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String toMd5(String data) {

		Assert.notNull(data, "Data can not be null");
		StringBuilder hexString = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data.getBytes());

			byte[] byteData = md.digest();

			for (int i = 0; i < byteData.length; i++) {
				String hex = Integer.toHexString(0xff & byteData[i]);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
		} catch (Exception e) {
			_LOGGER.error(e.getMessage(), e);
		}
		return hexString.toString();
	}

	/**
	 * Url decode.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (Exception e) {
			_LOGGER.warn("Decode Error : " + e.getMessage());
			return value;
		}

	}

	/**
	 * Url encode.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			_LOGGER.warn("Encode Error : " + e.getMessage());
			return value;
		}
	}
}
