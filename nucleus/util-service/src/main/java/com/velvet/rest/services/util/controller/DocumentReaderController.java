package com.velvet.rest.services.util.controller;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.velvet.rest.common.websupport.base.BaseController;
import com.velvet.rest.services.common.mapper.BaseMapper;
import com.velvet.rest.services.util.beans.DocumentReadResponse;
import com.velvet.rest.services.util.services.DocumentReaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for reading and extracting text data from PDF and image files
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestController
@Api(tags = "Document Reader Services", description = "Extract text from PDF and image files using OCR")
public class DocumentReaderController implements BaseController {

	private static final Logger logger = LoggerFactory.getLogger(DocumentReaderController.class);

	@Autowired
	private DocumentReaderService documentReaderService;

	@Autowired
	private BaseMapper<?, ?> baseMapper;

	@ApiOperation(value = "Extract text from PDF file", nickname = "extractTextFromPdf")
	@PostMapping("/utils/documents/pdf/extract-text")
	public ResponseEntity<DocumentReadResponse> extractTextFromPdf(
			@ApiParam(value = "PDF file to extract text from", required = true) 
			@RequestParam @NotNull MultipartFile file,
			@ApiParam(value = "Extract structured data (tables, forms)", required = false) 
			@RequestParam(defaultValue = "false") boolean extractStructuredData) {
		try {
			DocumentReadResponse response = documentReaderService.extractTextFromPdf(file, extractStructuredData);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error extracting text from PDF: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(DocumentReadResponse.error("Error extracting text from PDF: " + e.getMessage()));
		}
	}

	@ApiOperation(value = "Extract text from image using OCR", nickname = "extractTextFromImage")
	@PostMapping("/utils/documents/image/extract-text")
	public ResponseEntity<DocumentReadResponse> extractTextFromImage(
			@ApiParam(value = "Image file to extract text from", required = true) 
			@RequestParam @NotNull MultipartFile file,
			@ApiParam(value = "OCR language code (e.g., 'eng', 'spa', 'fra')", required = false) 
			@RequestParam(defaultValue = "eng") String language,
			@ApiParam(value = "Image preprocessing (enhance quality)", required = false) 
			@RequestParam(defaultValue = "true") boolean preprocess) {
		try {
			DocumentReadResponse response = documentReaderService.extractTextFromImage(file, language, preprocess);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error extracting text from image: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(DocumentReadResponse.error("Error extracting text from image: " + e.getMessage()));
		}
	}

	@ApiOperation(value = "Auto-detect file type and extract text", nickname = "extractTextFromDocument")
	@PostMapping("/utils/documents/extract-text")
	public ResponseEntity<DocumentReadResponse> extractTextFromDocument(
			@ApiParam(value = "Document file (PDF or image)", required = true) 
			@RequestParam @NotNull MultipartFile file,
			@ApiParam(value = "OCR language for images", required = false) 
			@RequestParam(defaultValue = "eng") String language,
			@ApiParam(value = "Extract structured data for PDFs", required = false) 
			@RequestParam(defaultValue = "false") boolean extractStructuredData,
			@ApiParam(value = "Image preprocessing", required = false) 
			@RequestParam(defaultValue = "true") boolean preprocess) {
		try {
			DocumentReadResponse response = documentReaderService.extractTextFromDocument(file, language, extractStructuredData, preprocess);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error extracting text from document: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(DocumentReadResponse.error("Error extracting text from document: " + e.getMessage()));
		}
	}

	@ApiOperation(value = "Extract specific data patterns from document", nickname = "extractDataPatterns")
	@PostMapping("/utils/documents/extract-patterns")
	public ResponseEntity<Map<String, Object>> extractDataPatterns(
			@ApiParam(value = "Document file (PDF or image)", required = true) 
			@RequestParam @NotNull MultipartFile file,
			@ApiParam(value = "Pattern types to extract (emails,phones,dates,amounts,addresses)", required = false) 
			@RequestParam(defaultValue = "emails,phones,dates,amounts") String patterns,
			@ApiParam(value = "OCR language for images", required = false) 
			@RequestParam(defaultValue = "eng") String language) {
		try {
			Map<String, Object> extractedPatterns = documentReaderService.extractDataPatterns(file, patterns, language);
			return ResponseEntity.ok(extractedPatterns);
		} catch (Exception e) {
			logger.error("Error extracting data patterns: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error extracting data patterns: " + e.getMessage()));
		}
	}

	@ApiOperation(value = "Convert document to searchable PDF", nickname = "convertToSearchablePdf")
	@PostMapping("/utils/documents/convert-to-searchable-pdf")
	public ResponseEntity<Map<String, Object>> convertToSearchablePdf(
			@ApiParam(value = "Image file to convert", required = true) 
			@RequestParam @NotNull MultipartFile file,
			@ApiParam(value = "OCR language", required = false) 
			@RequestParam(defaultValue = "eng") String language) {
		try {
			Map<String, Object> result = documentReaderService.convertToSearchablePdf(file, language);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			logger.error("Error converting to searchable PDF: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Error converting to searchable PDF: " + e.getMessage()));
		}
	}

	@Override
	public BaseMapper<?, ?> getMapper() {
		return baseMapper;
	}
} 