package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IncorrectStatusException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class BookingServiceImplMockTest {
    private ItemService mockItemService;
    private BookingRepository mockBookingRepository;
    private UserService mockUserService;
    private BookingService bookingService;
    private Throwable throwable;

    @BeforeEach
    void prepareData() {
        mockItemService = Mockito.mock(ItemServiceImpl.class);
        mockBookingRepository = Mockito.mock((BookingRepository.class));
        mockUserService = Mockito.mock(UserServiceImpl.class);
        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemService, mockUserService);
    }

    @Test
    void shouldThrowExceptionWhenItemNotAvailable() {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setAvailable(false);

        Mockito.when(mockItemService.getItemById(anyLong(), eq(Long.valueOf(1)))).thenReturn(itemOwnerDto);

        BookingDto bookingDto = BookingDto.builder().itemId(1L).build();
        throwable = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(1L, bookingDto));
        assertTrue(throwable.getMessage().contains("Ошибка при бронировании вещи. Вещь не доступна, id вещи"));
    }

    @Test
    void shouldReturnAllBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwner(any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "ALL", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwner(any(), any());
    }

    @Test
    void shouldReturnAllBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsByBookerOrderByStartDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "ALL", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsByBookerOrderByStartDesc(any(), any());
    }

    @Test
    void shouldReturnCurrentBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwnerCurrent(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "CURRENT", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwnerCurrent(any(), any(), any());
    }

    @Test
    void shouldReturnCurrentBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsCurrent(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "CURRENT", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsCurrent(any(), any(), any());
    }

    @Test
    void shouldReturnPastBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwnerInPast(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "PAST", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwnerInPast(any(), any(), any());
    }

    @Test
    void shouldReturnPastBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsByBookerAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "PAST", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsByBookerAndEndBeforeOrderByStartDesc(any(), any(), any());
    }

    @Test
    void shouldReturnFutureBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwnerInFuture(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "FUTURE", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwnerInFuture(any(), any(), any());
    }

    @Test
    void shouldReturnFutureBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsByBookerAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "FUTURE", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsByBookerAndStartAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void shouldReturnWaitingBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwnerAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "WAITING", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwnerAndStatus(any(), any(), any());
    }

    @Test
    void shouldReturnWaitingBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "WAITING", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsByBookerAndStatusOrderByStartDesc(any(), any(), any());
    }

    @Test
    void shouldReturnRejectedBookingsOfOwner() {
        Mockito.when(mockBookingRepository
                        .getAllBookingsByOwnerAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfOwner(1L, "REJECTED", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getAllBookingsByOwnerAndStatus(any(), any(), any());
    }

    @Test
    void shouldReturnRejectedBookingsOfUser() {
        Mockito.when(mockBookingRepository
                        .getBookingsByBookerAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(new Booking())));
        List<Booking> bookingList = bookingService.getBookingsOfUser(1L, "REJECTED", 0, 10);
        assertEquals(1, bookingList.size(),
                "Размерность не совпадает");
        Mockito.verify(mockBookingRepository,
                Mockito.times(1)).getBookingsByBookerAndStatusOrderByStartDesc(any(), any(), any());
    }

    @Test
    void shouldNotReturnErrorStateBookingsOfUser() {
        throwable = assertThrows(IncorrectStatusException.class, () -> bookingService.getBookingsOfUser(1L, "UNSUPPORTED", 0, 10));
        assertTrue(throwable.getMessage().contains("UNSUPPORTED"));
    }

    @Test
    void shouldNotReturnErrorStateBookingsOfOwner() {
        throwable = assertThrows(IncorrectStatusException.class, () -> bookingService.getBookingsOfOwner(1L, "UNSUPPORTED", 0, 10));
        assertTrue(throwable.getMessage().contains("UNSUPPORTED"));
    }

    @Test
    void shouldThrowExceptionWhenBookedByOwner() {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setAvailable(true);
        User booker = new User();
        booker.setId(1L);
        itemOwnerDto.setOwner(booker);

        Mockito.when(mockItemService.getItemById(anyLong(), eq(1L))).thenReturn(itemOwnerDto);
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(booker);

        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .bookerId(1L).build();
        throwable = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(1L, bookingDto));
        assertTrue(throwable.getMessage().contains("Ошибка при бронировании вещи. Вещь не доступна для бронирования владельцем, id вещи"));
    }

    @Test
    void shouldCorrectMapBookingDto() {
        ItemOwnerDto itemOwnerDto = new ItemOwnerDto();
        itemOwnerDto.setAvailable(true);
        User booker = new User();
        booker.setId(1L);
        itemOwnerDto.setOwner(booker);

        Mockito.when(mockItemService.getItemById(anyLong(), eq(1L))).thenReturn(itemOwnerDto);
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(booker);

        BookingDto bookingDto = BookingDto
                .builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .bookerId(1L).build();
        Booking booking = BookingMapper.toBooking(bookingDto, booker.getId());
        assertEquals(booking.getStart(), bookingDto.getStart(), "Время старат не совпадает.");
        assertEquals(booking.getEnd(), bookingDto.getEnd(), "Время окончания не совпадает.");
        throwable = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(1L, bookingDto));
        assertTrue(throwable.getMessage().contains("Ошибка при бронировании вещи. Вещь не доступна для бронирования владельцем, id вещи"));
    }

    @Test
    void shouldThrowExceptionWhenUnknownStateInBookingsOfUser() {
        throwable = assertThrows(IncorrectStatusException.class,
                () -> bookingService.getBookingsOfUser(1L, "UNKNOWN", 0, 10));
        assertTrue(throwable.getMessage().contains("UNKNOWN"));
    }

    @Test
    void shouldThrowExceptionWhenUnknownStateInBookingsOfOwn() {
        throwable = assertThrows(IncorrectStatusException.class,
                () -> bookingService.getBookingsOfOwner(1L, "UNKNOWN_OWN", 0, 10));
        assertTrue(throwable.getMessage().contains("UNKNOWN_OWN"));
    }
}
