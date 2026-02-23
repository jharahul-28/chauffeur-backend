package com.velvet.rest.common.exceptions.handler;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.beans.ErrorResponse;
import com.velvet.rest.common.exceptions.AuthenticationException;
import com.velvet.rest.common.exceptions.ConfigurationException;
import com.velvet.rest.common.exceptions.NoDataException;
import com.velvet.rest.common.exceptions.PrivilegeException;
import com.velvet.rest.common.exceptions.ServerException;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.logging.AppLogger;
import com.velvet.rest.common.logging.LoggingManager;
import com.velvet.rest.common.properties.Errors;
import com.velvet.rest.common.utils.BeanValidationUtils;

/**
 * The Class RestExceptionAdvice.
 *
 * @author Mohit
 * @version $Id: $Id
 */
@RestControllerAdvice
public class RestExceptionAdvice {

	/** The Constant _LOGGER. */
	private static final AppLogger _LOGGER = LoggingManager.getLogger(RestExceptionAdvice.class);

	/**
	 * Prepare server exception.
	 *
	 * @param ex the ex
	 * @return the error response
	 */
	@ExceptionHandler(ServerException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse prepareServerException(ServerException ex) {
		_LOGGER.error(ex.getMessage(), ex);
		ErrorResponse response = ErrorResponse.create().message(ex.getMessage()).addError(ex.getError());
		return logAndReturn(response, ex);
	}

	/**
	 * Prepare validation exception.
	 *
	 * @param ex the ex
	 * @return the error response
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ErrorResponse prepareValidationException(ValidationException ex) {
		_LOGGER.info(ex.getMessage());
		ErrorResponse response = BeanValidationUtils.prepareValidationResponse(ex);
		return logAndReturn(response, ex);
	}

	/**
	 * Prepare no data exception.
	 *
	 * @param ex the ex
	 * @return the error response
	 */
	@ExceptionHandler(NoDataException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ErrorResponse prepareNoDataException(NoDataException ex) {
		_LOGGER.error(ex.getMessage(), ex);
		ErrorResponse response = ErrorResponse.create().message(ex.getMessage()).addError(ex.getError());
		return logAndReturn(response, ex);
	}

	/**
	 * Prepare forbidden exception.
	 *
	 * @param ex the ex
	 * @return the error response
	 */
	@ExceptionHandler(PrivilegeException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ErrorResponse preparePrivilegeException(PrivilegeException ex, HttpServletResponse servletResponse) {
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		_LOGGER.error(ex.getMessage());
		ErrorResponse response = ErrorResponse.create().message(ex.getMessage()).addError(ex.getError());
		return logAndReturn(response, ex);
	}

	/**
	 * Prepare auth exception.
	 *
	 * @param ex the ex
	 * @return the error response
	 */
	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ErrorResponse prepareAuthException(AuthenticationException ex, HttpServletResponse servletResponse) {
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		_LOGGER.error(ex.getMessage());
		ErrorResponse response = ErrorResponse.create().message(ex.getMessage()).addError(ex.getError());
		return logAndReturn(response, ex);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler(ConfigurationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleException(ConfigurationException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		ErrorResponse response = ErrorResponse.create().message("Configuration Error").addError(ErrorBean.withError(Errors.SERVER_EXCEPTION));
		return logAndReturn(response, exception);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleException(IllegalArgumentException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		ErrorResponse response = ErrorResponse.create().message(exception.getMessage()).addError(ErrorBean.withError(Errors.UNSUPPORTED_DB_OPERATION));
		return logAndReturn(response, exception);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler(NumberFormatException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleException(NumberFormatException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		ErrorResponse response = ErrorResponse.create().message(exception.getMessage()).addError(ErrorBean.withError(Errors.INVALID_NUMBER));
		return logAndReturn(response, exception);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleException(MethodArgumentNotValidException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		ErrorResponse response = ErrorResponse.create().message("Validation Errors").errors(exception.getBindingResult().getFieldErrors().parallelStream()
				.map(error -> BeanValidationUtils.prepareError(error)).collect(Collectors.toList()));
		return logAndReturn(response, exception);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler({ InvalidFormatException.class, JsonMappingException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleException(JsonMappingException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		String message = StringUtils.isEmpty(exception.getOriginalMessage()) ? ErrorBean.withError(Errors.INVALID_REQUEST_PAYLOAD).getMessage()
				: exception.getOriginalMessage();
		ErrorResponse response = ErrorResponse.create().message(ErrorBean.withError(Errors.INVALID_REQUEST_PAYLOAD).getMessage())
				.addError(ErrorBean.withError(Errors.INVALID_REQUEST_PAYLOAD, message, exception.getPathReference()));
		return logAndReturn(response, exception);
	}

	/**
	 * Handle exception.
	 *
	 * @param exception the exception
	 * @return the error response
	 */
	@ExceptionHandler({ JsonParseException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleException(JsonParseException exception) {
		_LOGGER.error(exception.getMessage(), exception);
		ErrorResponse response = ErrorResponse.create().message(ErrorBean.withError(Errors.INVALID_REQUEST_PAYLOAD).getMessage())
				.addError(ErrorBean.withError(Errors.INVALID_REQUEST_PAYLOAD, exception.getMessage()));
		return logAndReturn(response, exception);
	}

	public ErrorResponse logAndReturn(ErrorResponse response, Exception ex) {
		try {
//			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//			_LOGGER.info("request string: " + request.getReader().lines().collect(Collectors.joining()));
//			AdminErrorLog log = AdminErrorLog.newInstance(ex);
//			log.setErrors(response.getErrors());
//			LoggingUtils.getContext().addAdminError(log);
		} catch (Exception e) {
			_LOGGER.warn(e.getMessage());
		}
		return response;
	}
}
