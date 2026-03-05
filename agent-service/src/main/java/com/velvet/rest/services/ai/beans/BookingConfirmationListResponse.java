package com.velvet.rest.services.ai.beans;

import lombok.Data;
import java.util.List;

@Data
public class BookingConfirmationListResponse {
    
    private List<BookingConfirmationResponse> bookings;
    
    private Integer count;
    
    private String notes;
}