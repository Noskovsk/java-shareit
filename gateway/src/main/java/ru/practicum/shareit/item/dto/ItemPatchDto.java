package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.Size;

@Data
public class ItemPatchDto {
    @Size(min = 1)
    private String name;
    @Size(min = 1)
    private String description;
    private Boolean available;
    private ItemRequest request;
}
