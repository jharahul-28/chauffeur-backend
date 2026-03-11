package com.velvet.rest.services.fleet.entity;

/**
 * Column field mapping as constants for table Vehicle
 */
public interface VehicleConstants {

	String VEHICLE_TABLE_NAME = "vehicles";

	enum FuelType {
		PETROL, DIESEL, ELECTRIC, HYBRID
	}

	enum Transmission {
		AUTOMATIC, MANUAL
	}
}
