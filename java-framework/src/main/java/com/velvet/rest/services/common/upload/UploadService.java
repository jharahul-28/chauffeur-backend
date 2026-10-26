package com.velvet.rest.services.common.upload;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface UploadService {

	public Object uploadFile(String bucketName, String subdirectory, byte[] byteArray, String fileName) throws IOException;

	public String presignedPutUrl(String bucketName, String keyPrefix, long expiryDurationInMinutes);

	public default String findFileExt(String fname) {
		String ext = "";
		int i = fname.lastIndexOf('#');
		if (i > 0) {
			fname = fname.substring(0, i - 1);
		}
		i = fname.lastIndexOf('.');
		// REMIND: OS specific delimters appear here
		i = Math.max(i, fname.lastIndexOf('/'));
		i = Math.max(i, fname.lastIndexOf('?'));
		if (i != -1 && fname.charAt(i) == '.') {
			ext = fname.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
