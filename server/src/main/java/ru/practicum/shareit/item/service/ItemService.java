package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemService {
    ItemOwnerDto getItemById(long userId, long itemId);

    ItemCreateDto addItem(Long userId, ItemCreateDto item);

    Item updateItem(Long userId, Long itemId, ItemPatchDto itemPatch);

    List<ItemOwnerDto> getItemByUserId(Long userId, Integer from, Integer size);

    List<Item> searchItems(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, Comment comment);

    List<Item> getItemsByRequest(ItemRequest itemRequest);
}
