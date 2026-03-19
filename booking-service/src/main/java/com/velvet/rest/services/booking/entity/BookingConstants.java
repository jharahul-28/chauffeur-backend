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

	enum BeverageType {
		NONE,
		WATER,
		JUICE,
		SOFT_DRINK,
		COFFEE,
		TEA
	}
}
