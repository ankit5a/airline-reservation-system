package com.airline.controllers;

import com.airline.web.dtos.FlightDto;
import ninja.Context;
import ninja.Result;
import ninja.params.PathParam;
import ninja.params.Param;

public interface FlightController {
    Result getAllFlights(Context context);
    Result getFlightById(Context context, @PathParam("id") Long id);
    Result searchFlights(Context context,
                         @Param("origin") String origin,
                         @Param("destination") String destination,
                         @Param("date") String date);
    Result createFlight(Context context, FlightDto flightDto);
    Result updateFlight(Context context, @PathParam("id") Long id, FlightDto flightDto);
    Result deleteFlight(Context context, @PathParam("id") Long id);
}