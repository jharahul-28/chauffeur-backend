package com.velvet.rest.services.util.beans;

import lombok.Data;

@Data
public class FlightResponse {
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String status;
    private String duration;
    private String flightNumber;
}