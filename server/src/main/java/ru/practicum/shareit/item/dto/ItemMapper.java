package ru.practicum.shareit.item.dto;

import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemMapper {
    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    public static Item toItem(ItemPatchDto itemPatchDto) {
        return MODEL_MAPPER.map(itemPatchDto, Item.class);
    }

    public static Item toItem(ItemOwnerDto itemOwnerDto) {
        return MODEL_MAPPER.map(itemOwnerDto, Item.class);
    }

    public static Item toItem(ItemCreateDto itemCreateDto) {
        Item item = MODEL_MAPPER.map(itemCreateDto, Item.class);
        if (itemCreateDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemCreateDto.getRequestId());
            item.setRequest(itemRequest);
        }
        return item;
    }

    public static ItemOwnerDto toItemOwnerDto(Item item) {
        return MODEL_MAPPER.map(item, ItemOwnerDto.class);
    }

    public static ItemCreateDto toItemCreateDto(Item item) {
        ItemCreateDto itemCreateDto = MODEL_MAPPER.map(item, ItemCreateDto.class);
        ItemRequest itemRequest = item.getRequest();
        if (itemRequest != null) {
            itemCreateDto.setRequestId(itemRequest.getId());
        }
        return itemCreateDto;
    }

    public static Item patchItem(Item patchItem, Item itemToBePatched) {
        MODEL_MAPPER.getConfiguration().setSkipNullEnabled(true);
        MODEL_MAPPER.map(patchItem, itemToBePatched);
        return itemToBePatched;
    }
}
