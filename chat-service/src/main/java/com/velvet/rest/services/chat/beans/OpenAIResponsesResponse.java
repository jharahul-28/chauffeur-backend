package com.velvet.rest.services.chat.beans;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Full POJO for the OpenAI Responses API response object.
 * Maps the complete structure returned by POST /v1/responses.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAIResponsesResponse {

	/**
	 * Unique response identifier. Used as previous_response_id in the next turn.
	 */
	@JsonProperty("id")
	private String id;

	/** Always "response". */
	@JsonProperty("object")
	private String object;

	/** Unix timestamp of when the response was created. */
	@JsonProperty("created_at")
	private Long createdAt;

	/** Unix timestamp of when the response was completed. */
	@JsonProperty("completed_at")
	private Long completedAt;

	/** "completed" | "in_progress" | "failed" | "cancelled" */
	@JsonProperty("status")
	private String status;

	/** Whether the request ran in background mode. */
	@JsonProperty("background")
	private Boolean background;

	/** Billing information. */
	@JsonProperty("billing")
	private Billing billing;

	/** Non-null only if the response ended prematurely. */
	@JsonProperty("incomplete_details")
	private IncompleteDetails incompleteDetails;

	/** Non-null if an error occurred. */
	@JsonProperty("error")
	private ApiError error;

	/** The system instructions sent with this response (echoed back). */
	@JsonProperty("instructions")
	private String instructions;

	/** The model used, e.g. "gpt-5-nano-2025-08-07". */
	@JsonProperty("model")
	private String model;

	/**
	 * List of output items. Each item is either a reasoning block or a message
	 * block. Use {@link #getFirstTextContent()} for the assistant's text reply.
	 */
	@JsonProperty("output")
	private List<OutputItem> output;

	/** Max output tokens allowed (null = no limit set). */
	@JsonProperty("max_output_tokens")
	private Integer maxOutputTokens;

	/** Max tool calls allowed (null = no limit set). */
	@JsonProperty("max_tool_calls")
	private Integer maxToolCalls;

	/** The response ID this response is chained from (null on first turn). */
	@JsonProperty("previous_response_id")
	private String previousResponseId;

	/** Frequency penalty (0.0 default). */
	@JsonProperty("frequency_penalty")
	private Double frequencyPenalty;

	/** Presence penalty (0.0 default). */
	@JsonProperty("presence_penalty")
	private Double presencePenalty;

	/** Sampling temperature. */
	@JsonProperty("temperature")
	private Double temperature;

	/** Top-p nucleus sampling. */
	@JsonProperty("top_p")
	private Double topP;

	/** Number of top log-probs returned per token. */
	@JsonProperty("top_logprobs")
	private Integer topLogprobs;

	/** Whether parallel tool calls are enabled. */
	@JsonProperty("parallel_tool_calls")
	private Boolean parallelToolCalls;

	/** "auto" | "none" | specific tool name. */
	@JsonProperty("tool_choice")
	private String toolChoice;

	/** Tools made available to the model. */
	@JsonProperty("tools")
	private List<Object> tools;

	/** "disabled" | "auto" — controls context truncation behaviour. */
	@JsonProperty("truncation")
	private String truncation;

	/** Service tier used for this request. */
	@JsonProperty("service_tier")
	private String serviceTier;

	/** Whether this response is stored on OpenAI's side. */
	@JsonProperty("store")
	private Boolean store;

	/** Prompt cache key (if prompt caching is enabled). */
	@JsonProperty("prompt_cache_key")
	private String promptCacheKey;

	/** Prompt cache retention setting. */
	@JsonProperty("prompt_cache_retention")
	private Object promptCacheRetention;

	/** Safety identifier (if applicable). */
	@JsonProperty("safety_identifier")
	private String safetyIdentifier;

	/** The end-user identifier passed by the caller. */
	@JsonProperty("user")
	private String user;

	/** Caller-supplied metadata key-value pairs. */
	@JsonProperty("metadata")
	private Map<String, Object> metadata;

	/** Text output configuration. */
	@JsonProperty("text")
	private TextConfig text;

	/** Reasoning configuration (effort level + summary mode). */
	@JsonProperty("reasoning")
	private ReasoningConfig reasoning;

	/** Token usage breakdown for this response. */
	@JsonProperty("usage")
	private Usage usage;

	// =========================================================================
	// Convenience helper
	// =========================================================================

	/**
	 * Extracts the first reasoning summary text from the output list.
	 * Returns null if reasoning was not requested or summary is empty.
	 */
	public String getFirstReasoningSummaryText() {
		if (output == null)
			return null;
		return output.stream()
				.filter(o -> "reasoning".equals(o.getType()))
				.filter(o -> o.getSummary() != null)
				.flatMap(o -> o.getSummary().stream())
				.filter(s -> "summary_text".equals(s.getType()))
				.map(ReasoningSummary::getText)
				.filter(t -> t != null && !t.isBlank())
				.findFirst().orElse(null);
	}

	/**
	 * Extracts the first assistant text reply from the output list. Skips reasoning
	 * blocks; returns null if no text content is present.
	 */
	public String getFirstTextContent() {
		if (output == null)
			return null;
		return output.stream()
				.filter(o -> "message".equals(o.getType()))
				.filter(o -> o.getContent() != null)
				.flatMap(o -> o.getContent().stream())
				.filter(c -> "output_text".equals(c.getType()))
				.map(ContentBlock::getText)
				.filter(t -> t != null && !t.isBlank())
				.findFirst().orElse(null);
	}

	// =========================================================================
	// Nested types
	// =========================================================================

	/**
	 * A single item in the output array. type = "reasoning" → reasoning block
	 * (summary may be empty). type = "message" → assistant message with content
	 * blocks.
	 */
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class OutputItem {

		@JsonProperty("id")
		private String id;

		/** "reasoning" | "message" */
		@JsonProperty("type")
		private String type;

		/** "completed" | "in_progress" (present on message items) */
		@JsonProperty("status")
		private String status;

		/** "assistant" (present on message items) */
		@JsonProperty("role")
		private String role;

		/** Content blocks (present on message items) */
		@JsonProperty("content")
		private List<ContentBlock> content;

		/** Reasoning summary blocks (present on reasoning items, may be empty) */
		@JsonProperty("summary")
		private List<ReasoningSummary> summary;
	}

	/**
	 * A single summary block inside a reasoning output item.
	 * type = "summary_text" carries the reasoning summary string.
	 */
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ReasoningSummary {

		/** "summary_text" */
		@JsonProperty("type")
		private String type;

		/** The reasoning summary text. */
		@JsonProperty("text")
		private String text;
	}

	/**
	 * A single content block within an output message. type = "output_text" carries
	 * the actual reply text.
	 */
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ContentBlock {

		/** "output_text" */
		@JsonProperty("type")
		private String type;

		/** The assistant's reply text. */
		@JsonProperty("text")
		private String text;

		/** Text annotations (citations, links, etc.). */
		@JsonProperty("annotations")
		private List<Object> annotations;

		/** Per-token log probabilities (empty list if top_logprobs = 0). */
		@JsonProperty("logprobs")
		private List<Object> logprobs;
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Billing {

		/** "openai" | "user" */
		@JsonProperty("payer")
		private String payer;
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class IncompleteDetails {

		/** Reason the response did not complete. */
		@JsonProperty("reason")
		private String reason;
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ApiError {

		@JsonProperty("code")
		private String code;

		@JsonProperty("message")
		private String message;
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TextConfig {

		@JsonProperty("format")
		private TextFormat format;

		/** "low" | "medium" | "high" */
		@JsonProperty("verbosity")
		private String verbosity;

		@Getter
		@Setter
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class TextFormat {

			/** "text" | "json_object" | "json_schema" */
			@JsonProperty("type")
			private String type;
		}
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ReasoningConfig {

		/** "low" | "medium" | "high" */
		@JsonProperty("effort")
		private String effort;

		/** "auto" | "concise" | "detailed" | null */
		@JsonProperty("summary")
		private String summary;
	}

	// -------------------------------------------------------------------------

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Usage {

		/** Tokens consumed by the prompt (input). */
		@JsonProperty("input_tokens")
		private Integer inputTokens;

		/** Tokens generated by the model (output). */
		@JsonProperty("output_tokens")
		private Integer outputTokens;

		/** inputTokens + outputTokens. */
		@JsonProperty("total_tokens")
		private Integer totalTokens;

		@JsonProperty("input_tokens_details")
		private InputTokensDetails inputTokensDetails;

		@JsonProperty("output_tokens_details")
		private OutputTokensDetails outputTokensDetails;

		@Getter
		@Setter
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class InputTokensDetails {

			/** Tokens served from the prompt cache (cost saving). */
			@JsonProperty("cached_tokens")
			private Integer cachedTokens;
		}

		@Getter
		@Setter
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class OutputTokensDetails {

			/** Tokens used internally by the model for chain-of-thought reasoning. */
			@JsonProperty("reasoning_tokens")
			private Integer reasoningTokens;
		}
	}
}