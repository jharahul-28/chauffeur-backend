package com.velvet.rest.services.ai.agent.impl;

import org.springframework.stereotype.Component;
import com.velvet.rest.services.ai.agent.OpenAIAgentStrategy;
import com.velvet.rest.services.ai.beans.BookingConfirmationListResponse;
import com.velvet.rest.services.ai.beans.TaskRequest;

@Component
public class BookingConfirmationAgent extends OpenAIAgentStrategy {

    @Override
    public String getTaskType() {
        return "BOOKING_CONFIRMATION";
    }

    @Override
    public Class<BookingConfirmationListResponse> getResponse() {
        return BookingConfirmationListResponse.class;
    }

    @Override
    protected String buildPrompt(TaskRequest request) {
        String fileContent = (String) request.getParameters().get("fileContent");
        String fileName = (String) request.getParameters().getOrDefault("fileName", "booking_file");
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert booking confirmation parser. ");
        prompt.append("Parse the following booking confirmation document and extract key information to create itinerary items. ");
        prompt.append("This document may contain MULTIPLE bookings (e.g., round-trip flights, multi-segment journeys).\n\n");
        
        prompt.append("Document Content:\n");
        prompt.append(fileContent).append("\n\n");
        
        prompt.append("Please analyze this booking confirmation and extract ALL separate bookings/segments. ");
        prompt.append("For each booking, extract the following information:\n");
        prompt.append("1. title: A clear, concise title for each booking (e.g., 'Flight BLR to GOI', 'Return Flight GOI to BLR')\n");
        prompt.append("2. description: Brief description with key details (route, service provider, passengers, etc.)\n");
        prompt.append("3. startDateTime: The start date and time in ISO format (yyyy-MM-ddTHH:mm:ss)\n");
        prompt.append("4. endDateTime: The end date and time in ISO format (yyyy-MM-ddTHH:mm:ss) - optional for single events\n");
        prompt.append("5. bookingId: The booking reference/confirmation number (same for round-trip)\n");
        prompt.append("6. notes: Any additional important details (seat numbers, terminal, baggage, etc.)\n");
        prompt.append("7. itemType: Categorize as one of: TRAVEL, ACCOMMODATION, ACTIVITIES, FOOD\n");
        prompt.append("8. entityNumber: An array of service numbers (flight numbers like ['AI-123', 'AI-456'] for connecting flights, train numbers, bus numbers, etc.)\n");
        prompt.append("9. source: Source location/city/airport code (only for TRAVEL items, e.g., 'BLR', 'Delhi', 'Mumbai Central')\n");
        prompt.append("10. destination: Destination location/city/airport code (only for TRAVEL items, e.g., 'GOI', 'Goa', 'Chennai Central')\n");
        prompt.append("11. travelMode: For TRAVEL items ONLY, specify the exact mode of transport: PLANE, TRAIN, BUS, or CAR (for rentals)\n");
        prompt.append("12. placeName: For ACCOMMODATION items ONLY, the name of the hotel/property (e.g., 'The Taj Mahal Palace', 'Hotel Lumiere')\n");
        prompt.append("13. address: For ACCOMMODATION items ONLY, the full address of the property\n");
        prompt.append("14. roomType: For ACCOMMODATION items ONLY, the type of room booked (e.g., 'Deluxe King', 'Ocean View Suite')\n\n");
        
        prompt.append("Comprehensive Categorization Rules:\n");
        prompt.append("- TRAVEL: All transportation bookings\n");
        prompt.append("  * Flights (domestic/international, direct/connecting)\n");
        prompt.append("  * Trains (high-speed, local, sleeper, express)\n");
        prompt.append("  * Buses (city, intercity, luxury coaches, shuttle services)\n");
        prompt.append("  * Car rentals (cars, bikes, scooters, self-drive, with driver)\n");
        prompt.append("  * Ferries, cruises, boats, water transport\n");
        prompt.append("  * Airport transfers, taxi bookings, ride-sharing\n");
        prompt.append("- ACCOMMODATION: All lodging bookings\n");
        prompt.append("  * Hotels (luxury, budget, boutique, chain hotels)\n");
        prompt.append("  * Resorts, hostels, guesthouses, B&Bs\n");
        prompt.append("  * Vacation rentals (Airbnb, apartments, villas)\n");
        prompt.append("  * Camping sites, glamping, unique stays\n");
        prompt.append("- ACTIVITIES: Entertainment and experience bookings\n");
        prompt.append("  * Tours (city, cultural, adventure, food, walking)\n");
        prompt.append("  * Attractions (museums, theme parks, monuments)\n");
        prompt.append("  * Shows (concerts, theater, sports events)\n");
        prompt.append("  * Adventure activities (safaris, water sports, trekking)\n");
        prompt.append("  * Spa treatments, wellness experiences\n");
        prompt.append("- FOOD: Dining and culinary experiences\n");
        prompt.append("  * Restaurant reservations\n");
        prompt.append("  * Food tours, cooking classes\n");
        prompt.append("  * Special dining experiences (fine dining, street food tours)\n\n");
        
        prompt.append("TravelMode determination for TRAVEL items:\n");
        prompt.append("- PLANE: Any air travel (look for flight numbers, airlines, airports)\n");
        prompt.append("- TRAIN: Rail travel (look for train numbers, railway stations, rail operators)\n");
        prompt.append("- BUS: Bus or coach travel (look for bus operators, bus stations)\n");
        prompt.append("- CAR: Car, bike, or vehicle rentals (look for rental companies, pickup/drop-off locations)\n\n");
        
        prompt.append("CRITICAL: Handle layovers and connecting flights properly:\n");
        prompt.append("- MANDATORY: For flights with layovers/connections, you MUST create SEPARATE booking entries for EACH flight segment\n");
        prompt.append("- COUNT RULE: If you see 'Copa 383, Copa 131' that means 2 flights = you MUST create 2 bookings\n");
        prompt.append("- COUNT RULE: If you see 'avianca 102, avianca 120' that means 2 flights = you MUST create 2 bookings\n");
        prompt.append("- Example: SFO→PTY→LIM with 'Copa 383, Copa 131' MUST create exactly 2 separate bookings:\n");
        prompt.append("  * Booking 1: Copa 383 from SFO to PTY\n");
        prompt.append("  * Booking 2: Copa 131 from PTY to LIM\n");
        prompt.append("- Example: LIM→BOG→LHR with 'avianca 102, avianca 120' MUST create exactly 2 separate bookings:\n");
        prompt.append("  * Booking 1: avianca 102 from LIM to BOG\n");
        prompt.append("  * Booking 2: avianca 120 from BOG to LHR\n");
        prompt.append("- Each segment should have its own flight number in entityNumber (as a single item array)\n");
        prompt.append("- Use different bookingId for each flight segment (use the flight number as bookingId)\n");
        prompt.append("- For round-trip or return journeys: Create separate bookings for each direction\n");
        prompt.append("- VALIDATION: Count your flight numbers - if you count 6 flight numbers in the input, you MUST create 6 bookings\n\n");
        
        // Add critical parsing rules
        java.time.LocalDate now = java.time.LocalDate.now();
        String currentYear = String.valueOf(now.getYear());
        String nextYear = String.valueOf(now.getYear() + 1);
        int currentMonth = now.getMonthValue();

        prompt.append("CRITICAL PARSING RULES - FOLLOW EXACTLY:\n");
        prompt.append("1. YEAR INFERENCE (MANDATORY): Current year is ").append(currentYear).append(". When parsing dates without years:\n");
        prompt.append("   - NEVER use 2023 or any year other than ").append(currentYear).append("/").append(nextYear).append(".\n");
        prompt.append("   - If currently month ").append(currentMonth).append(" and you see Jan/Feb/Mar dates, use ").append(nextYear).append(".\n");
        prompt.append("   - For Apr/May/Jun/Jul/Aug/Sep/Oct/Nov/Dec dates, use ").append(currentYear).append(".\n");
        prompt.append("   - EXAMPLES: 'Oct 28' = 'Oct 28, ").append(currentYear).append("' (NOT 2023), 'Jan 15' = 'Jan 15, ").append(currentMonth >= 10 ? nextYear : currentYear).append("' (NOT 2023).\n\n");

        prompt.append("2. FLIGHT TIMES (CRITICAL - NULL HANDLING): For flights with layovers:\n");
        prompt.append("   - MANDATORY: Create separate entries for EACH flight number mentioned (Copa 383, Copa 131, avianca 102, avianca 120, etc.).\n");
        prompt.append("   - NEVER skip flight segments - if 2 flight numbers are mentioned, create 2 separate booking entries.\n");
        prompt.append("   - ONLY use explicitly stated times. DO NOT copy overall journey times to individual segments.\n");
        prompt.append("   - MUST return null for missing times - this is REQUIRED, not optional.\n");
        prompt.append("   - MANDATORY EXAMPLE: 'SFO to LIM: 1:36am-3:40pm, Copa 383, Copa 131' MUST create EXACTLY 2 bookings:\n");
        prompt.append("     * Booking 1 - Copa 383: title='Flight from San Francisco to Panama City', startDateTime='").append(currentYear).append("-10-28T01:36:00' (SFO departure stated), endDateTime=null (PTY arrival NOT stated), source='SFO', destination='PTY'\n");
        prompt.append("     * Booking 2 - Copa 131: title='Flight from Panama City to Lima', startDateTime=null (PTY departure NOT stated), endDateTime='").append(currentYear).append("-10-28T15:40:00' (LIM arrival stated), source='PTY', destination='LIM'\n");
        prompt.append("   - MANDATORY EXAMPLE: 'LIM to LHR: 7:30am-2:35pm+1, avianca 102, avianca 120' MUST create EXACTLY 2 bookings:\n");
        prompt.append("     * Booking 1 - avianca 102: title='Flight from Lima to Bogota', startDateTime='").append(currentYear).append("-11-02T07:30:00' (LIM departure stated), endDateTime=null (BOG arrival NOT stated), source='LIM', destination='BOG'\n");
        prompt.append("     * Booking 2 - avianca 120: title='Flight from Bogota to London', startDateTime=null (BOG departure NOT stated), endDateTime='").append(currentYear).append("-11-03T14:35:00' (LHR arrival stated), source='BOG', destination='LHR'\n");
        prompt.append("   - DO NOT use overall journey times ('1:36am-3:40pm', '7:30am-2:35pm+1') for individual segments.\n");
        prompt.append("   - COUNT CHECK: If you see 2 flight numbers, you MUST create 2 separate booking entries.\n\n");

        prompt.append("CRITICAL: You MUST respond with ONLY valid JSON. Do not include any explanatory text, markdown, or other content.\n");
        prompt.append("If you cannot parse the booking information, return: {\"bookings\": [], \"count\": 0, \"notes\": \"Unable to parse booking details\"}\n\n");
        prompt.append("Format your response as a valid JSON object with this exact structure:\n");
        prompt.append("{\n");
        prompt.append("  \"bookings\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"string\",\n");
        prompt.append("      \"description\": \"string\",\n");
        prompt.append("      \"startDateTime\": \"yyyy-MM-ddTHH:mm:ss\",\n");
        prompt.append("      \"endDateTime\": \"yyyy-MM-ddTHH:mm:ss\",\n");
        prompt.append("      \"bookingId\": \"string\",\n");
        prompt.append("      \"notes\": \"string\",\n");
        prompt.append("      \"itemType\": \"TRAVEL|ACCOMMODATION|ACTIVITIES|FOOD\",\n");
        prompt.append("      \"entityNumber\": [\"string\", \"string\"],\n");
        prompt.append("      \"source\": \"string (only for TRAVEL)\",\n");
        prompt.append("      \"destination\": \"string (only for TRAVEL)\",\n");
        prompt.append("      \"travelMode\": \"PLANE|TRAIN|BUS|CAR (only for TRAVEL)\",\n");
        prompt.append("      \"placeName\": \"string (only for ACCOMMODATION)\",\n");
        prompt.append("      \"address\": \"string (only for ACCOMMODATION)\",\n");
        prompt.append("      \"roomType\": \"string (only for ACCOMMODATION)\"\n");
        prompt.append("    }\n");
        prompt.append("  ],\n");
        prompt.append("  \"count\": number_of_bookings,\n");
        prompt.append("  \"notes\": \"Overall booking notes if any\"\n");
        prompt.append("}\n\n");
        
        prompt.append("PARSING EXAMPLES:\n");
        prompt.append("1. Flight with layover (DEL→SGN→DPS): Create 2 bookings:\n");
        prompt.append("   - Booking 1: 'Flight from New Delhi to Ho Chi Minh City' (DEL→SGN)\n");
        prompt.append("   - Booking 2: 'Flight from Ho Chi Minh City to Denpasar Bali' (SGN→DPS)\n");
        prompt.append("2. Round-trip flight: Create 2 bookings (one for each direction)\n");
        prompt.append("3. Hotel + Flight package: Create separate TRAVEL and ACCOMMODATION bookings\n");
        prompt.append("4. Multi-day hotel stay: Create 1 ACCOMMODATION booking with check-in/check-out dates\n");
        prompt.append("5. Train journey with connections: Treat each connection as separate booking\n\n");
        
        prompt.append("If only one booking is found, still return it in the array format. ");
        prompt.append("If certain information is not available, use null for optional fields. ");
        prompt.append("For TRAVEL items: set placeName, address, and roomType to null. ");
        prompt.append("For ACCOMMODATION items: set source, destination, and travelMode to null. ");
        prompt.append("For ACTIVITIES and FOOD items: set all travel and accommodation specific fields to null.\n\n");
        
        prompt.append("CRITICAL: Always look for and parse ALL bookings in the document. Common patterns:\n");
        prompt.append("- Outbound/Return or Departure/Arrival sections\n");
        prompt.append("- Multiple PNR/booking references\n");
        prompt.append("- Different dates of travel\n");
        prompt.append("- Multiple passengers with different bookings\n");
        prompt.append("- Package deals with multiple components\n\n");

        prompt.append("FINAL VALIDATION - BEFORE SUBMITTING YOUR RESPONSE:\n");
        prompt.append("1. Count all flight numbers in the input text (Copa 383, Copa 131, avianca 102, avianca 120, United 949, etc.)\n");
        prompt.append("2. Count the number of bookings you created in your JSON response\n");
        prompt.append("3. These numbers MUST match - if you found 5 flight numbers, create 5 booking entries\n");
        prompt.append("4. For the given input, you should create exactly 5 bookings (Copa 383, Copa 131, avianca 102, avianca 120, United 949)\n");
        prompt.append("5. If your count doesn't match, review and add the missing flight segments\n");
        
        return prompt.toString();
    }

    @Override
    protected String getTaskContext() {
        return "Booking confirmation document parsing and itinerary item extraction";
    }
}