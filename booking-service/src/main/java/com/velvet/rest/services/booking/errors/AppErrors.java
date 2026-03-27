package com.velvet.rest.services.booking.errors;

import com.velvet.rest.common.properties.Errors;

public class AppErrors extends Errors {

	public static final String BOOKING_NOT_FOUND = "booking_not_found";

	public static final String BOOKING_WAYPOINT_NOT_FOUND = "booking_waypoint_not_found";

	public static final String BOOKING_NOT_PENDING = "booking_not_pending";

	public static final String BOOKING_ALREADY_REQUESTED = "booking_already_requested";

	public static final String WAYPOINT_NOT_FOUND = "waypoint_not_found";

	public static final String WAYPOINT_REQUIRED = "waypoint_required";

	public static final String INVALID_WAYPOINT_ORDER = "invalid_waypoint_order";

}
