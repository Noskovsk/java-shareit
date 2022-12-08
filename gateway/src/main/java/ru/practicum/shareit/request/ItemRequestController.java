package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PaginationValidator;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("gateway: Создаем запрос {}", itemRequestDto.getDescription());
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("gateway: Ищем запрос с id {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("gateway: Ищем запросы с id {}", userId);
        return itemRequestClient.getMyItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestFromUsers(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(required = false) Integer from,
                                                          @RequestParam(required = false) Integer size) {
        from = PaginationValidator.validateFrom(from);
        size = PaginationValidator.validateSize(size);
        log.info("gateway:  Ищем все запросы с пользователя id {}", userId);
        return itemRequestClient.getItemRequestFromUsers(userId, from, size);
    }
}
