package com.velvet.rest.services.booking.entity;

public interface BookingWaypointConstants {

	String TABLE_NAME = "booking_waypoints";

	enum WaypointType {
		PICKUP,
		STOP,
		DROP
	}
}
