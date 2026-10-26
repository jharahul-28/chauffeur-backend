package com.velvet.rest.common.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.StringUtils;

public class EncryptionUtils {

	public static void main(String[] d) {
//		String s = "{\"channel\":\"WEB\",\"deliveryDate\":\"2026-02-01\",\"orderType\":\"LUNCH_MEAL\",\"quantity\":1,\"skuCode\":\"2\"}";
//		String en = encrypt(s);
//		System.out.println(en);
		String en = "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";
		String de = decrypt(en);
		System.out.println(de);
	}

	private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";

	private static final int GCM_TAG_BITS = 128; // 16 bytes tag

	private static final int IV_LEN = 12; // GCM standard nonce size

	/** The Constant _128_BIT_KEY. */
	public static final String _128_BIT_KEY = "ajFnTE1UODI4cG92M3FMWVhJMzBSQ1Rxbkh3SFRvajE=";

	private static SecretKeySpec keySpec;

	private static void initializeKeySpec() {
		String raw = _128_BIT_KEY;
		byte[] key = decodeKey(raw);
		if (key.length != 32) {
			throw new IllegalArgumentException("ENCRYPTION_KEY must be 32 bytes");
		}
		keySpec = new SecretKeySpec(key, "AES");
	}

	public static String encrypt(String plain) {
		if (!StringUtils.hasText(plain))
			return null;
		try {
			byte[] iv = deterministicIv(plain);
			Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
			if (keySpec == null) {
				initializeKeySpec();
			}
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_BITS, iv));
			byte[] ctAndTag = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));

			ByteBuffer bb = ByteBuffer.allocate(iv.length + ctAndTag.length);
			bb.put(iv).put(ctAndTag);
			return Base64.getEncoder().encodeToString(bb.array());
		} catch (Exception e) {
			throw new RuntimeException("Encryption failed", e);
		}
	}

	public static String decrypt(String base64) {
		if (!StringUtils.hasText(base64))
			return null;
		try {
			byte[] all = Base64.getDecoder().decode(base64);
			if (all.length <= IV_LEN) {
				throw new IllegalArgumentException("Ciphertext too short");
			}
			byte[] iv = new byte[IV_LEN];
			byte[] ctAndTag = new byte[all.length - IV_LEN];
			System.arraycopy(all, 0, iv, 0, IV_LEN);
			System.arraycopy(all, IV_LEN, ctAndTag, 0, ctAndTag.length);

			Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
			if (keySpec == null) {
				initializeKeySpec();
			}
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_BITS, iv));
			byte[] pt = cipher.doFinal(ctAndTag);
			return new String(pt, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Decryption failed", e);
		}
	}

	private static byte[] deterministicIv(String plain) {
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] digest = sha256.digest(plain.getBytes(StandardCharsets.UTF_8));
			byte[] iv = new byte[IV_LEN];
			System.arraycopy(digest, 0, iv, 0, IV_LEN);
			return iv;
		} catch (Exception e) {
			throw new RuntimeException("IV derivation failed", e);
		}
	}

	private static byte[] decodeKey(String raw) {
		if (raw == null)
			throw new IllegalArgumentException("ENCRYPTION_KEY is null");
		String s = raw.trim();
		try {
			byte[] b = Base64.getDecoder().decode(s);
			if (b.length == 32)
				return b;
		} catch (IllegalArgumentException ignored) {
		}
		if (s.matches("^[0-9a-fA-F]{64}$")) {
			return HexUtils.hexToBytes(s);
		}
		throw new IllegalArgumentException("ENCRYPTION_KEY must be 32-byte Base64 or 64-char hex");
	}
}
