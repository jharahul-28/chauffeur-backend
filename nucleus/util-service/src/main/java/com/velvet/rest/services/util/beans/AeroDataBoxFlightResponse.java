package com.velvet.rest.services.util.beans;

import java.util.List;
import lombok.Data;

@Data
public class AeroDataBoxFlightResponse {
    private GreatCircleDistance greatCircleDistance;
    private Departure departure;
    private Arrival arrival;
    private String lastUpdatedUtc;
    private String number;
    private String status;
    private String codeshareStatus;
    private boolean isCargo;
    private Aircraft aircraft;
    private Airline airline;
    
    @Data
    public static class GreatCircleDistance {
        private double meter;
        private double km;
        private double mile;
        private double nm;
        private double feet;
    }
    
    @Data
    public static class Departure {
        private Airport airport;
        private ScheduledTime scheduledTime;
        private ScheduledTime predictedTime;
        private String terminal;
        private List<String> quality;
    }
    
    @Data
    public static class Arrival {
        private Airport airport;
        private ScheduledTime scheduledTime;
        private ScheduledTime predictedTime;
        private String terminal;
        private List<String> quality;
    }
    
    @Data
    public static class Airport {
        private String icao;
        private String iata;
        private String name;
        private String shortName;
        private String municipalityName;
        private Location location;
        private String countryCode;
        private String timeZone;
    }
    
    @Data
    public static class Location {
        private double lat;
        private double lon;
    }
    
    @Data
    public static class ScheduledTime {
        private String utc;
        private String local;
    }
    
    @Data
    public static class Aircraft {
        private String model;
    }
    
    @Data
    public static class Airline {
        private String name;
        private String iata;
        private String icao;
    }
}