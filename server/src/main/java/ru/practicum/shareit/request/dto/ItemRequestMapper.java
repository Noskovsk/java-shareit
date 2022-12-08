package ru.practicum.shareit.request.dto;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return MODEL_MAPPER.map(itemRequestDto, ItemRequest.class);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return MODEL_MAPPER.map(itemRequest, ItemRequestDto.class);
    }

}
