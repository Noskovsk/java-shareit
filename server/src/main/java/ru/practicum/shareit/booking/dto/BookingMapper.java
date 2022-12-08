package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Long ownerId) {
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        User booker = new User();
        booker.setId(ownerId);
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item).booker(booker).build();
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto
                .builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
