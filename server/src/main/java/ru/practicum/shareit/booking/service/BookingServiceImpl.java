package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingQueryStatus;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectStatusException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pagination.PaginationParams;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking getBookingById(long userId, long bookingId) {
        log.info("Получен запрос на поиск бронирования с id: {}", bookingId);
        User requestUser = userService.getUserById(userId);
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()
                || (!requestUser.equals(bookingOptional.get().getBooker())
                && !requestUser.equals(bookingOptional.get().getItem().getOwner()))) {
            log.error("Ошибка при поиске бронирования с id: {}", bookingId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при поиске бронирования!");
        }
        return bookingOptional.get();
    }

    @Override
    @Transactional
    public Booking createBooking(long userId, BookingDto bookingDto) {
        log.info("Получен запрос на бронирование вещи {}", bookingDto.toString());
        Booking booking = BookingMapper.toBooking(bookingDto, userId);
        Item item = ItemMapper.toItem(itemService.getItemById(userId, booking.getItem().getId()));
        if (!item.getAvailable()) {
            log.error("Ошибка при бронировании вещи. Вещь не доступна, id вещи: " + item.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при бронировании вещи. " +
                    "Вещь не доступна, id вещи: " + item.getId());
        }
        User booker = userService.getUserById(booking.getBooker().getId());
        if (booker.equals(item.getOwner())) {
            log.error("Ошибка при бронировании вещи. Вещь не доступна для бронирования владельцем, id вещи: " + item.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при бронировании вещи. " +
                    "Вещь не доступна для бронирования владельцем, id вещи: " + item.getId());
        }
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Сохраняем бронирование вещи {}", booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Получен запрос на изменение статуса бронирования вещи {}", bookingId);
        Booking booking = getBookingById(userId, bookingId);
        User acceptor = userService.getUserById(userId);
        User owner = booking.getItem().getOwner();
        if (!acceptor.equals(owner)) {
            log.error("Ошибка при акцепте бронирования. Акцепт возможен только владельцем. owner = {}, acceptor = {}",
                    owner.getEmail(), acceptor.getEmail());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при акцепте бронирования. " +
                    "Акцепт возможен только владельцем. owner = " + owner.getEmail() + ", acceptor = " + acceptor.getEmail());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.error("Ошибка при акцепте бронирования. Акцепт возможен только из статуса WAITING. status = {}",
                    booking.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при акцепте бронирования. " +
                    "Акцепт возможен только из статуса WAITING. status = " + booking.getStatus());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsOfUser(Long userId, String state, Integer from, Integer size) {
        log.info("Получен запрос поиск бронирований. userId = {}, state = {}", userId, state);
        PageRequest pageRequest = PaginationParams.createPageRequest(from, size);
        switch (stringToBookingQueryStatus(state)) {
            case ALL:
                return bookingRepository.getBookingsByBookerOrderByStartDesc(userService.getUserById(userId), pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.getBookingsCurrent(userService.getUserById(userId), LocalDateTime.now(), pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.getBookingsByBookerAndEndBeforeOrderByStartDesc(userService.getUserById(userId), LocalDateTime.now(), pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.getBookingsByBookerAndStartAfterOrderByStartDesc(userService.getUserById(userId), LocalDateTime.now(), pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.getBookingsByBookerAndStatusOrderByStartDesc(userService.getUserById(userId), BookingStatus.WAITING, pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.getBookingsByBookerAndStatusOrderByStartDesc(userService.getUserById(userId), BookingStatus.REJECTED, pageRequest)
                        .stream()
                        .collect(Collectors.toList());
            default:
                log.error("Ошибка при поиске бронирований. Неизвестный статус: status = {}", state);
                throw new IncorrectStatusException(state);
        }
    }

    @Override
    public List<Booking> getBookingsOfOwner(Long userId, String state, Integer from, Integer size) {
        log.info("Получен запрос поиск бронирований владельца вещей. userId = {}, state = {}", userId, state);
        PageRequest pageRequest = PaginationParams.createPageRequest(from, size);
        userService.getUserById(userId);
        switch (stringToBookingQueryStatus(state)) {
            case ALL:
                return bookingRepository.getAllBookingsByOwner(userId, pageRequest).stream().collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.getAllBookingsByOwnerCurrent(userId, LocalDateTime.now(), pageRequest).stream().collect(Collectors.toList());
            case PAST:
                return bookingRepository.getAllBookingsByOwnerInPast(userId, LocalDateTime.now(), pageRequest).stream().collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.getAllBookingsByOwnerInFuture(userId, LocalDateTime.now(), pageRequest).stream().collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.getAllBookingsByOwnerAndStatus(userId, state, pageRequest).stream().collect(Collectors.toList());
            default:
                log.error("Ошибка при поиске бронирований. Неизвестный статус: status = {}", state);
                throw new IncorrectStatusException(state);
        }
    }

    private BookingQueryStatus stringToBookingQueryStatus(String status) {
        try {
            return BookingQueryStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStatusException(status);
        }
    }
}
