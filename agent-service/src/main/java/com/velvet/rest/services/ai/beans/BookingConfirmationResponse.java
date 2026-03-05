package com.velvet.rest.services.ai.beans;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;

@Data
public class BookingConfirmationResponse {
    
    private String title;
    
    private String description;
    
    @NotNull(message = "Start date time is required")
    private LocalDateTime startDateTime;
    
    private LocalDateTime endDateTime;
    
    private String bookingId;
    
    private String notes;
    
    @NotNull(message = "Item type is required")
    private String itemType;
    
    private List<String> entityNumber;
    
    private String source;
    
    private String destination;
    
    private String travelMode;
    
    // Accommodation specific fields
    private String placeName;
    
    private String address;
    
    private String roomType;
}