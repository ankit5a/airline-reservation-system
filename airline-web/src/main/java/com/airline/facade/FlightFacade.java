package com.airline.facade;

import com.airline.web.dtos.FlightDto;
import java.util.List;

public interface FlightFacade {
    List<FlightDto> getAllFlights();
    FlightDto getFlightById(Long id);
    List<FlightDto> searchFlights(String origin, String destination, String date);
    FlightDto createFlight(FlightDto flightDto);
    FlightDto updateFlight(Long id, FlightDto flightDto);
    void deleteFlight(Long id);
}