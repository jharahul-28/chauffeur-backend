package com.velvet.rest.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AWSConfig {

	@Value("${client.aws.access-key}")
	private String awsAccessKey;

	@Value("${client.aws.secret-key}")
	private String awsSecretKey;

	@Bean
	S3Client s3() {
		String accessKey = awsAccessKey;
		String secretKey = awsSecretKey;
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder().region(Region.of("ap-south-1")).credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}

	@Bean
	SqsClient sqs() {
		String accessKey = awsAccessKey;
		String secretKey = awsSecretKey;
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return SqsClient.builder().region(Region.of("ap-south-1")).credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}

	@Bean
	S3Presigner s3Presigner() {
		String accessKey = awsAccessKey;
		String secretKey = awsSecretKey;
		AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		return S3Presigner.builder().region(Region.of("ap-south-1")).credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}
}
