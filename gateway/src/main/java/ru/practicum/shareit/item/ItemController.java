package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.PaginationValidator;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @PathVariable long itemId) {
        log.info("gateway: Ищем вещь с id {}", itemId);
        return itemClient.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("gateway: Создаем вещь {}", itemCreateDto);
        return itemClient.addItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody ItemPatchDto itemPatchDto) {
        return itemClient.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(required = false) Integer from,
                                              @RequestParam(required = false) Integer size) {
        from = PaginationValidator.validateFrom(from);
        size = PaginationValidator.validateSize(size);
        return itemClient.searchItems(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getItemByUserId(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        from = PaginationValidator.validateFrom(from);
        size = PaginationValidator.validateSize(size);
        return itemClient.getItemByUserId(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PositiveOrZero @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
