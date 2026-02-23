package com.velvet.rest.common.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.velvet.rest.common.beans.ErrorBean;
import com.velvet.rest.common.beans.ErrorResponse;
import com.velvet.rest.common.exceptions.ValidationException;
import com.velvet.rest.common.properties.Errors;

/**
 * <p>
 * BeanValidationUtils class.
 * </p>
 *
 * @author Mohit
 * @version $Id: $Id
 */
public class BeanValidationUtils {

	/**
	 * Prepare error.
	 *
	 * @param error the error
	 * @return the error bean
	 */
	public static ErrorBean prepareError(FieldError error) {
		String code = error.getCode();
		String errorKey = "bean.validation." + code;

		ErrorBean errorBean = ErrorBean.withError(errorKey);
		errorBean.setField(error.getField());
		errorBean.setCode(code);

		String message = MessageFormat.format(errorBean.getMessage(), error.getField());

		if (!StringUtils.isEmpty(code)) {
			switch (code) {
			case Errors.MAX_LENGTH:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.MIN_LENGTH:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.MAX:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.MIN:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.LENGTH_EQUALS:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.DATE_FORMAT:
				Object argdt = getArgument(error, 1);
				message = MessageFormat.format(message, null, argdt != null ? ((MessageSourceResolvable) argdt).getDefaultMessage() : null);
				break;
			case Errors.FLOAT_MAX_LENGTH:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.FLOAT_MIN_LENGTH:
				message = MessageFormat.format(message, null, getArgument(error, 1));
				break;
			case Errors.DECIMAL_MAX:
				Object argmax = getArgument(error, 2);
				message = MessageFormat.format(message, null, argmax != null ? ((MessageSourceResolvable) argmax).getDefaultMessage() : null);
				break;
			case Errors.DECIMAL_MIN:
				Object argm = getArgument(error, 2);
				message = MessageFormat.format(message, null, argm != null ? ((MessageSourceResolvable) argm).getDefaultMessage() : null);
				break;
			case Errors.DECIMAL_FORMAT:
				Object argd = getArgument(error, 1);
				message = MessageFormat.format(message, null, argd != null ? ((MessageSourceResolvable) argd).getDefaultMessage() : null);
				break;
			default:
			}
		}

		errorBean.setMessage(message);
		if (!StringUtils.isEmpty(message) && message.startsWith("bean.validation.")) {
			String errorMessage = message.replace("bean.validation.", "");
			String resolvedMessage = Errors.error(errorMessage, error.getArguments()).getMessage();
			errorBean.setMessage(errorMessage);

			if (resolvedMessage.equalsIgnoreCase(errorMessage)) {
				errorBean.setMessage(error.getDefaultMessage());
			}
		}

		return errorBean;
	}

	/**
	 * <p>
	 * addValidationError.
	 * </p>
	 *
	 * @param ex               a {@link com.velvet.rest.common.exceptions.ValidationException} object.
	 * @param validationErrors a {@link java.util.List} object.
	 */
	public static void addValidationError(ValidationException ex, List<ErrorBean> validationErrors) {
		for (ObjectError error : ex.getValidationErrors().getGlobalErrors()) {
			ErrorBean errorBean = Errors.error(error.getCode(), error.getArguments());
			errorBean.setField(error.getObjectName());
			if (!StringUtils.isEmpty(error.getDefaultMessage())) {
				errorBean.setMessage(error.getDefaultMessage());
			}
			validationErrors.add(errorBean);
		}
	}

	/**
	 * <p>
	 * prepareValidationResponse.
	 * </p>
	 *
	 * @param e a {@link com.velvet.rest.common.exceptions.ValidationException} object.
	 * @return a {@link com.velvet.rest.common.beans.ErrorResponse} object.
	 */
	public static ErrorResponse prepareValidationResponse(ValidationException e) {
		List<ErrorBean> validationErrors = new ArrayList<>();
		if (e.getValidationErrors() != null) {
			if (!CollectionUtils.isEmpty(e.getValidationErrors().getFieldErrors())) {
				for (FieldError error : e.getValidationErrors().getFieldErrors()) {
					validationErrors.add(BeanValidationUtils.prepareError(error));
				}
			}
			if (!CollectionUtils.isEmpty(e.getValidationErrors().getGlobalErrors())) {
				BeanValidationUtils.addValidationError(e, validationErrors);
			}
		}
		e.getErrors().addAll(validationErrors);
		return ErrorResponse.create().message(e.getMessage() != null ? e.getMessage() : "Validation Errors").errors(e.getErrors());
	}

	/**
	 * Gets the argument.
	 *
	 * @param error the error
	 * @param index the index
	 * @return the argument
	 */
	private static Object getArgument(@Nullable FieldError error, int index) {
		if (error != null) {
			Object[] args = error.getArguments();
			if (args != null && args.length > index && index >= 0) {
				return args[index];
			}
		}
		return null;
	}
}
