package com.velvet.rest.services.chat.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * Classifies the user's travel intent from their message using keyword matching.
 *
 * Priority order (most specific first):
 *   AIRPORT → PRICING → HOURLY → EVENT → CORPORATE → CITY → OTHER
 */
@Service
public class IntentService {

	public enum Intent {
		AIRPORT, PRICING, HOURLY, EVENT, CORPORATE, CITY, OTHER
	}

	// Keywords are matched against the lowercased user message.
	// Each entry is a list of keywords/phrases; any single match is sufficient.
	private static final Map<Intent, List<String>> INTENT_KEYWORDS = Map.of(

		Intent.AIRPORT, List.of(
			"airport", "terminal", "t1", "t2", "t3", "igi", "jewar",
			"flight", "arrive", "arrival", "depart", "departure",
			"land", "landed", "landing", "fly", "flying",
			"meet and greet", "placard"
		),

		Intent.PRICING, List.of(
			"price", "pricing", "cost", "rate", "how much", "charges",
			"discount", "quote", "quotation", "expensive", "cheap",
			"affordable", "fare", "tariff", "fee", "fees"
		),

		Intent.HOURLY, List.of(
			"hourly", "per hour", "by the hour",
			"4 hour", "6 hour", "full day", "half day", "flexible itinerary"
		),

		Intent.EVENT, List.of(
			"event", "wedding", "conference", "delegation",
			"ceremony", "multiple car", "fleet", "group booking", "convoy"
		),

		Intent.CORPORATE, List.of(
			"corporate", "b2b", "company", "invoice", "billing",
			"monthly", "bulk", "rate card", "account", "business travel"
		),

		Intent.CITY, List.of(
			"city ride", "city", "point to point", "meeting",
			"dinner", "office", "gurugram", "gurgaon", "noida",
			"delhi", "ncr", "ride", "pick", "drop"
		)
	);

	// Evaluation order — most specific first, broad last
	private static final List<Intent> PRIORITY = List.of(
		Intent.AIRPORT,
		Intent.PRICING,
		Intent.HOURLY,
		Intent.EVENT,
		Intent.CORPORATE,
		Intent.CITY
	);

	/**
	 * Returns the detected intent for the given user message.
	 * Falls back to {@link Intent#OTHER} if no keywords match.
	 */
	public Intent classify(String userMessage) {
		if (userMessage == null || userMessage.isBlank()) {
			return Intent.OTHER;
		}
		String lower = userMessage.toLowerCase();
		for (Intent intent : PRIORITY) {
			List<String> keywords = INTENT_KEYWORDS.get(intent);
			if (keywords != null && keywords.stream().anyMatch(lower::contains)) {
				return intent;
			}
		}
		return Intent.OTHER;
	}
}
