package com.velvet.rest.services.whatsapp.constants;

/**
 * Constants for Whapi service.
 *
 * @author mohit
 */
public class WhapiConstants {

	private WhapiConstants() {
	}

	/**
	 * Message types from Whapi.
	 */
	public enum MessageType {
		TEXT, POLL, ACTION, IMAGE, DOCUMENT, AUDIO, VIDEO, STICKER, LOCATION, CONTACT, REACTION, UNKNOWN;

		public static MessageType fromString(String type) {
			if (type == null) {
				return UNKNOWN;
			}
			try {
				return valueOf(type.toUpperCase());
			} catch (IllegalArgumentException e) {
				return UNKNOWN;
			}
		}
	}

	/**
	 * Webhook event types.
	 */
	public enum WebhookEventType {
		POST, PUT, PATCH, DELETE;

		public static WebhookEventType fromString(String event) {
			if (event == null) {
				return POST;
			}
			try {
				return valueOf(event.toUpperCase());
			} catch (IllegalArgumentException e) {
				return POST;
			}
		}
	}

	/**
	 * Webhook payload types.
	 */
	public enum WebhookPayloadType {
		GROUPS, MESSAGES, CHATS, STATUSES;

		public static WebhookPayloadType fromString(String type) {
			if (type == null) {
				return MESSAGES;
			}
			try {
				return valueOf(type.toUpperCase());
			} catch (IllegalArgumentException e) {
				return MESSAGES;
			}
		}
	}

	/**
	 * Participant ranks in groups.
	 */
	public enum ParticipantRank {
		CREATOR, ADMIN, MEMBER;

		public static ParticipantRank fromString(String rank) {
			if (rank == null) {
				return MEMBER;
			}
			try {
				return valueOf(rank.toUpperCase());
			} catch (IllegalArgumentException e) {
				return MEMBER;
			}
		}
	}

	/**
	 * Action types for message actions.
	 */
	public enum ActionType {
		VOTE, REACTION, DELETE, EDIT;

		public static ActionType fromString(String type) {
			if (type == null) {
				return VOTE;
			}
			try {
				return valueOf(type.toUpperCase());
			} catch (IllegalArgumentException e) {
				return VOTE;
			}
		}
	}
}
