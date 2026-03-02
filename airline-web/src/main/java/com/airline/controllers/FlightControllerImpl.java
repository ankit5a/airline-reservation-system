package com.airline.controllers;

import com.airline.exceptions.FlightNotFoundException;
import com.airline.exceptions.InvalidRequestException;
import com.airline.facade.FlightFacade;
import com.airline.filters.AclFilter;
import com.airline.filters.CorsFilter;
import com.airline.web.dtos.ApiResponseDto;
import com.airline.web.dtos.FlightDto;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;
import ninja.params.Param;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class FlightControllerImpl implements FlightController {

    @Inject
    private FlightFacade flightFacade;

    @Override
    public Result getAllFlights(Context context) {
        try {
            List<FlightDto> flights = flightFacade.getAllFlights();
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Flights retrieved", flights))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    public Result getFlightById(Context context, @PathParam("id") Long id) {
        try {
            FlightDto flight = flightFacade.getFlightById(id);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Flight found", flight))
            );
        } catch (FlightNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    public Result searchFlights(Context context,
                                @Param("origin") String origin,
                                @Param("destination") String destination,
                                @Param("date") String date) {
        try {
            List<FlightDto> flights = flightFacade.searchFlights(origin, destination, date);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Search results", flights))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result createFlight(Context context, FlightDto flightDto) {
        try {
            FlightDto created = flightFacade.createFlight(flightDto);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Flight created", created))
            );
        } catch (InvalidRequestException e) {
            return CorsFilter.addCorsHeaders(
                    Results.badRequest().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result updateFlight(Context context, @PathParam("id") Long id, FlightDto flightDto) {
        try {
            FlightDto updated = flightFacade.updateFlight(id, flightDto);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Flight updated", updated))
            );
        } catch (FlightNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result deleteFlight(Context context, @PathParam("id") Long id) {
        try {
            flightFacade.deleteFlight(id);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Flight deleted", null))
            );
        } catch (FlightNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }
}