package com.airline.facade;

import com.airline.web.dtos.BookingDto;
import java.util.List;

public interface BookingFacade {
    BookingDto createBooking(BookingDto bookingDto, Long userId);
    BookingDto getBookingById(Long id);
    List<BookingDto> getAllBookings();
    List<BookingDto> getBookingsByUserId(Long userId);
    void cancelBooking(Long id);
}