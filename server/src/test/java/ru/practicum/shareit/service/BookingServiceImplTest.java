package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final EntityManager manager;

    protected List<User> createTestUserIntoDb(Integer count) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            userList.add(new User());
            userList.get(i).setName("user" + (i + 1));
            userList.get(i).setEmail("email" + (i + 1) + "@email.com");
            manager.persist(userList.get(i));
        }
        manager.flush();
        return userList;
    }

    protected Item createItem(User user, boolean available) {
        Item item = new Item();
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(available);
        item.setOwner(user);
        manager.persist(item);
        manager.flush();
        return item;
    }


    @Test
    void shouldCreateBooking() {
        List<User> userList = createTestUserIntoDb(2);
        User userOwner = userList.get(0);
        User userBooker = userList.get(1);
        Item item = createItem(userOwner, true);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(item.getId())
                .start(localDateTimeStart)
                .end(localDateTimeEnd)
                .bookerId(userBooker.getId()).build();
        Booking booking = bookingService.createBooking(userBooker.getId(), bookingDto);

        assertEquals(localDateTimeStart, booking.getStart(),
                "Время начала бронирования не совпадает");
        assertEquals(localDateTimeEnd, booking.getEnd(),
                "Время окончания бронирования не совпадает");
        assertEquals(BookingStatus.WAITING, booking.getStatus(), "Не совпадает статус у бронирования");
    }

    @Test
    void shouldNotGetBookingById() {
        List<User> userList = createTestUserIntoDb(1);
        User user = userList.get(0);
        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> bookingService.getBookingById(user.getId(), 999L));
        assertTrue(throwable.getMessage().contains("404"));
    }

    @Test
    void shouldGetBookingById() {
        List<User> userList = createTestUserIntoDb(2);
        User userOwner = userList.get(0);
        User userBooker = userList.get(1);
        Item item = createItem(userOwner, true);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(item.getId())
                .start(localDateTimeStart)
                .end(localDateTimeEnd)
                .bookerId(userBooker.getId()).build();
        Booking booking = bookingService.createBooking(userBooker.getId(), bookingDto);

        Booking bookingGet = bookingService.getBookingById(userBooker.getId(), booking.getId());
        assertEquals(booking.getId(), bookingGet.getId(),
                "Id бронирования не совпадает");
        assertEquals(localDateTimeStart, bookingGet.getStart(),
                "Время начала бронирования не совпадает");
        assertEquals(localDateTimeEnd, bookingGet.getEnd(),
                "Время окончания бронирования не совпадает");
        assertEquals(BookingStatus.WAITING, bookingGet.getStatus(), "Не совпадает статус у бронирования");
    }

    @Test
    void shouldUpdateBooking() {
        List<User> userList = createTestUserIntoDb(2);
        User userOwner = userList.get(0);
        User userBooker = userList.get(1);
        Item item = createItem(userOwner, true);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(item.getId())
                .start(localDateTimeStart)
                .end(localDateTimeEnd)
                .bookerId(userBooker.getId()).build();
        Booking booking = bookingService.createBooking(userBooker.getId(), bookingDto);

        Booking bookingUpd = bookingService.updateBooking(userOwner.getId(), booking.getId(), true);
        assertEquals(booking.getId(), bookingUpd.getId(),
                "Id бронирования не совпадает");
        assertEquals(localDateTimeStart, bookingUpd.getStart(),
                "Время начала бронирования не совпадает");
        assertEquals(localDateTimeEnd, bookingUpd.getEnd(),
                "Время окончания бронирования не совпадает");
        assertEquals(BookingStatus.APPROVED, bookingUpd.getStatus(), "Не совпадает статус у бронирования");
    }

    @Test
    void shouldNotUpdateBookingByBooker() {
        List<User> userList = createTestUserIntoDb(2);
        User userOwner = userList.get(0);
        User userBooker = userList.get(1);
        Item item = createItem(userOwner, true);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(item.getId())
                .start(localDateTimeStart)
                .end(localDateTimeEnd)
                .bookerId(userBooker.getId()).build();
        Booking booking = bookingService.createBooking(userBooker.getId(), bookingDto);

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> bookingService.updateBooking(userBooker.getId(), booking.getId(), true));
        assertTrue(throwable.getMessage().contains("404"));
    }

    @Test
    void shouldNotUpdateBookingWithWrongStatus() {
        List<User> userList = createTestUserIntoDb(2);
        User userOwner = userList.get(0);
        User userBooker = userList.get(1);
        Item item = createItem(userOwner, true);
        LocalDateTime localDateTimeStart = LocalDateTime.now().plusDays(1);
        LocalDateTime localDateTimeEnd = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(item.getId())
                .start(localDateTimeStart)
                .end(localDateTimeEnd)
                .bookerId(userBooker.getId()).build();
        Booking booking = bookingService.createBooking(userBooker.getId(), bookingDto);
        bookingService.updateBooking(userOwner.getId(), booking.getId(), false);

        Throwable throwable = assertThrows(ResponseStatusException.class,
                () -> bookingService.updateBooking(userOwner.getId(), booking.getId(), true));
        assertTrue(throwable.getMessage().contains("400"));
    }
}
