package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemOwnerDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @PostMapping
    public ItemCreateDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemCreateDto itemCreateDto) {
        return itemService.addItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId,
                           @RequestBody ItemPatchDto itemPatchDto) {
        return itemService.updateItem(userId, itemId, itemPatchDto);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text,
                                  @RequestParam(required = false) Integer from,
                                  @RequestParam(required = false) Integer size) {
        return itemService.searchItems(text, from, size);
    }

    @GetMapping
    public List<ItemOwnerDto> getItemByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(required = false) Integer from,
                                              @RequestParam(required = false) Integer size) {
        System.out.println("fr" + from + ", sz" + size);
        return itemService.getItemByUserId(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody Comment comment) {
        return itemService.addComment(userId, itemId, comment);
    }
}
