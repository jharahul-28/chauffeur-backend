package com.velvet.rest.services.booking.entity;

public interface BookingVehicleConstants {

	String TABLE_NAME = "booking_vehicles";

	enum VehicleAssignmentStatus {
		ASSIGNED,
		DRIVER_ON_THE_WAY,
		ARRIVED,
		COMPLETED,
		CANCELLED
	}
}
