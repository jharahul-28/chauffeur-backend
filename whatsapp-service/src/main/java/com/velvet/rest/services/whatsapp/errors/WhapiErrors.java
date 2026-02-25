package com.velvet.rest.services.whatsapp.errors;

import com.velvet.rest.common.properties.Errors;

/**
 * Error constants for Whapi service.
 *
 * @author mohit
 */
public class WhapiErrors extends Errors {

	public static final String WEBHOOK_PROCESSING_FAILED = "webhook_processing_failed";

	public static final String INVALID_WEBHOOK_PAYLOAD = "invalid_webhook_payload";

	public static final String GROUP_NOT_FOUND = "group_not_found";

	public static final String MESSAGE_NOT_FOUND = "message_not_found";

	public static final String INVALID_MESSAGE_TYPE = "invalid_message_type";

	public static final String CHANNEL_NOT_FOUND = "channel_not_found";
}
