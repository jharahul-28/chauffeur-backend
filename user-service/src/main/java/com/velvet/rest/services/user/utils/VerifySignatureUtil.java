package com.velvet.rest.services.user.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * <p>
 * VerifySignatureUtil class.
 * </p>
 *
 * @author mohit
 * @version $Id: $Id
 */
public class VerifySignatureUtil {

	/**
	 * @param keyType            (keyType attribute from publicKey api endpoint
	 *                           response. Example RSA)
	 * @param publicKeyString    (key attribute from publicKey api endpoint
	 *                           response. publicKey can be cached. Try to verify
	 *                           the payload with current key and if its fail reload
	 *                           it from api and try once more.)
	 * @param payload            (payload attribute from the response)
	 * @param signedString       (signature attribute from the response)
	 * @param signatureAlgorithm (Signature-Algorithm from response header example
	 *                           SHA512withRSA)
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean verify(final String keyType, final String publicKeyString, final String payload, final String signedString,
			final String signatureAlgorithm) throws Exception {

		final byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
		final PublicKey publicKey = KeyFactory.getInstance(keyType).generatePublic(new X509EncodedKeySpec(publicKeyBytes));

		final byte[] signatureByteArray = Base64.getDecoder().decode(signedString.getBytes(StandardCharsets.UTF_8));
		final byte[] payloadArray = payload.getBytes(StandardCharsets.UTF_8);

		Signature vSignature = Signature.getInstance(signatureAlgorithm);
		vSignature.initVerify(publicKey);
		vSignature.update(payloadArray);

		return vSignature.verify(signatureByteArray);
	}
}