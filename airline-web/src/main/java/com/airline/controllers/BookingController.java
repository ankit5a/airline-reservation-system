package com.airline.controllers;

import com.airline.web.dtos.BookingDto;
import ninja.Context;
import ninja.Result;
import ninja.params.PathParam;

public interface BookingController {
    Result createBooking(Context context, BookingDto bookingDto);
    Result getBookingById(Context context, @PathParam("id") Long id);
    Result getAllBookings(Context context);
    Result getMyBookings(Context context);
    Result cancelBooking(Context context, @PathParam("id") Long id);
}