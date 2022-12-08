package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class ItemPatchDto {
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}
