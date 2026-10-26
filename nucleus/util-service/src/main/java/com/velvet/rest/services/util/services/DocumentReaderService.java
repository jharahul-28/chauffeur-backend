package com.velvet.rest.services.util.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.velvet.rest.services.common.upload.UploadServiceUtil;
import com.velvet.rest.services.util.beans.DocumentReadResponse;

@Service
public class DocumentReaderService {

	@Value("${openai.api.key:}")
	private String openaiApiKey;

	@Value("${openai.api.url:https://api.openai.com/v1}")
	private String openaiApiUrl;

	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final List<String> SUPPORTED_IMAGE_FORMATS = Arrays.asList(
			"image/jpeg", "image/jpg", "image/png", "image/bmp", "image/tiff", "image/gif");

	public DocumentReadResponse extractTextFromPdf(MultipartFile file, boolean extractStructuredData) throws IOException {
		long startTime = System.currentTimeMillis();
		
		// Validate PDF file
		if (!file.getContentType().equals("application/pdf")) {
			throw new IllegalArgumentException("File must be a PDF");
		}

		UploadServiceUtil.isValidPdfDocFile(file);

		try {
			// Convert PDF to base64 for OpenAI API
			String base64Content = Base64.getEncoder().encodeToString(file.getBytes());
			
			// Create OpenAI API request for PDF text extraction
			Map<String, Object> requestBody = createOpenAiPdfRequest(base64Content, extractStructuredData);
			
			// Call OpenAI API
			String extractedText = callOpenAiApi(requestBody);

			DocumentReadResponse.DocumentReadResponseBuilder responseBuilder = DocumentReadResponse.builder()
					.success(true)
					.extractedText(extractedText.trim())
					.documentType("PDF")
					.fileName(file.getOriginalFilename())
					.processingTimeMs(System.currentTimeMillis() - startTime);

			// Add metadata
			Map<String, Object> metadata = new HashMap<>();
			metadata.put("fileSize", file.getSize());
			metadata.put("contentType", file.getContentType());
			metadata.put("extractionMethod", "OpenAI API");
			responseBuilder.metadata(metadata);

			// Extract structured data if requested
			if (extractStructuredData) {
				Map<String, Object> structuredData = extractStructuredDataFromText(extractedText);
				responseBuilder.structuredData(structuredData);
			}

			return responseBuilder.build();
		} catch (Exception e) {
			throw new RuntimeException("Error extracting text from PDF using OpenAI: " + e.getMessage(), e);
		}
	}

	public DocumentReadResponse extractTextFromImage(MultipartFile file, String language, boolean preprocess) {
		long startTime = System.currentTimeMillis();

		// Validate image file
		if (!SUPPORTED_IMAGE_FORMATS.contains(file.getContentType().toLowerCase())) {
			throw new IllegalArgumentException("Unsupported image format: " + file.getContentType());
		}

		try {
			UploadServiceUtil.isValidImgFile(file);

			// Convert image to base64 for OpenAI Vision API
			String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
			
			// Create OpenAI Vision API request
			Map<String, Object> requestBody = createOpenAiVisionRequest(base64Image, language, file.getContentType());
			
			// Call OpenAI Vision API
			String extractedText = callOpenAiVisionApi(requestBody);

			// Calculate confidence score (simplified)
			double confidence = calculateOcrConfidence(extractedText);

			Map<String, Object> metadata = new HashMap<>();
			metadata.put("fileSize", file.getSize());
			metadata.put("contentType", file.getContentType());
			metadata.put("language", language);
			metadata.put("extractionMethod", "OpenAI Vision API");

			return DocumentReadResponse.builder()
					.success(true)
					.extractedText(extractedText.trim())
					.documentType("IMAGE")
					.fileName(file.getOriginalFilename())
					.confidence(confidence)
					.metadata(metadata)
					.processingTimeMs(System.currentTimeMillis() - startTime)
					.build();

		} catch (Exception e) {
			throw new RuntimeException("Error performing OCR on image using OpenAI: " + e.getMessage(), e);
		}
	}

	public DocumentReadResponse extractTextFromDocument(MultipartFile file, String language, 
			boolean extractStructuredData, boolean preprocess) throws IOException {
		
		String contentType = file.getContentType().toLowerCase();
		
		if (contentType.equals("application/pdf")) {
			return extractTextFromPdf(file, extractStructuredData);
		} else if (SUPPORTED_IMAGE_FORMATS.contains(contentType)) {
			return extractTextFromImage(file, language, preprocess);
		} else {
			throw new IllegalArgumentException("Unsupported file type: " + contentType);
		}
	}

	public Map<String, Object> extractDataPatterns(MultipartFile file, String patterns, String language) throws IOException {
		// First extract text from the document
		DocumentReadResponse response = extractTextFromDocument(file, language, false, true);
		
		if (!response.isSuccess()) {
			throw new RuntimeException("Failed to extract text from document");
		}

		String text = response.getExtractedText();
		Map<String, Object> extractedData = new HashMap<>();

		// Use OpenAI to extract structured patterns
		Map<String, Object> aiExtractedPatterns = extractPatternsWithAI(text, patterns);
		extractedData.putAll(aiExtractedPatterns);

		// Also extract using regex as fallback
		List<String> patternTypes = Arrays.asList(patterns.split(","));
		for (String patternType : patternTypes) {
			String key = patternType.trim().toLowerCase() + "_regex";
			switch (patternType.trim().toLowerCase()) {
				case "emails":
					extractedData.put(key, extractEmails(text));
					break;
				case "phones":
					extractedData.put(key, extractPhoneNumbers(text));
					break;
				case "dates":
					extractedData.put(key, extractDates(text));
					break;
				case "amounts":
					extractedData.put(key, extractMonetaryAmounts(text));
					break;
				case "addresses":
					extractedData.put(key, extractAddresses(text));
					break;
			}
		}

		extractedData.put("documentInfo", response);
		return extractedData;
	}

	public Map<String, Object> convertToSearchablePdf(MultipartFile file, String language) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			// Extract text using OpenAI
			DocumentReadResponse textResponse = extractTextFromDocument(file, language, false, true);
			
			if (textResponse.isSuccess()) {
				result.put("extractedText", textResponse.getExtractedText());
				result.put("originalFile", file.getOriginalFilename());
				result.put("language", language);
				result.put("status", "text_extracted");
				result.put("message", "Text extracted successfully. PDF creation requires additional implementation.");
			} else {
				result.put("status", "error");
				result.put("message", "Failed to extract text from document");
			}
		} catch (Exception e) {
			result.put("status", "error");
			result.put("message", "Error processing document: " + e.getMessage());
		}
		
		return result;
	}

	// Private helper methods

	private Map<String, Object> createOpenAiPdfRequest(String base64Content, boolean extractStructuredData) {
		Map<String, Object> request = new HashMap<>();
		request.put("model", "gpt-4");
		
		List<Map<String, Object>> messages = new ArrayList<>();
		Map<String, Object> message = new HashMap<>();
		message.put("role", "user");
		
		String prompt = "Extract all text content from this PDF document. Maintain the original structure and formatting as much as possible.";
		if (extractStructuredData) {
			prompt += " Also identify any tables, forms, or structured data elements.";
		}
		
		message.put("content", prompt + "\n\nPDF Content (base64): " + base64Content.substring(0, Math.min(base64Content.length(), 50000))); // Limit content size
		messages.add(message);
		
		request.put("messages", messages);
		request.put("max_tokens", 4000);
		request.put("temperature", 0.1);
		
		return request;
	}

	private Map<String, Object> createOpenAiVisionRequest(String base64Image, String language, String contentType) {
		Map<String, Object> request = new HashMap<>();
		request.put("model", "gpt-4-vision-preview");
		
		List<Map<String, Object>> messages = new ArrayList<>();
		Map<String, Object> message = new HashMap<>();
		message.put("role", "user");
		
		List<Map<String, Object>> content = new ArrayList<>();
		
		// Text content
		Map<String, Object> textContent = new HashMap<>();
		textContent.put("type", "text");
		textContent.put("text", "Extract all text from this image. Be as accurate as possible and maintain the original structure. Language: " + language);
		content.add(textContent);
		
		// Image content
		Map<String, Object> imageContent = new HashMap<>();
		imageContent.put("type", "image_url");
		Map<String, Object> imageUrl = new HashMap<>();
		imageUrl.put("url", "data:" + contentType + ";base64," + base64Image);
		imageContent.put("image_url", imageUrl);
		content.add(imageContent);
		
		message.put("content", content);
		messages.add(message);
		
		request.put("messages", messages);
		request.put("max_tokens", 4000);
		request.put("temperature", 0.1);
		
		return request;
	}

	private String callOpenAiApi(Map<String, Object> requestBody) throws IOException {
		if (openaiApiKey == null || openaiApiKey.isEmpty()) {
			throw new RuntimeException("OpenAI API key not configured");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(openaiApiKey);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					openaiApiUrl + "/chat/completions",
					HttpMethod.POST,
					entity,
					String.class
			);

			JsonNode jsonResponse = objectMapper.readTree(response.getBody());
			return jsonResponse.path("choices").get(0).path("message").path("content").asText();
		} catch (Exception e) {
			throw new RuntimeException("Error calling OpenAI API: " + e.getMessage(), e);
		}
	}

	private String callOpenAiVisionApi(Map<String, Object> requestBody) throws IOException {
		return callOpenAiApi(requestBody); // Same endpoint for vision
	}

	private Map<String, Object> extractPatternsWithAI(String text, String patterns) {
		Map<String, Object> result = new HashMap<>();
		
		try {
			Map<String, Object> request = new HashMap<>();
			request.put("model", "gpt-4");
			
			List<Map<String, Object>> messages = new ArrayList<>();
			Map<String, Object> message = new HashMap<>();
			message.put("role", "user");
			
			String prompt = String.format(
				"Extract the following data patterns from this text and return as JSON: %s\n\n" +
				"Text: %s\n\n" +
				"Return only valid JSON with arrays for each pattern type.",
				patterns, text.substring(0, Math.min(text.length(), 3000))
			);
			
			message.put("content", prompt);
			messages.add(message);
			
			request.put("messages", messages);
			request.put("max_tokens", 1000);
			request.put("temperature", 0.1);
			
			String aiResponse = callOpenAiApi(request);
			
			// Try to parse AI response as JSON
			try {
				JsonNode jsonNode = objectMapper.readTree(aiResponse);
				result.put("ai_extracted", jsonNode);
			} catch (Exception e) {
				result.put("ai_extracted", aiResponse);
			}
			
		} catch (Exception e) {
			result.put("ai_extraction_error", e.getMessage());
		}
		
		return result;
	}

	private Map<String, Object> extractStructuredDataFromText(String text) {
		Map<String, Object> structuredData = new HashMap<>();
		
		// Use AI to identify structured elements
		try {
			Map<String, Object> request = new HashMap<>();
			request.put("model", "gpt-4");
			
			List<Map<String, Object>> messages = new ArrayList<>();
			Map<String, Object> message = new HashMap<>();
			message.put("role", "user");
			message.put("content", "Analyze this text and identify any tables, forms, lists, or other structured data elements. Return as JSON:\n\n" + 
									text.substring(0, Math.min(text.length(), 3000)));
			messages.add(message);
			
			request.put("messages", messages);
			request.put("max_tokens", 1000);
			request.put("temperature", 0.1);
			
			String aiResponse = callOpenAiApi(request);
			
			try {
				JsonNode jsonNode = objectMapper.readTree(aiResponse);
				structuredData.put("ai_analysis", jsonNode);
			} catch (Exception e) {
				structuredData.put("ai_analysis", aiResponse);
			}
			
		} catch (Exception e) {
			structuredData.put("analysis_error", e.getMessage());
		}
		
		// Basic text analysis
		structuredData.put("hasNumbers", text.matches(".*\\d.*"));
		structuredData.put("hasEmails", text.matches(".*@.*\\..*"));
		structuredData.put("hasDates", text.matches(".*\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}.*"));
		structuredData.put("wordCount", text.split("\\s+").length);
		structuredData.put("lineCount", text.split("\n").length);
		
		return structuredData;
	}

	private double calculateOcrConfidence(String text) {
		// Simplified confidence calculation
		if (text == null || text.trim().isEmpty()) {
			return 0.0;
		}
		
		// Basic heuristic: longer text with proper spacing typically indicates better OCR
		int words = text.split("\\s+").length;
		int characters = text.length();
		
		if (characters < 10) return 0.3;
		if (words < 3) return 0.4;
		
		// Check for common OCR errors (repeated special characters)
		if (text.matches(".*[^a-zA-Z0-9\\s]{3,}.*")) return 0.5;
		
		return Math.min(0.95, 0.6 + (words * 0.05));
	}

	private List<String> extractEmails(String text) {
		List<String> emails = new ArrayList<>();
		Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
		Matcher matcher = emailPattern.matcher(text);
		while (matcher.find()) {
			emails.add(matcher.group());
		}
		return emails;
	}

	private List<String> extractPhoneNumbers(String text) {
		List<String> phones = new ArrayList<>();
		// Pattern for various phone number formats
		Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
		Matcher matcher = phonePattern.matcher(text);
		while (matcher.find()) {
			phones.add(matcher.group());
		}
		return phones;
	}

	private List<String> extractDates(String text) {
		List<String> dates = new ArrayList<>();
		// Pattern for various date formats
		Pattern datePattern = Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{2,4}[/-]\\d{1,2}[/-]\\d{1,2})\\b");
		Matcher matcher = datePattern.matcher(text);
		while (matcher.find()) {
			dates.add(matcher.group());
		}
		return dates;
	}

	private List<String> extractMonetaryAmounts(String text) {
		List<String> amounts = new ArrayList<>();
		// Pattern for monetary amounts
		Pattern amountPattern = Pattern.compile("[$£€¥]\\s?\\d{1,3}(,\\d{3})*(\\.\\d{2})?|\\d{1,3}(,\\d{3})*(\\.\\d{2})?\\s?[$£€¥]");
		Matcher matcher = amountPattern.matcher(text);
		while (matcher.find()) {
			amounts.add(matcher.group());
		}
		return amounts;
	}

	private List<String> extractAddresses(String text) {
		List<String> addresses = new ArrayList<>();
		// Simplified address pattern - in reality you'd use more sophisticated NLP
		Pattern addressPattern = Pattern.compile("\\d+\\s+[A-Za-z\\s]+(?:Street|St|Avenue|Ave|Road|Rd|Boulevard|Blvd|Lane|Ln|Drive|Dr)(?:\\s+[A-Za-z\\s]*)?(?:\\s+\\d{5})?", Pattern.CASE_INSENSITIVE);
		Matcher matcher = addressPattern.matcher(text);
		while (matcher.find()) {
			addresses.add(matcher.group());
		}
		return addresses;
	}
} 