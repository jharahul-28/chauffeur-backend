package com.velvet.rest.services.util.beans;

import lombok.Data;

@Data
public class FlightRequest {
    private String flightCode; // Combined flight code e.g., "DL8599" (optional - will be parsed)
    private String flightNumber; // e.g., "8599" (can be auto-parsed from flightCode)
    private String airlineCode; // e.g., "DL" (can be auto-parsed from flightCode)
    private String date; // e.g., "20230513" (YYYYMMDD format)
}