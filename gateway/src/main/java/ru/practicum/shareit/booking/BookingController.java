package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.PaginationValidator;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.IncorrectStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(required = false) Integer from,
                                              @Positive @RequestParam(required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectStatusException(stateParam));
        log.info("gateway: Получить бронирования со статусом бронирование {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        from = PaginationValidator.validateFrom(from);
        size = PaginationValidator.validateSize(size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                     @RequestParam(required = false) Integer from,
                                                     @RequestParam(required = false) Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IncorrectStatusException(stateParam));
        log.info("gateway: Получить бронирования как владельца вещи {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        from = PaginationValidator.validateFrom(from);
        size = PaginationValidator.validateSize(size);
        return bookingClient.getBookingsOfOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("gateway: Создаем бронирование {}, userId={}", requestDto, userId);
        if (requestDto.getStart().isAfter(requestDto.getEnd())) {
            log.error("Ошибка при бронировании вещи. Дата завершения бронирования: " + requestDto.getEnd() +
                    ", раньше даты начала: " + requestDto.getStart());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при бронировании вещи. " +
                    "Дата завершения бронирования: " + requestDto.getEnd() + ", раньше даты начала: " + requestDto.getStart());
        }
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получить бронирование {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Обновить бронирование {}, userId={}", bookingId, userId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

}
