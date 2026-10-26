package com.velvet.rest.services.common.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class AWSUploadService implements UploadService {

	private static final AppLogger _LOGGER = LoggingManager.getLogger(AWSUploadService.class);

	@Autowired
	private S3Client amazonS3;

	@Autowired
	private S3Presigner s3Presigner;

	@Override
	public Object uploadFile(String bucketName, String subdirectory, byte[] byteArray, String fileName) throws IOException {
		InputStream targetStream = new ByteArrayInputStream(byteArray);
		Object path = getUrl(subdirectory, fileName);
		try {
			// Save Image in S3
			amazonS3.putObject(PutObjectRequest.builder().bucket(bucketName).key(path.toString()).contentLength((long) byteArray.length).build(),
					RequestBody.fromInputStream(targetStream, byteArray.length));
		} catch (AwsServiceException e) {
			throw new IllegalStateException(String.format("Failed to upload the file : %s", path), e);
		}
		return path;
	}

	private Object getUrl(String subdirectory, String fileName) {
		return String.format("%s/%s", subdirectory, fileName);
	}

	/**
	 * Creates a presigned PUT URL based on the provided example for filename
	 * constraints
	 * 
	 * @param bucketName              S3 bucket name
	 * @param keyName                 Key for the object
	 * @param expiryDurationInMinutes Expiration time in minutes
	 * @return Presigned URL as String
	 */
	@Override
	public String presignedPutUrl(String bucketName, String keyName, long expiryDurationInMinutes) {
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(expiryDurationInMinutes))
					.putObjectRequest(putObjectRequest).build();

			PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
			return presignedRequest.url().toString();
		} catch (Exception e) {
			_LOGGER.error(String.format("Failed to create presigned PUT URL for bucket: %s, keyName: %s", bucketName, keyName), e);
			throw new RuntimeException("Failed to create presigned PUT URL", e);
		}
	}

}
