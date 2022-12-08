package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking getBookingById(long userId, long bookingId);

    Booking createBooking(long userId, BookingDto bookingDto);

    Booking updateBooking(Long userId, Long bookingId, Boolean approved);

    List<Booking> getBookingsOfUser(Long userId, String state, Integer from, Integer size);

    List<Booking> getBookingsOfOwner(Long userId, String state, Integer from, Integer size);
}
