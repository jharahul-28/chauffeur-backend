package com.velvet.rest.services.common.upload;

import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

public class UploadServiceUtil {

	private static final String IMAGE_PNG = "image/png";
	private static final String IMAGE_BMP = "image/bmp";
	private static final String IMAGE_GIF = "image/gif";
	private static final String IMAGE_JPEG = "image/jpeg";

	public static boolean isValidImgOrPdfFile(MultipartFile file) throws JsonProcessingException {

		// check if the file is empty
		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}
		// Check if the file is an image or pdf
		if (!Arrays.asList(IMAGE_PNG, IMAGE_BMP, IMAGE_GIF, IMAGE_JPEG, "application/pdf")
				.contains(file.getContentType())) {
			throw new IllegalStateException("File uploaded is not an image or PDF");
		}
		return true;
	}

	public static boolean isValidImgFile(MultipartFile file) throws JsonProcessingException {

		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}
		if (!Arrays.asList(IMAGE_PNG, IMAGE_BMP, IMAGE_GIF, IMAGE_JPEG)
				.contains(file.getContentType())) {
			throw new IllegalStateException("File uploaded is not an image");
		}
		return true;
	}

	public static boolean isValidPdfDocFile(MultipartFile file) throws JsonProcessingException {

		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file");
		}
		if (!Arrays.asList("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
				"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-powerpoint",
				"application/vnd.openxmlformats-officedocument.presentationml.presentation",
				"application/vnd.openxmlformats-officedocument.presentationml.slideshow").contains(file.getContentType())) {
			throw new IllegalStateException("File uploaded is not a PDF/Doc");
		}
		return true;
	}
}
