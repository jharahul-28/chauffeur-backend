package com.velvet.rest.services.booking.entity;

public interface BookingConstants {

	String TABLE_NAME = "bookings";

	enum Status {
		PENDING,
		REQUESTED,
		CONFIRMED,
		DRIVER_ON_THE_WAY,
		DRIVER_ARRIVED,
		IN_PROGRESS,
		COMPLETED,
		CANCELLED_BY_RIDER,
		CANCELLED_BY_DRIVER,
		NO_SHOW,
		DRIVER_UNAVAILABLE,
		EXPIRED
	}

	enum CustomerType {
		NORMAL,
		MEMBERSHIP,
		VIP
	}

	enum BookingSource {
		APP,
		WEB,
		WALK_IN
	}

	enum RideType {
		ONE_WAY,
		ROUND_TRIP
	}

	enum TripType {
		ONE_WAY,
		AIRPORT_TRANSFER,
		HOURLY_RENTALS
	}

	enum PaymentMode {
		CASH,
		CARD,
		UPI,
		ONLINE
	}

	enum PaymentStatus {
		PENDING,
		PARTIAL,
		PAID,
		REFUNDED
	}
}
