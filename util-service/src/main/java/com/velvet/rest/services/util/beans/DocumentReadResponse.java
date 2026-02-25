package com.velvet.rest.services.util.beans;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentReadResponse {

	private boolean success;
	private String extractedText;
	private String documentType;
	private String fileName;
	private Integer pageCount;
	private Map<String, Object> metadata;
	private List<String> pages;
	private Map<String, Object> structuredData;
	private String errorMessage;
	private Long processingTimeMs;
	private Double confidence;

	public static DocumentReadResponse success(String extractedText, String documentType, String fileName) {
		return DocumentReadResponse.builder()
				.success(true)
				.extractedText(extractedText)
				.documentType(documentType)
				.fileName(fileName)
				.build();
	}

	public static DocumentReadResponse error(String errorMessage) {
		return DocumentReadResponse.builder()
				.success(false)
				.errorMessage(errorMessage)
				.build();
	}
} 