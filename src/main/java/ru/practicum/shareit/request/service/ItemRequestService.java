package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getMyItemRequests(Long userId);

    List<ItemRequestDto> getItemRequestFromUsers(Long userId, Integer from, Integer size);
}
