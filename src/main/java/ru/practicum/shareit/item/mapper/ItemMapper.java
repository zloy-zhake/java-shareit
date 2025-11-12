package ru.practicum.shareit.item.mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(itemDto.getName());
        itemDto.setDescription(itemDto.getDescription());
        itemDto.setAvailable(itemDto.isAvailable());
        itemDto.setOwner(itemDto.getOwner());
        itemDto.setRequest(item.getRequest() != null ? item.getRequest() : null);
        return itemDto;
    }
}
