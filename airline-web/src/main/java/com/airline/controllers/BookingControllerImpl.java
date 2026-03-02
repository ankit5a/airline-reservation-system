package com.airline.controllers;

import com.airline.exceptions.BookingNotFoundException;
import com.airline.exceptions.FlightNotFoundException;
import com.airline.exceptions.InvalidRequestException;
import com.airline.facade.BookingFacade;
import com.airline.filters.AclFilter;
import com.airline.filters.CorsFilter;
import com.airline.web.dtos.ApiResponseDto;
import com.airline.web.dtos.BookingDto;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BookingControllerImpl implements BookingController {

    @Inject
    private BookingFacade bookingFacade;

    @Override
    @FilterWith(AclFilter.class)
    public Result createBooking(Context context, BookingDto bookingDto) {
        try {
            String userId = context.getSession().get("userId");
            BookingDto created = bookingFacade.createBooking(bookingDto, Long.valueOf(userId));
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Booking created", created))
            );
        } catch (FlightNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        } catch (InvalidRequestException e) {
            return CorsFilter.addCorsHeaders(
                    Results.badRequest().json().render(ApiResponseDto.error(e.getMessage()))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error("Booking failed: " + e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result getBookingById(Context context, @PathParam("id") Long id) {
        try {
            BookingDto booking = bookingFacade.getBookingById(id);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Booking found", booking))
            );
        } catch (BookingNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result getAllBookings(Context context) {
        try {
            List<BookingDto> bookings = bookingFacade.getAllBookings();
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("All bookings", bookings))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result getMyBookings(Context context) {
        try {
            String userId = context.getSession().get("userId");
            List<BookingDto> bookings = bookingFacade.getBookingsByUserId(Long.valueOf(userId));
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Your bookings", bookings))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    @FilterWith(AclFilter.class)
    public Result cancelBooking(Context context, @PathParam("id") Long id) {
        try {
            bookingFacade.cancelBooking(id);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Booking cancelled", null))
            );
        } catch (BookingNotFoundException e) {
            return CorsFilter.addCorsHeaders(
                    Results.notFound().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }
}